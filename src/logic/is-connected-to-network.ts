export async function isConnectedToNetwork(): Promise<boolean> {
  if (!navigator.onLine) return false

  try {
    const response = await CapacitorWebFetch("https://1.1.1.1/cdn-cgi/trace")
    return response.ok
  } catch {
    return false
  }
}
