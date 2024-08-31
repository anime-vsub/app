import { App } from "@capacitor/app"
import { name as packageName } from "app/package.json"
import { MEDIA_STREAM_SUPPORT } from "src/constants"
import { decryptM3u8, init } from "src/logic/decrypt-hls-animevsub"
import { getQualityByLabel } from "src/logic/get-quality-by-label"
import { post } from "src/logic/http"

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
export function PlayerLink(config: {
  id: string
  play: string
  hash: string
}): Promise<PlayerLinkReturn> {
  const { id, play, hash: link } = config
  return post("/ajax/player?v=2019a", {
    id,
    play,
    link,
    backuplinks: "1",
  }).then(async ({ data }) => {
    if (!data) throw new Error("unknown_error")
    type Writeable<T> = {
      -readonly [P in keyof T]: T[P] extends object ? Writeable<T[P]> : T[P]
    }
    const config = JSON.parse(data) as Writeable<PlayerLinkReturn>
    await Promise.all(
      config.link.map(async (item) => {
        if (item.file.includes("://")) {
          item.file = addProtocolUrl(item.file)
        } else {
          if (isNative) {
            // eslint-disable-next-line @typescript-eslint/no-explicit-any, promise/no-nesting
            ;(self as unknown as any).hn ??= await App.getInfo().then(
              (info) => info.id
            )
          } else {
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            ;(self as unknown as any).hn = packageName
          }
          await init()

          try {
            item.file = `data:application/vnd.apple.mpegurl;base64,${btoa(
              fixiOSLow17(await decryptM3u8(item.file))
            )}`
          } catch (err) {
            console.error(err)
          }

          item.label = "HD"
          item.preload = "auto"
          item.type = "hls"
        }

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
    )

    return config
  })
}

function fixiOSLow17(manifest: string): string {
  if (!MEDIA_STREAM_SUPPORT) {
    return manifest
      .split("\n")
      .map((line) => {
        if (line.includes("//")) return line + "#animevsub-vsub_extra"
        return line
      })
      .join("\n")
  }

  return manifest
}
