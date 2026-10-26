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

use anyhow::{anyhow, Context, Result};
use async_trait::async_trait;
use tokio::sync::{Mutex, RwLock};
use warp_wireguard_gen::{get_config, register, RegistrationOptions, WarpCredentials};
use wireguard_netstack::{DohResolver, ManagedTunnel, NetStack, TcpConnection};

use crate::socks::{Dialer, TunnelStream};
use crate::status::StatusCell;

/// WARP's WireGuard peer needs a conservative MTU. `wireguard-netstack`
/// defaults to 460 which would cap throughput badly.
const MTU: u16 = 1280;
const CREDENTIALS_FILE: &str = "warp-wireguard.json";

// `warp-wireguard-gen` and `wireguard-netstack` have no internal network
// timeouts. Without these, one dead path leaves `start()` or a SOCKS dial
// hanging indefinitely instead of failing fast and letting the caller retry.
const API_TIMEOUT: Duration = Duration::from_secs(20);
const DNS_TIMEOUT: Duration = Duration::from_secs(6);
const DIAL_TIMEOUT: Duration = Duration::from_secs(10);
const HANDSHAKE_TIMEOUT: Duration = Duration::from_secs(8);
const PROBE_TIMEOUT: Duration = Duration::from_secs(15);

/// Extra anycast edges to try when the registered endpoint drops the
/// handshake. WARP hands out one edge IP, but a single edge can silently
/// blackhole UDP for minutes while the rest of Cloudflare's network is fine.
const FALLBACK_ENDPOINTS: [&str; 4] = [
    "162.159.192.1",
    "162.159.195.1",
    "162.159.193.1",
    "162.159.204.1",
];

pub struct WgDialer {
    inner: RwLock<Inner>,
    dir: PathBuf,
    status: Arc<StatusCell>,
    /// Serializes rebuilds: several SOCKS dials can fail at once, and each
    /// one rebuilding independently only produced registration/handshake
    /// storms that made the intermittent timeouts worse.
    rebuild: Mutex<()>,
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
            rebuild: Mutex::new(()),
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
        Some(credentials) => match tokio::time::timeout(API_TIMEOUT, get_config(credentials)).await
        {
            Ok(Ok(config)) => Some(config),
            Ok(Err(err)) => {
                log::warn!(
                    "warp: cached WireGuard registration rejected ({err:#}), re-registering"
                );
                None
            }
            Err(_) => {
                log::warn!(
                    "warp: WARP config refresh timed out after {}s, re-registering",
                    API_TIMEOUT.as_secs()
                );
                None
            }
        },
        None => None,
    };

    if config.is_none() {
        status.set_detail("Registering with Cloudflare WARP");
        let (fresh_config, fresh_credentials) =
            tokio::time::timeout(API_TIMEOUT, register(RegistrationOptions::default()))
                .await
                .map_err(|_| {
                    anyhow!(
                        "WARP device registration timed out after {}s",
                        API_TIMEOUT.as_secs()
                    )
                })?
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
    let mut endpoints = vec![config.peer_endpoint];
    for raw in FALLBACK_ENDPOINTS {
        if let Ok(ip) = raw.parse::<IpAddr>() {
            let candidate = SocketAddr::new(ip, config.peer_endpoint.port());
            if !endpoints.contains(&candidate) {
                endpoints.push(candidate);
            }
        }
    }

    let mut tunnel = None;
    for (index, endpoint) in endpoints.iter().enumerate() {
        config.peer_endpoint = *endpoint;
        log::info!(
            "warp: WireGuard handshake with {endpoint} (attempt {}/{})",
            index + 1,
            endpoints.len()
        );
        match ManagedTunnel::connect_with_timeout(config.clone(), HANDSHAKE_TIMEOUT).await {
            Ok(established) => {
                tunnel = Some(established);
                break;
            }
            Err(err) => {
                log::warn!("warp: WireGuard handshake with {endpoint} failed: {err}");
            }
        }
    }
    let tunnel = tunnel.ok_or_else(|| {
        anyhow!(
            "WireGuard handshake with Cloudflare failed on all {} endpoints",
            endpoints.len()
        )
    })?;

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
    let addr =
        match tokio::time::timeout(DNS_TIMEOUT, dns.resolve_addr("cloudflare.com", 443)).await {
            Ok(result) => result.context("DNS through the WireGuard tunnel failed")?,
            Err(_) => {
                return Err(anyhow!(
                    "DNS through the WireGuard tunnel timed out after {}s",
                    DNS_TIMEOUT.as_secs()
                ))
            }
        };
    let connect = TcpConnection::connect(Arc::clone(netstack), addr);
    match tokio::time::timeout(PROBE_TIMEOUT, connect).await {
        Ok(Ok(connection)) => {
            connection.shutdown();
            Ok(())
        }
        Ok(Err(err)) => Err(anyhow!("tunnel probe failed: {err}")),
        Err(_) => Err(anyhow!("tunnel probe timed out")),
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
                    log::warn!("warp: WireGuard dial to {host}:{port} failed ({err:#})");
                }
            }
        }

        // Only one dial rebuilds at a time. While waiting, another dial may
        // have already restored the tunnel, so retry before rebuilding again.
        let _rebuild_guard = self.rebuild.lock().await;
        {
            let guard = self.inner.read().await;
            if let Ok(stream) = dial_with(&guard, host, port).await {
                return Ok(stream);
            }
        }

        log::info!("warp: rebuilding WireGuard tunnel after dial failure");
        let rebuilt = self.rebuild().await?;
        *self.inner.write().await = rebuilt;

        let guard = self.inner.read().await;
        dial_with(&guard, host, port).await.map_err(Into::into)
    }
}

async fn dial_with(inner: &Inner, host: &str, port: u16) -> Result<Box<dyn TunnelStream>> {
    let addr = match host.parse::<IpAddr>() {
        Ok(ip) => SocketAddr::new(ip, port),
        Err(_) => match tokio::time::timeout(DNS_TIMEOUT, inner.dns.resolve_addr(host, port)).await
        {
            Ok(result) => result.with_context(|| format!("cannot resolve {host} through WARP"))?,
            Err(_) => {
                return Err(anyhow!(
                    "resolving {host} through WARP timed out after {}s",
                    DNS_TIMEOUT.as_secs()
                ))
            }
        },
    };

    let connection = match tokio::time::timeout(
        DIAL_TIMEOUT,
        TcpConnection::connect(Arc::clone(&inner.netstack), addr),
    )
    .await
    {
        Ok(result) => result?,
        Err(_) => {
            return Err(anyhow!(
                "connecting to {host}:{port} through WARP timed out after {}s",
                DIAL_TIMEOUT.as_secs()
            ))
        }
    };
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
