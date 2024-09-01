import type PhimId from "src/apis/parser/phim/[id]"
import type PhimIdChap from "src/apis/parser/phim/[id]/[chap]"

import Worker from "./download-to-mp4.worker?worker"

export function downloadToMp4(
  url: string,
  filename: string,
  realSeasonId: string,
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
      url,
      filename,
      realSeasonId,
      season,
      chaps,
      currentChapId
    })
  })
}
