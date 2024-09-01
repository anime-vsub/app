import type PhimId from "src/apis/parser/phim/[id]"
import type PhimIdChap from "src/apis/parser/phim/[id]/[chap]"

import { convertHlsToMP4 } from "./convert-hls-to-mp4"

async function download(url: string) {
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

  return buffer
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
      const buffer = await download(data.url)

      if (buffer) postMessage({ ok: true, buffer }, { transfer: [buffer] })
      else postMessage({ ok: true })
    } catch (err) {
      console.error(err)
      postMessage({ ok: false, message: err + "" })
    }

    self.close()
  }
)
