export const labelToQuality: Record<string, string> = {
  HD: "720p",
  SD: "480p",
}
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

export const DELAY_SAVE_VIEWING_PROGRESS = 5_000

export const C_URL = [
  104, 116, 116, 112, 115, 58, 47, 47, 97, 110, 105, 109, 101, 118, 105, 101,
  116, 115, 117, 98, 46, 112, 114, 111,
]
  .map((val) => String.fromCharCode(val))
  .join("")
