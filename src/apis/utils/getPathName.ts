export function getPathName(url: string) {
  try {
    return new URL(url, location.href).pathname
  } catch {
    return url
  }
}
