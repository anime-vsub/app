import type { PlayerLink } from "src/apis/runs/ajax/player-link"

const map = {
  "FHD|HD": "1080p|720p",
  FHD: "1080p",
  HD: "720p",
  SD: "480p"
} as const
export function getQualityByLabel(
  label: Awaited<ReturnType<typeof PlayerLink>>["link"][0]["label"]
) {
  return (
    (map[label as keyof typeof map] as
      | (typeof map)[keyof typeof map]
      | undefined) ?? (label as Exclude<typeof label, keyof typeof map>)
  )
}
