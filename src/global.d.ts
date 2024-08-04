declare global {
  interface Window {
    CapacitorWebFetch: typeof fetch
  }
}
