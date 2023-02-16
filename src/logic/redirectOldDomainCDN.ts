import { HOST_CURL, REGEXP_OLD_HOST_CURL } from "src/constants"

export function redirectOldDomainCDN(url: string): string {
  return url.replace(REGEXP_OLD_HOST_CURL, HOST_CURL)
}
