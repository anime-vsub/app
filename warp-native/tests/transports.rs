//! End-to-end transport tests.
//!
//! These hit the live Cloudflare WARP API and network (and register a device
//! on first run), so they are ignored by default. Run them on a host with:
//!
//! ```sh
//! cargo test --test transports -- --ignored --test-threads=1 --nocapture
//! ```
//!
//! Each test brings a transport up and makes a plain-HTTP request to
//! `www.cloudflare.com/cdn-cgi/trace` through the local SOCKS5 proxy. The
//! trace reports `warp=on` only when the request really left through WARP.

use std::io::{Read, Write};
use std::net::TcpStream;
use std::path::PathBuf;
use std::time::Duration;

use warp_native::{WarpMode, WarpTunnel};

fn state_dir() -> PathBuf {
    let dir = std::env::temp_dir().join("warp-native-tests");
    std::fs::create_dir_all(&dir).expect("create state dir");
    dir
}

fn trace_through_socks(port: u16) -> String {
    let host = "www.cloudflare.com";
    let mut stream = TcpStream::connect(("127.0.0.1", port)).expect("connect to SOCKS proxy");
    stream
        .set_read_timeout(Some(Duration::from_secs(30)))
        .unwrap();
    stream
        .set_write_timeout(Some(Duration::from_secs(30)))
        .unwrap();

    stream.write_all(&[0x05, 0x01, 0x00]).unwrap();
    let mut greeting = [0u8; 2];
    stream.read_exact(&mut greeting).unwrap();
    assert_eq!(greeting, [0x05, 0x00], "SOCKS greeting failed");

    let mut request = vec![0x05, 0x01, 0x00, 0x03, host.len() as u8];
    request.extend_from_slice(host.as_bytes());
    request.extend_from_slice(&80u16.to_be_bytes());
    stream.write_all(&request).unwrap();

    let mut reply = [0u8; 4];
    stream.read_exact(&mut reply).unwrap();
    assert_eq!(reply[0], 0x05, "unexpected SOCKS version");
    assert_eq!(reply[1], 0x00, "SOCKS CONNECT to {host}:80 failed");
    match reply[3] {
        0x01 => {
            let mut skip = [0u8; 6];
            stream.read_exact(&mut skip).unwrap();
        }
        0x04 => {
            let mut skip = [0u8; 18];
            stream.read_exact(&mut skip).unwrap();
        }
        0x03 => {
            let mut len = [0u8; 1];
            stream.read_exact(&mut len).unwrap();
            let mut skip = vec![0u8; len[0] as usize + 2];
            stream.read_exact(&mut skip).unwrap();
        }
        other => panic!("unexpected SOCKS address type {other}"),
    }

    write!(
        stream,
        "GET /cdn-cgi/trace HTTP/1.1\r\n\
         Host: {host}\r\n\
         Connection: close\r\n\
         User-Agent: warp-native-test\r\n\r\n"
    )
    .unwrap();

    let mut response = String::new();
    stream.read_to_string(&mut response).unwrap();
    response
}

fn assert_warp_works(mode: WarpMode) {
    let tunnel = WarpTunnel::new(state_dir().to_string_lossy().into_owned());
    let port = tunnel
        .start(mode)
        .unwrap_or_else(|err| panic!("{mode:?} start failed: {err}"));
    assert!(port > 0, "start returned no SOCKS port");

    let response = trace_through_socks(port);
    assert!(
        response.starts_with("HTTP/1.1 200"),
        "unexpected response through {mode:?}: {response}"
    );
    assert!(
        response.contains("warp=on"),
        "traffic did not leave through WARP ({mode:?}): {response}"
    );

    tunnel.stop();
}

#[test]
#[ignore = "hits the live WARP network"]
fn wireguard_carries_traffic() {
    assert_warp_works(WarpMode::WireGuard);
}

#[test]
#[ignore = "hits the live WARP network"]
fn masque_http2_carries_traffic() {
    assert_warp_works(WarpMode::MasqueHttp2);
}

#[test]
#[ignore = "hits the live WARP network; requires UDP/443 (HTTP/3)"]
fn masque_http3_carries_traffic() {
    assert_warp_works(WarpMode::MasqueHttp3);
}
