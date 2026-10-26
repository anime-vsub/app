//! Embedded Cloudflare WARP tunnel for AnimeVsub.
//!
//! The library exposes a tiny blocking API to Kotlin through uniffi. Calling
//! [`WarpTunnel::start`] brings up a WARP transport (WireGuard or MASQUE) and
//! returns the port of a local SOCKS5 proxy bound to `127.0.0.1`. The player
//! then uses an OkHttp client configured with `Proxy.Type.SOCKS`, so only
//! video traffic goes through Cloudflare: no `VpnService`, no VPN permission
//! and no effect on the rest of the app.

uniffi::setup_scaffolding!();

mod masque;
mod rt;
mod socks;
mod status;
mod wireguard;

use std::path::{Path, PathBuf};
use std::sync::{Arc, Mutex, Once};

use tokio::task::JoinHandle;
use tokio_util::sync::CancellationToken;

use crate::socks::Dialer;
use crate::status::StatusCell;

/// Which WARP transport to use.
#[derive(Debug, Clone, Copy, PartialEq, Eq, uniffi::Enum)]
pub enum WarpMode {
    /// Tunnel disabled; the player connects directly.
    Off,
    /// Classic WARP: userspace WireGuard over UDP/2408.
    WireGuard,
    /// WARP MASQUE: Connect-IP over QUIC (HTTP/3). Default MASQUE transport.
    MasqueHttp3,
    /// WARP MASQUE: Connect-IP over TCP (HTTP/2). For networks that block UDP.
    MasqueHttp2,
}

impl WarpMode {
    fn as_str(self) -> &'static str {
        match self {
            WarpMode::Off => "off",
            WarpMode::WireGuard => "wireguard",
            WarpMode::MasqueHttp3 => "masque-http3",
            WarpMode::MasqueHttp2 => "masque-http2",
        }
    }
}

#[derive(Debug, thiserror::Error, uniffi::Error)]
#[uniffi(flat_error)]
pub enum WarpError {
    #[error("{message}")]
    Failed { message: String },
}

impl From<anyhow::Error> for WarpError {
    fn from(err: anyhow::Error) -> Self {
        WarpError::Failed {
            message: format!("{err:#}"),
        }
    }
}

impl From<std::io::Error> for WarpError {
    fn from(err: std::io::Error) -> Self {
        WarpError::Failed {
            message: err.to_string(),
        }
    }
}

#[derive(uniffi::Record)]
pub struct WarpStatus {
    pub running: bool,
    pub port: u16,
    pub mode: WarpMode,
    pub connected: bool,
    pub detail: String,
    pub last_error: Option<String>,
}

struct Active {
    mode: WarpMode,
    port: u16,
    cancel: CancellationToken,
    status: Arc<StatusCell>,
    task: JoinHandle<()>,
}

#[derive(uniffi::Object)]
pub struct WarpTunnel {
    dir: PathBuf,
    active: Mutex<Option<Active>>,
}

#[uniffi::export]
impl WarpTunnel {
    /// `app_dir` must be a writable directory owned by the app (normally
    /// `context.filesDir`). Registrations and TLS temp files live there.
    #[uniffi::constructor]
    pub fn new(app_dir: String) -> Arc<Self> {
        let dir = PathBuf::from(app_dir);
        if let Err(err) = std::fs::create_dir_all(&dir) {
            log::error!("warp: cannot create {}: {err}", dir.display());
        }
        init_logging(&dir);

        Arc::new(Self {
            dir,
            active: Mutex::new(None),
        })
    }

