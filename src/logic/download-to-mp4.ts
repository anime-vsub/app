import { i18n } from "boot/i18n"
import type PhimId from "src/apis/parser/phim/[id]"
import type PhimIdChap from "src/apis/parser/phim/[id]/[chap]"

import Worker from "./download-to-mp4.worker?worker"

export function downloadToMp4(
  url: string,
  season: Awaited<ReturnType<typeof PhimId>>,
  chaps: Awaited<ReturnType<typeof PhimIdChap>>,
  currentChapId: string,
  onProgress: (
    current: number,
    total: number,
    tCurrent: number,
    tDuration: number,
    speed: number
  ) => void
) {
  return new Promise<void>((resolve, reject) => {
    const worker = new Worker()
    worker.onmessage = (
      event: MessageEvent<
        | {
            ok: boolean
            buffer: ArrayBuffer
            message?: string
          }
        | [
            current: number,
            total: number,
            tCurrent: number,
            tDuration: number,
            speed: number
          ]
      >
    ) => {
      if ("ok" in event.data) {
        if (event.data.ok) {
          if (event.data.buffer) {
            // save to buffer
            const url = URL.createObjectURL(
              new Blob([event.data.buffer], { type: "video/mp4" })
            )

            const a = document.createElement("a")
            a.href = url
            a.id = "temp_download"

            a.download = `[animevsub.eu.org] ${i18n.global.t(
              "tap-_chap-_name-_othername",
              [
                chaps.chaps.find((item) => item.id === currentChapId)?.name,
                season.name,
                season.othername
              ]
            )}.mp4`
            document.body.appendChild(a)
            a.click()

            // URL.revokeObjectURL(url)
            document.body.removeChild(a)
          }

          resolve()
          worker.terminate()
        } else reject(new Error(event.data.message ?? ""))
      } else {
        onProgress(...event.data)
      }
    }
    worker.onerror = (event) => reject(event)
    // worker.onmessageerror = (event) => reject(event)

    worker.postMessage({
      url
    })
  })
}
