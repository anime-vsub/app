export function getPathName(url: string) {
  try {
    return new URL(
      url,
      typeof location === "undefined" ? undefined : location.href
    ).pathname
  } catch (err) {
    return url
  }
}
