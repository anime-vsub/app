import { C_URL } from "src/constants"
import { getQualityByLabel } from "src/logic/get-quality-by-label"
// import { post } from "src/logic/http"

const addProtocolUrl = (file: string) =>
  file.startsWith("http") ? file : `https:${file}`

interface PlayerLinkReturn {
  readonly link: {
    readonly file: string
    readonly label: "FHD|HD" | "HD" | "FHD" | `${720 | 360 | 340}p`
    readonly qualityCode: ReturnType<typeof getQualityByLabel>
    readonly preload?: string
    readonly type:
      | "hls"
      | "aac"
      | "f4a"
      | "mp4"
      | "f4v"
      | "m3u"
      | "m3u8"
      | "m4v"
      | "mov"
      | "mp3"
      | "mpeg"
      | "oga"
      | "ogg"
      | "ogv"
      | "vorbis"
      | "webm"
      | "youtube"
  }[]
  readonly playTech: "api" | "trailer"
}

type Writeable<T> = {
  -readonly [P in keyof T]: T[P] extends object ? Writeable<T[P]> : T[P]
}

export function PlayerLink(config: {
  id: string
  play: string
  hash: string
}): Promise<PlayerLinkReturn> {
  const { id, play, hash: link } = config
  //  "#animevsub-vsub" + "_extra"
  // return post("/ajax/player?v=2019a", {
  //   id,
  //   play,
  //   link,
  //   backuplinks: "1"
  // }).then(({ data }) => {
  return fetch(`${C_URL}/ajax/player?v=2019a#animevsub-vsub_extra`, {
    method: "post",
    body: new URLSearchParams({ id, play, link, backuplinks: "1" })
  })
    .then((res) => res.json() as Promise<Writeable<PlayerLinkReturn>>)
    .then((config) => {
      config.link.forEach((item) => {
        item.file = addProtocolUrl(item.file)
        switch (
          (item.label as typeof item.label | undefined)?.toUpperCase() as
            | Uppercase<Exclude<typeof item.label, undefined>>
            | undefined
        ) {
          case "HD":
            if (item.preload) item.label = "FHD|HD"
            break
          case undefined:
            item.label = "HD"
            break
        }
        item.qualityCode = getQualityByLabel(item.label)
        item.type ??= "mp4"
      })

      return config
    })
}
