//! Small shared state used to report tunnel health back to Kotlin.

use std::sync::atomic::{AtomicBool, Ordering};
use std::sync::Mutex;

#[derive(Default)]
pub struct StatusCell {
    connected: AtomicBool,
    detail: Mutex<String>,
    last_error: Mutex<Option<String>>,
}

impl StatusCell {
    pub fn set_connected(&self, connected: bool) {
        self.connected.store(connected, Ordering::Relaxed);
    }

    pub fn is_connected(&self) -> bool {
        self.connected.load(Ordering::Relaxed)
    }

    pub fn set_detail(&self, detail: impl Into<String>) {
        if let Ok(mut guard) = self.detail.lock() {
            *guard = detail.into();
        }
    }

    pub fn detail(&self) -> String {
        self.detail.lock().map(|g| g.clone()).unwrap_or_default()
    }

    pub fn set_error(&self, error: Option<String>) {
        if let Ok(mut guard) = self.last_error.lock() {
            *guard = error;
        }
    }

    pub fn error(&self) -> Option<String> {
        self.last_error.lock().ok().and_then(|g| g.clone())
    }
}
