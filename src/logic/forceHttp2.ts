export function forceHttp2(url: string): string {
  if (url.startsWith("http://")) return "https" + url.slice(4)

  return url
}
