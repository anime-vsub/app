//! Classic WARP transport: userspace WireGuard over UDP/2408.
//!
//! Uses `wireguard-netstack` (gotatun + smoltcp) so no `VpnService` and no
//! system-wide routing is involved: only the sockets created here travel
//! through Cloudflare.

use std::io;
use std::net::{IpAddr, SocketAddr};
use std::path::{Path, PathBuf};
use std::sync::Arc;
use std::time::Duration;

use anyhow::{Context, Result};
use async_trait::async_trait;
use tokio::sync::RwLock;
use warp_wireguard_gen::{get_config, register, RegistrationOptions, WarpCredentials};
use wireguard_netstack::{DohResolver, ManagedTunnel, NetStack, TcpConnection};

use crate::socks::{Dialer, TunnelStream};
use crate::status::StatusCell;

/// WARP's WireGuard peer needs a conservative MTU. `wireguard-netstack`
/// defaults to 460 which would cap throughput badly.
const MTU: u16 = 1280;
const CREDENTIALS_FILE: &str = "warp-wireguard.json";

pub struct WgDialer {
    inner: RwLock<Inner>,
    dir: PathBuf,
    status: Arc<StatusCell>,
}

struct Inner {
    netstack: Arc<NetStack>,
    dns: DohResolver,
    // Kept alive so the tunnel's background tasks (and its `Drop`) stay valid.
    _tunnel: ManagedTunnel,
}

impl WgDialer {
    pub async fn connect(dir: &Path, status: Arc<StatusCell>) -> Result<Self> {
        let inner = build(dir, &status).await?;
        status.set_connected(true);
        status.set_error(None);
        status.set_detail("WireGuard tunnel up");

        Ok(Self {
            inner: RwLock::new(inner),
            dir: dir.to_path_buf(),
            status,
        })
    }

    async fn rebuild(&self) -> Result<Inner> {
        self.status.set_connected(false);
        let inner = build(&self.dir, &self.status).await?;
        self.status.set_connected(true);
        self.status.set_error(None);
        self.status.set_detail("WireGuard tunnel re-established");
        Ok(inner)
    }
}

async fn build(dir: &Path, status: &Arc<StatusCell>) -> Result<Inner> {
    let path = dir.join(CREDENTIALS_FILE);
    let credentials = load_credentials(&path).await;

    let mut config = match credentials.as_ref() {
        Some(credentials) => match get_config(credentials).await {
            Ok(config) => Some(config),
            Err(err) => {
                log::warn!("warp: cached WireGuard registration rejected ({err:#}), re-registering");
                None
            }
        },
        None => None,
    };

    if config.is_none() {
        status.set_detail("Registering with Cloudflare WARP");
        let (fresh_config, fresh_credentials) = register(RegistrationOptions::default())
            .await
            .context("WARP device registration failed")?;
        if let Err(err) = save_credentials(&path, &fresh_credentials).await {
            log::warn!("warp: cannot persist WireGuard credentials: {err:#}");
        }
        config = Some(fresh_config);
    }

    let mut config = config.expect("config is always set above");
    config.mtu = Some(MTU);
    if config.keepalive_seconds.unwrap_or(0) == 0 {
        // Keeps NAT bindings alive and lets us notice dead paths.
        config.keepalive_seconds = Some(25);
    }

    status.set_detail("Establishing WireGuard handshake");
    let tunnel = ManagedTunnel::connect_with_timeout(config, Duration::from_secs(15))
        .await
        .context("WireGuard handshake with Cloudflare failed")?;

    let netstack = tunnel.netstack();
    let dns = DohResolver::new_tunneled(Arc::clone(&netstack));

    // Fail fast when UDP/2408 is blackholed instead of handing the player a
    // proxy that cannot resolve anything.
    probe(&netstack, &dns).await?;

    Ok(Inner {
        netstack,
        dns,
        _tunnel: tunnel,
    })
}

async fn probe(netstack: &Arc<NetStack>, dns: &DohResolver) -> Result<()> {
    let addr = dns
        .resolve_addr("cloudflare.com", 443)
        .await
        .context("DNS through the WireGuard tunnel failed")?;
    let connect = TcpConnection::connect(Arc::clone(netstack), addr);
    match tokio::time::timeout(Duration::from_secs(12), connect).await {
        Ok(Ok(connection)) => {
            connection.shutdown();
            Ok(())
        }
        Ok(Err(err)) => Err(anyhow::anyhow!("tunnel probe failed: {err}")),
        Err(_) => Err(anyhow::anyhow!("tunnel probe timed out")),
    }
}

async fn load_credentials(path: &Path) -> Option<WarpCredentials> {
    let raw = tokio::fs::read_to_string(path).await.ok()?;
    match serde_json::from_str(&raw) {
        Ok(credentials) => Some(credentials),
        Err(err) => {
            log::warn!("warp: ignoring corrupt WireGuard credentials: {err}");
            None
        }
    }
}

async fn save_credentials(path: &Path, credentials: &WarpCredentials) -> Result<()> {
    let raw = serde_json::to_string_pretty(credentials)?;
    tokio::fs::write(path, raw).await?;
    Ok(())
}

#[async_trait]
impl Dialer for WgDialer {
    async fn dial(&self, host: &str, port: u16) -> Result<Box<dyn TunnelStream>> {
        {
            let guard = self.inner.read().await;
            match dial_with(&guard, host, port).await {
                Ok(stream) => return Ok(stream),
                Err(err) => {
                    log::warn!("warp: WireGuard dial to {host}:{port} failed ({err:#}), reconnecting");
                }
            }
        }

        let rebuilt = self.rebuild().await?;
        *self.inner.write().await = rebuilt;

        let guard = self.inner.read().await;
        dial_with(&guard, host, port).await.map_err(Into::into)
    }
}

async fn dial_with(inner: &Inner, host: &str, port: u16) -> Result<Box<dyn TunnelStream>> {
    let addr = match host.parse::<IpAddr>() {
        Ok(ip) => SocketAddr::new(ip, port),
        Err(_) => inner
            .dns
            .resolve_addr(host, port)
            .await
            .with_context(|| format!("cannot resolve {host} through WARP"))?,
    };

    let connection = TcpConnection::connect(Arc::clone(&inner.netstack), addr).await?;
    Ok(Box::new(WgStream { connection }))
}

struct WgStream {
    connection: TcpConnection,
}

#[async_trait]
impl TunnelStream for WgStream {
    async fn read_some(&mut self, buf: &mut [u8]) -> io::Result<usize> {
        self.connection
            .read(buf)
            .await
            .map_err(|err| io::Error::new(io::ErrorKind::Other, err.to_string()))
    }

    async fn write_all(&mut self, buf: &[u8]) -> io::Result<()> {
        self.connection
            .write_all(buf)
            .await
            .map_err(|err| io::Error::new(io::ErrorKind::Other, err.to_string()))
    }

    async fn shutdown(&mut self) {
        self.connection.shutdown();
    }
}
