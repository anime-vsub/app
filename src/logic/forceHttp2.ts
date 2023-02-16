import { redirectOldDomainCDN } from "./redirectOldDomainCDN"

/**
 * @description: this function has been information through new domain
 */
export function forceHttp2(url: string): string {
  url = redirectOldDomainCDN(url)

  if (url.startsWith("http://")) return "https" + url.slice(4)

  return url
}
