export const labelToQuality: Record<string, string> = {
  HD: "720p",
  SD: "480p",
}
export const servers = {
  DU: "High",
  FB: "Low",
} as const
export const playbackRates = [
  {
    name: "0.5x",
    value: 0.5,
  },
  {
    name: "0.75x",
    value: 0.75,
  },
  {
    name: "1.0x",
    value: 1,
  },
  {
    name: "1.25x",
    value: 1.25,
  },
  {
    name: "1.5x",
    value: 1.5,
  },
  {
    name: "2.0x",
    value: 2,
  },
]

export const DELAY_SAVE_VIEWING_PROGRESS = 30_000 // x4 6s. old is 15s
export const DELAY_SAVE_HISTORY = 7_500 // 7.5s

export const TIMEOUT_GET_LAST_EP_VIEWING_IN_STORE = 5_000 // 5s

export const REGEXP_OLD_HOST_CURL = /animevietsub\.(?:\w+)/i

export const HOST_CURL = [
  97, 110, 105, 109, 101, 118, 105, 101, 116, 115, 117, 98, 46, 109, 111, 101,
]
  .map((val) => String.fromCharCode(val))
  .join("")
export const C_URL =
  [104, 116, 116, 112, 115, 58, 47, 47]
    .map((val) => String.fromCharCode(val))
    .join("") + HOST_CURL
