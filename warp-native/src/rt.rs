//! Shared Tokio runtime.
//!
//! The tunnel lives for the whole process, so a single multi-threaded runtime
//! is created lazily and reused by every blocking entry point exposed over FFI.

use std::sync::OnceLock;

use tokio::runtime::Runtime;

static RUNTIME: OnceLock<Runtime> = OnceLock::new();

pub fn runtime() -> &'static Runtime {
    RUNTIME.get_or_init(|| {
        tokio::runtime::Builder::new_multi_thread()
            .worker_threads(2)
            .thread_name("warp-native")
            .enable_all()
            .build()
            .expect("failed to build the WARP runtime")
    })
}

pub fn block_on<F>(future: F) -> F::Output
where
    F: std::future::Future,
{
    runtime().block_on(future)
}
