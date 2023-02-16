import { HOST_CURL } from "src/constants"

import { redirectOldDomainCDN } from "./redirectOldDomainCDN"

const HOST_URL_IMAGE_REGEX = new RegExp(
  "https?:\\/\\/([^/]+.)?" + HOST_CURL + "(?=(?:\\:\\d+)?\\/)",
  "i"
)

export function removeHostUrlImage(url: string): string {
  return redirectOldDomainCDN(url).replace(HOST_URL_IMAGE_REGEX, "$1$@")
}
export function addHostUrlImage(url: string): string {
  // for old data from database
  return redirectOldDomainCDN(url).replace(
    /^([^/]+.)?\$@(:\d+)?(?=\/)/,
    "https:" + "//$1" + HOST_CURL + "$2"
  )
}
