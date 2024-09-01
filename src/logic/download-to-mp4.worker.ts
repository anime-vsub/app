import { createStore, get, setMany } from "idb-keyval"
import type PhimId from "src/apis/parser/phim/[id]"
import type PhimIdChap from "src/apis/parser/phim/[id]/[chap]"
import {
  PREFIX_CHAPS,
  PREFIX_IMAGE,
  PREFIX_POSTER,
  PREFIX_SEASON,
  REGISTRY_OFFLINE
} from "src/constants"
import type { SeasonOffline, VideoOfflineMeta } from "src/stores/vdm"
import { retryAsync } from "ts-retry"

import { convertHlsToMP4 } from "./convert-hls-to-mp4"

const store = createStore("vdm", "vdm")
async function download(
  url: string,
  filename: string,
  realSeasonId: string,
  season: Awaited<ReturnType<typeof PhimId>>,
  chaps: Awaited<ReturnType<typeof PhimIdChap>>,
  currentChapId: string,
  saveToFile: boolean
) {
  const hlsContent = await fetch(url).then((res) =>
    res.ok ? res.text() : Promise.reject(res)
  )

  let lastTCurrent = 0
  let lastTimeTCurrent = 0
  const mp4File = await convertHlsToMP4(
    hlsContent,
    (url) => {
      return fetch(`${url}#animevsub-vsub_extra`)
        .then((res) => (res.ok ? res.arrayBuffer() : Promise.reject(res)))
        .then((buffer) => new Uint8Array(buffer))
    },
    (current, total, tCurrent, tDuration) => {
      const now = performance.now()

      const speed = (tCurrent - lastTCurrent) / (now - lastTimeTCurrent)
      lastTimeTCurrent = now
      lastTCurrent = tCurrent

      postMessage([current, total, tCurrent, tDuration, speed])
    },
    10,
    { maxTry: 10, delay: 3_000 }
  )
  // save

  const buffer = await new Blob([mp4File], {
    type: "video/mp4"
  }).arrayBuffer()

  if (saveToFile) {
    return buffer
  }

  const datM: VideoOfflineMeta = {
    size: buffer.byteLength,
    saved_at: new Date().toString()
  }

  const oldSeasonData = (await get(`${PREFIX_SEASON}${realSeasonId}`)
    .then((data) => (data ? JSON.parse(data) : null))
    .catch(() => null)) as SeasonOffline | null

  const posterBuffer =
    season.poster || chaps.poster
      ? await retryAsync(
          () =>
            fetch(
              (season.poster || chaps.poster) + "#animevsub-vsub_extra"
            ).then((res) => (res.ok ? res.arrayBuffer() : Promise.reject(res))),
          { maxTry: 5, delay: 3_000 }
        )
      : undefined
  const imageBuffer =
    season.image || chaps.image
      ? await retryAsync(
          () =>
            fetch((season.image || chaps.image) + "#animevsub-vsub_extra").then(
              (res) => (res.ok ? res.arrayBuffer() : Promise.reject(res))
            ),
          { maxTry: 5, delay: 3_000 }
        )
      : undefined

  const seasonData: SeasonOffline = {
    ...season,
    // prettier-ignore
    poster: posterBuffer ? `file:/${PREFIX_POSTER}${realSeasonId}` : undefined,
    image: imageBuffer ? `file:/${PREFIX_IMAGE}${realSeasonId}` : "",
    off: {
      ...oldSeasonData?.off,
      [currentChapId]: datM
    }
  }

  const oldRegistry = (await get(`${REGISTRY_OFFLINE}`, store)
    .then((data) => (data ? JSON.parse(data) : null))
    .catch(() => null)) as Record<string, VideoOfflineMeta> | null

  await setMany(
    [
      // season data
      [`${PREFIX_SEASON}${realSeasonId}`, JSON.stringify(seasonData)],
      // chaps data
      // prettier-ignore
      [`${PREFIX_CHAPS}${realSeasonId}`, JSON.stringify({...chaps, poster: seasonData.poster, image: seasonData.image , off: seasonData.off})],
      // registry global
      [
        `${REGISTRY_OFFLINE}`,
        JSON.stringify({
          ...oldRegistry,
          [realSeasonId]: datM
        })
      ],
      // file video
      [filename, buffer],
      // file meta video
      [filename + "_m", JSON.stringify(datM)],
      // file image poster
      // prettier-ignore
      ...posterBuffer ? [[`${PREFIX_POSTER}${realSeasonId}`, posterBuffer]] as [string, ArrayBuffer][] : [],
      // file image image
      [`${PREFIX_IMAGE}${realSeasonId}`, imageBuffer]
    ],
    store
  )

  console.log(`Saved video ${mp4File.length} byte`)
}

addEventListener(
  "message",
  async ({
    data
  }: MessageEvent<{
    url: string
    filename: string
    realSeasonId: string
    season: Awaited<ReturnType<typeof PhimId>>
    chaps: Awaited<ReturnType<typeof PhimIdChap>>
    currentChapId: string
    saveToFile: boolean
  }>) => {
    try {
      const buffer = await download(
        data.url,
        data.filename,
        data.realSeasonId,
        data.season,
        data.chaps,
        data.currentChapId,
        data.saveToFile
      )

      if (buffer) postMessage({ ok: true, buffer }, { transfer: [buffer] })
      else postMessage({ ok: true })
    } catch (err) {
      console.error(err)
      postMessage({ ok: false, message: err + "" })
    }

    self.close()
  }
)