    /// Brings the tunnel up and returns the local SOCKS5 port.
    ///
    /// This blocks until the transport is actually usable (registration,
    /// handshake and a probe connection), so a failure here means the network
    /// cannot reach Cloudflare with the selected transport.
    pub fn start(&self, mode: WarpMode) -> Result<u16, WarpError> {
        self.stop();
        if mode == WarpMode::Off {
            return Ok(0);
        }

        let dir = self.dir.clone();
        let status = Arc::new(StatusCell::default());
        let cancel = CancellationToken::new();

        let listener = std::net::TcpListener::bind(("127.0.0.1", 0))?;
        listener.set_nonblocking(true)?;
        let port = listener.local_addr()?.port();

        let status_task = Arc::clone(&status);
        let cancel_task = cancel.clone();

        log::info!(
            "warp: starting {} on 127.0.0.1:{port}",
            mode.as_str()
        );
        status.set_detail("Starting");

        let started = rt::block_on(async move {
            let dialer: Arc<dyn Dialer> = match mode {
                WarpMode::WireGuard => Arc::new(
                    wireguard::WgDialer::connect(&dir, Arc::clone(&status_task)).await?,
                ),
                WarpMode::MasqueHttp3 | WarpMode::MasqueHttp2 => Arc::new(
                    masque::MasqueDialer::connect(
                        &dir,
                        mode == WarpMode::MasqueHttp2,
                        Arc::clone(&status_task),
                        cancel_task.clone(),
                    )
                    .await?,
                ),
                WarpMode::Off => unreachable!(),
            };

            let cancel_serving = cancel_task.clone();
            let task = tokio::spawn(async move {
                socks::serve(listener, dialer, cancel_serving).await;
            });

            Ok::<JoinHandle<()>, anyhow::Error>(task)
        });

        match started {
            Ok(task) => {
                if let Ok(mut guard) = self.active.lock() {
                    *guard = Some(Active {
                        mode,
                        port,
                        cancel,
                        status,
                        task,
                    });
                }
                Ok(port)
            }
            Err(err) => {
                // The uniffi error reaches Kotlin, but logcat is where we
                // diagnose transport failures on real devices.
                log::error!("warp: {} start failed: {err:#}", mode.as_str());
                cancel.cancel();
                Err(err.into())
            }
        }
    }

    /// Tears the tunnel down. Safe to call when nothing is running.
    pub fn stop(&self) {
        let active = self.active.lock().ok().and_then(|mut guard| guard.take());
        if let Some(active) = active {
            active.cancel.cancel();
            active.task.abort();
            log::info!(
                "warp: stopped {} (port {})",
                active.mode.as_str(),
                active.port
            );
        }
    }

    pub fn status(&self) -> WarpStatus {
        let guard = match self.active.lock() {
            Ok(guard) => guard,
            Err(poisoned) => poisoned.into_inner(),
        };

        match guard.as_ref() {
            Some(active) => WarpStatus {
                running: !active.task.is_finished(),
                port: active.port,
                mode: active.mode,
                connected: active.status.is_connected(),
                detail: active.status.detail(),
                last_error: active.status.error(),
            },
            None => WarpStatus {
                running: false,
                port: 0,
                mode: WarpMode::Off,
                connected: false,
                detail: String::new(),
                last_error: None,
            },
        }
    }

    /// Forgets the cached device registrations. The next `start` registers a
    /// brand-new device with Cloudflare, which is also the escape hatch when
    /// a registration gets rejected or rate-limited.
    pub fn reset_credentials(&self) -> Result<(), WarpError> {
        self.stop();
        for name in ["warp-wireguard.json", "warp-masque.json"] {
            let path = self.dir.join(name);
            match std::fs::remove_file(&path) {
                Ok(()) => log::info!("warp: removed {}", path.display()),
                Err(err) if err.kind() == std::io::ErrorKind::NotFound => {}
                Err(err) => {
                    return Err(WarpError::Failed {
                        message: format!("cannot remove {}: {err}", path.display()),
                    })
                }
            }
        }
        Ok(())
    }
}

fn init_logging(dir: &Path) {
    static ONCE: Once = Once::new();
    ONCE.call_once(|| {
        // `usque-crypto` writes PEM material with `tempfile`, which honours
        // TMPDIR. Android has no writable /tmp, so point it at the app dir.
        std::env::set_var("TMPDIR", dir);

        #[cfg(target_os = "android")]
        {
            android_logger::init_once(
                android_logger::Config::default()
                    .with_max_level(log::LevelFilter::Info)
                    .with_tag("WarpNative"),
            );
        }
        #[cfg(not(target_os = "android"))]
        {
            let _ = env_logger::builder().is_test(false).try_init();
        }

        log::info!("warp-native ready (dir={})", dir.display());
    });
}
