//! MASQUE transport: Connect-IP (RFC 9484) over HTTP/3 (QUIC) or HTTP/2.
//!
//! Cloudflare's MASQUE mode survives networks that block UDP/2408, which is
//! the main reason it is offered next to WireGuard. This module drives the
//! `usque-rs` crates (MIT) and exposes the tunnel as a SOCKS5 upstream.

use std::io;
use std::net::{IpAddr, Ipv4Addr, SocketAddr};
use std::path::Path;
use std::sync::Arc;
use std::time::Duration;

use anyhow::{anyhow, Context, Result};
use async_trait::async_trait;
use base64::engine::general_purpose::STANDARD;
use base64::Engine as _;
use bytes::BytesMut;
use tokio::task::JoinHandle;
use tokio_util::sync::CancellationToken;
use usque_cloudflare_api::{
    enroll_key, register, CloudflareClient, CONNECT_SNI, CONNECT_URI, DEFAULT_LOCALE, DEFAULT_MODEL,
};
use usque_config::{
    parse_endpoint_v4, parse_endpoint_v6, select_endpoint, Config, DEFAULT_ENDPOINT_H2_V4,
};
use usque_crypto::{
    build_rustls_config, decode_endpoint_public_key, decode_private_key, generate_ec_keypair,
    prepare_quiche_client_credentials, TlsOptions,
};
use usque_masque::{connect_tunnel, ConnectOptions, PacketSession};
use usque_tunnel_core::TunnelDevice;
use usque_virtual_net::dns::DnsResolver;
use usque_virtual_net::{ChannelDevice, VirtualStack, VirtualTcpStream};

use crate::socks::{Dialer, TunnelStream};
use crate::status::StatusCell;

const CONFIG_FILE: &str = "warp-masque.json";
/// Largest IP packet that reliably fits a single QUIC datagram on WARP.
const MTU: usize = 1400;
const CONNECT_PORT: u16 = 443;

pub struct MasqueDialer {
    stack: Arc<VirtualStack>,
    dns: DnsResolver,
    _supervisor: JoinHandle<()>,
}

impl MasqueDialer {
    pub async fn connect(
        dir: &Path,
        http2: bool,
        status: Arc<StatusCell>,
        cancel: CancellationToken,
    ) -> Result<Self> {
        let config = ensure_config(dir).await?;
        let options = build_connect_options(&config, http2)?;

        let (device, to_device, from_device) = ChannelDevice::pair();
        let device = Arc::new(device);
        // The activity handle lets SOCKS-side wakeups trigger a reconnect
        // after the tunnel went idle, instead of waiting for the next packet.
        let activity = Arc::new(tokio::sync::Notify::new());

        let local_v4: IpAddr = config
            .ipv4
            .parse()
            .with_context(|| format!("invalid IPv4 address in WARP config: {:?}", config.ipv4))?;
        let local_v6: IpAddr = config
            .ipv6
            .parse()
            .with_context(|| format!("invalid IPv6 address in WARP config: {:?}", config.ipv6))?;

        let stack = Arc::new(VirtualStack::start(
            Some(local_v4),
            Some(local_v6),
            MTU,
            from_device,
            to_device,
            activity,
        ));

        let dns = DnsResolver {
            servers: vec![
                IpAddr::V4(Ipv4Addr::new(1, 1, 1, 1)),
                IpAddr::V4(Ipv4Addr::new(1, 0, 0, 1)),
            ],
            timeout: Duration::from_secs(3),
            use_os_resolver: false,
            local_dns: false,
            stack: Some(Arc::clone(&stack)),
        };

        let supervisor = tokio::spawn(supervise(
            options,
            Arc::clone(&device),
            Arc::clone(&stack),
            Arc::clone(&status),
            cancel,
        ));

        probe(&stack, &dns).await?;

        status.set_connected(true);
        status.set_error(None);
        status.set_detail(if http2 {
            "MASQUE (HTTP/2) tunnel up"
        } else {
            "MASQUE (HTTP/3) tunnel up"
        });

        Ok(Self {
            stack,
            dns,
            _supervisor: supervisor,
        })
    }
}

