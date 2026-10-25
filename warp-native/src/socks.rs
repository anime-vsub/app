//! Minimal SOCKS5 server (CONNECT only, no auth) exposing the WARP tunnel.
//!
//! The player's OkHttp client is pointed at `127.0.0.1:<port>` with
//! `Proxy.Type.SOCKS`. OkHttp hands us the *hostname* for `ATYP=DOMAIN`
//! requests, so DNS resolution happens inside the tunnel (no leak, and no
//! dependency on the ISP resolver for blocked hosts).

use std::io;
use std::net::Ipv4Addr;
use std::sync::Arc;

use async_trait::async_trait;
use tokio::io::{AsyncReadExt, AsyncWriteExt};
use tokio::net::{TcpListener, TcpStream};
use tokio_util::sync::CancellationToken;

/// A byte stream whose bytes travel through the WARP tunnel.
#[async_trait]
pub trait TunnelStream: Send {
    async fn read_some(&mut self, buf: &mut [u8]) -> io::Result<usize>;
    async fn write_all(&mut self, buf: &[u8]) -> io::Result<()>;
    async fn shutdown(&mut self);
}

/// Opens TCP connections through the active tunnel.
#[async_trait]
pub trait Dialer: Send + Sync {
    async fn dial(&self, host: &str, port: u16) -> anyhow::Result<Box<dyn TunnelStream>>;
}

const REPLY_SUCCEEDED: u8 = 0x00;
const REPLY_GENERAL_FAILURE: u8 = 0x01;
const REPLY_COMMAND_NOT_SUPPORTED: u8 = 0x07;
const REPLY_ADDRESS_TYPE_NOT_SUPPORTED: u8 = 0x08;

pub async fn serve(listener: std::net::TcpListener, dialer: Arc<dyn Dialer>, cancel: CancellationToken) {
    listener.set_nonblocking(true).ok();
    let listener = match TcpListener::from_std(listener) {
        Ok(listener) => listener,
        Err(err) => {
            log::error!("warp: cannot adopt SOCKS listener: {err}");
            return;
        }
    };

    loop {
        tokio::select! {
            _ = cancel.cancelled() => break,
            accepted = listener.accept() => {
                let (socket, _peer) = match accepted {
                    Ok(pair) => pair,
                    Err(err) => {
                        log::warn!("warp: SOCKS accept failed: {err}");
                        continue;
                    }
                };
                let _ = socket.set_nodelay(true);
                let dialer = Arc::clone(&dialer);
                tokio::spawn(async move {
                    if let Err(err) = handle(socket, dialer).await {
                        log::debug!("warp: SOCKS session ended: {err}");
                    }
                });
            }
        }
    }

    log::info!("warp: SOCKS server stopped");
}

async fn handle(mut socket: TcpStream, dialer: Arc<dyn Dialer>) -> io::Result<()> {
    // Greeting: VER, NMETHODS, METHODS...
    let mut greeting = [0u8; 2];
    socket.read_exact(&mut greeting).await?;
    if greeting[0] != 0x05 {
        return Ok(());
    }
    let mut methods = vec![0u8; greeting[1] as usize];
    socket.read_exact(&mut methods).await?;
    // "No authentication required".
    socket.write_all(&[0x05, 0x00]).await?;

    // Request: VER, CMD, RSV, ATYP
    let mut request = [0u8; 4];
    socket.read_exact(&mut request).await?;
    if request[0] != 0x05 {
        return Ok(());
    }
    if request[1] != 0x01 {
        reply(&mut socket, REPLY_COMMAND_NOT_SUPPORTED).await?;
        return Ok(());
    }

    let host = match request[3] {
        0x01 => {
            let mut raw = [0u8; 4];
            socket.read_exact(&mut raw).await?;
            Ipv4Addr::from(raw).to_string()
        }
        0x04 => {
            let mut raw = [0u8; 16];
            socket.read_exact(&mut raw).await?;
            std::net::Ipv6Addr::from(raw).to_string()
        }
        0x03 => {
            let mut len = [0u8; 1];
            socket.read_exact(&mut len).await?;
            let mut domain = vec![0u8; len[0] as usize];
            socket.read_exact(&mut domain).await?;
            match String::from_utf8(domain) {
                Ok(domain) => domain,
                Err(_) => {
                    reply(&mut socket, REPLY_GENERAL_FAILURE).await?;
                    return Ok(());
                }
            }
        }
        _ => {
            reply(&mut socket, REPLY_ADDRESS_TYPE_NOT_SUPPORTED).await?;
            return Ok(());
        }
    };

    let mut port = [0u8; 2];
    socket.read_exact(&mut port).await?;
    let port = u16::from_be_bytes(port);

    match dialer.dial(&host, port).await {
        Ok(mut remote) => {
            reply(&mut socket, REPLY_SUCCEEDED).await?;
            relay(socket, remote.as_mut()).await;
            remote.shutdown().await;
        }
        Err(err) => {
            log::warn!("warp: cannot open {host}:{port} through tunnel: {err:#}");
            reply(&mut socket, REPLY_GENERAL_FAILURE).await?;
        }
    }
    Ok(())
}

async fn reply(socket: &mut TcpStream, code: u8) -> io::Result<()> {
    // BND.ADDR/BND.PORT are meaningless for us; OkHttp ignores them.
    socket
        .write_all(&[0x05, code, 0x00, 0x01, 0, 0, 0, 0, 0, 0])
        .await
}

async fn relay(mut client: TcpStream, remote: &mut dyn TunnelStream) {
    let mut client_buf = vec![0u8; 32 * 1024];
    let mut remote_buf = vec![0u8; 32 * 1024];

    loop {
        tokio::select! {
            read = client.read(&mut client_buf) => {
                match read {
                    Ok(0) | Err(_) => break,
                    Ok(n) => {
                        if remote.write_all(&client_buf[..n]).await.is_err() {
                            break;
                        }
                    }
                }
            }
            read = remote.read_some(&mut remote_buf) => {
                match read {
                    Ok(0) | Err(_) => break,
                    Ok(n) => {
                        if client.write_all(&remote_buf[..n]).await.is_err() {
                            break;
                        }
                    }
                }
            }
        }
    }

    let _ = client.shutdown().await;
}
