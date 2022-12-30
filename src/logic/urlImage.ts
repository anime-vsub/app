import { HOST_CURL } from "src/constants"

const HOST_URL_IMAGE_REGEX = new RegExp(
  "https?:\\/\\/([^/]+.)?" + HOST_CURL + "(?=(?:\\:\\d+)?\\/)",
  "i"
)

export function removeHostUrlImage(url: string): string {
  return url.replace(HOST_URL_IMAGE_REGEX, "$1$@")
}
export function addHostUrlImage(url: string): string {
  return url.replace(
    /^([^/]+.)?\$@(:\d+)?(?=\/)/,
    location.protocol + "//$1" + location.hostname + "$2"
  )
}