#[async_trait]
impl Dialer for MasqueDialer {
    async fn dial(&self, host: &str, port: u16) -> Result<Box<dyn TunnelStream>> {
        let addr = match host.parse::<IpAddr>() {
            Ok(ip) => SocketAddr::new(ip, port),
            Err(_) => {
                let ip = self
                    .dns
                    .lookup_ip(host)
                    .await
                    .with_context(|| format!("cannot resolve {host} through MASQUE"))?;
                SocketAddr::new(ip, port)
            }
        };

        let stream = self.stack.dial_tcp(addr).await?;
        Ok(Box::new(MasqueStream { stream }))
    }
}

struct MasqueStream {
    stream: VirtualTcpStream,
}

#[async_trait]
impl TunnelStream for MasqueStream {
    async fn read_some(&mut self, buf: &mut [u8]) -> io::Result<usize> {
        tokio::io::AsyncReadExt::read(&mut self.stream, buf).await
    }

    async fn write_all(&mut self, buf: &[u8]) -> io::Result<()> {
        tokio::io::AsyncWriteExt::write_all(&mut self.stream, buf).await
    }

    async fn shutdown(&mut self) {
        let _ = tokio::io::AsyncWriteExt::shutdown(&mut self.stream).await;
    }
}

async fn ensure_config(dir: &Path) -> Result<Config> {
    let path = dir.join(CONFIG_FILE);
    if let Ok(config) = Config::load(&path) {
        return Ok(config);
    }

    log::info!("warp: registering a new MASQUE device with Cloudflare");
    let client = CloudflareClient::new();
    let account = register(&client, DEFAULT_MODEL, DEFAULT_LOCALE, None, true)
        .await
        .context("WARP registration failed")?;

    let (private_key, public_key) = generate_ec_keypair()?;
    let access_token = account.token.clone().unwrap_or_default();
    let enrolled = enroll_key(&client, &account.id, &access_token, &public_key, "")
        .await
        .map_err(|err| anyhow!("{err}"))
        .context("WARP MASQUE enrollment failed")?;

    let peer = enrolled
        .config
        .peers
        .first()
        .ok_or_else(|| anyhow!("WARP enrollment returned no peer"))?;

    let config = Config {
        private_key: STANDARD.encode(private_key),
        endpoint_v4: parse_endpoint_v4(&peer.endpoint.v4),
        endpoint_v6: parse_endpoint_v6(&peer.endpoint.v6),
        endpoint_h2_v4: DEFAULT_ENDPOINT_H2_V4.to_string(),
        endpoint_h2_v6: String::new(),
        endpoint_pub_key: peer.public_key.clone(),
        license: enrolled.account.license,
        id: enrolled.id,
        access_token,
        ipv4: enrolled.config.interface.addresses.v4,
        ipv6: enrolled.config.interface.addresses.v6,
    };

    if let Err(err) = config.save(&path) {
        log::warn!("warp: cannot persist MASQUE registration: {err:#}");
    }
    Ok(config)
}

fn build_connect_options(config: &Config, http2: bool) -> Result<ConnectOptions> {
    let signing_key = decode_private_key(&config.private_key)?;
    let peer_public_key = decode_endpoint_public_key(&config.endpoint_pub_key)?;

    let alpn = if http2 {
        vec![b"h2".to_vec()]
    } else {
        vec![b"h3".to_vec()]
    };

    let tls_config = build_rustls_config(
        &signing_key,
        TlsOptions {
            sni: CONNECT_SNI.to_string(),
            insecure: false,
            peer_public_key: peer_public_key.clone(),
            alpn,
        },
    )?;

    let quiche_credentials = Arc::new(prepare_quiche_client_credentials(&signing_key)?);
    let endpoint = select_endpoint(config, http2, false, CONNECT_PORT)?;

    log::info!("warp: MASQUE endpoint {endpoint} (http2={http2})");

    Ok(ConnectOptions {
        tls_config,
        quiche_credentials,
        insecure: false,
        peer_public_key,
        endpoint,
        sni: CONNECT_SNI.to_string(),
        use_http2: http2,
        keepalive_period: Duration::from_secs(30),
        initial_packet_size: 0,
        connect_uri: CONNECT_URI.to_string(),
    })
}

