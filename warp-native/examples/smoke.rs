//! Manual smoke test: bring a transport up and print the SOCKS port.
//!
//! ```sh
//! cargo run --example smoke -- wg       # WireGuard
//! cargo run --example smoke -- h3       # MASQUE over HTTP/3
//! cargo run --example smoke -- h2       # MASQUE over HTTP/2
//! ```

fn main() {
    let dir = std::env::temp_dir().join("warp-native-smoke");
    std::fs::create_dir_all(&dir).expect("create state dir");

    let tunnel = warp_native::WarpTunnel::new(dir.to_string_lossy().into_owned());
    let mode = match std::env::args().nth(1).as_deref() {
        Some("wg") => warp_native::WarpMode::WireGuard,
        Some("h2") => warp_native::WarpMode::MasqueHttp2,
        _ => warp_native::WarpMode::MasqueHttp3,
    };

    println!("starting {mode:?}");
    match tunnel.start(mode) {
        Ok(port) => {
            println!("OK port={port}");
            println!("SOCKS5 on 127.0.0.1:{port}");
            loop {
                std::thread::sleep(std::time::Duration::from_secs(5));
                println!("status={:?}", tunnel.status().detail);
            }
        }
        Err(err) => {
            println!("ERR {err:?}");
            std::process::exit(1);
        }
    }
}