async fn supervise(
    options: ConnectOptions,
    device: Arc<ChannelDevice>,
    stack: Arc<VirtualStack>,
    status: Arc<StatusCell>,
    cancel: CancellationToken,
) {
    loop {
        if cancel.is_cancelled() {
            break;
        }

        status.set_detail("Connecting MASQUE tunnel");
        match connect_tunnel(&options).await {
            Ok(mut session) => {
                status.set_connected(true);
                status.set_error(None);
                status.set_detail("MASQUE tunnel up");
                log::info!("warp: MASQUE tunnel established");

                let reason = pump(&device, session.as_mut(), &cancel).await;

                status.set_connected(false);
                status.set_detail(format!("MASQUE tunnel lost: {reason}"));
                log::warn!("warp: MASQUE tunnel lost: {reason}");
                let _ = session.close().await;
            }
            Err(err) => {
                status.set_connected(false);
                status.set_error(Some(format!("{err:#}")));
                status.set_detail("MASQUE connect failed, retrying");
                log::warn!("warp: MASQUE connect failed: {err:#}");
            }
        }

        stack.wake();
        tokio::select! {
            _ = cancel.cancelled() => break,
            _ = tokio::time::sleep(Duration::from_secs(2)) => {}
        }
    }

    log::info!("warp: MASQUE supervisor stopped");
}

/// Bidirectional packet pump between the smoltcp stack and the MASQUE session.
async fn pump(
    device: &Arc<ChannelDevice>,
    session: &mut dyn PacketSession,
    cancel: &CancellationToken,
) -> String {
    let mut buffer = BytesMut::with_capacity(MTU.max(2048));

    loop {
        tokio::select! {
            _ = cancel.cancelled() => return "cancelled".to_string(),
            read = async {
                buffer.clear();
                device.read_packet(&mut buffer).await
            } => {
                match read {
                    Ok(0) => {}
                    Ok(n) => {
                        if !is_ip_packet(&buffer[..n]) {
                            continue;
                        }
                        if usque_masque::decrement_ttl(&mut buffer[..n]).is_err() {
                            continue;
                        }
                        match session.write_packet(&buffer[..n]).await {
                            Ok(Some(icmp)) => {
                                let _ = device.write_packet(icmp).await;
                            }
                            Ok(None) => {}
                            Err(err) => return format!("write failed: {err}"),
                        }
                    }
                    Err(err) => return format!("stack closed: {err}"),
                }
            }
            packet = session.read_packet() => {
                match packet {
                    Ok(Some(packet)) => {
                        if device.write_packet(packet).await.is_err() {
                            return "stack write failed".to_string();
                        }
                    }
                    Ok(None) => return "session closed".to_string(),
                    Err(err) => return format!("session error: {err}"),
                }
            }
        }
    }
}

fn is_ip_packet(packet: &[u8]) -> bool {
    if packet.is_empty() {
        return false;
    }
    match packet[0] >> 4 {
        4 => packet.len() >= 20,
        6 => packet.len() >= 40,
        _ => false,
    }
}

/// Opens a throwaway TCP connection through the tunnel so `start()` can tell
/// the caller whether WARP actually works on this network.
async fn probe(stack: &Arc<VirtualStack>, dns: &DnsResolver) -> Result<()> {
    let attempt = async {
        let ip = dns
            .lookup_ip("cloudflare.com")
            .await
            .context("DNS through the MASQUE tunnel failed")?;
        let stream = stack
            .dial_tcp(SocketAddr::new(ip, CONNECT_PORT))
            .await
            .context("TCP through the MASQUE tunnel failed")?;
        drop(stream);
        Ok::<(), anyhow::Error>(())
    };

    match tokio::time::timeout(Duration::from_secs(35), attempt).await {
        Ok(result) => result,
        Err(_) => Err(anyhow!("MASQUE tunnel probe timed out")),
    }
}
