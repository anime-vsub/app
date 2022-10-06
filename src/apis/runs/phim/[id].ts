import type PhimIdParser from "src/apis/parser/phim/[id]"
import { useCache } from "src/apis/useCache"
import Worker from "src/apis/workers/phim/[id]?worker"
import { PostWorker } from "src/apis/wrap-worker"
import { get } from "src/logic/http"

export async function PhimId(seasonId: string) {
  return await useCache(`/phim/${seasonId}`, async () => {
    const { data: html } = await get(`/phim/${seasonId}/`)
    const now = Date.now()

    return PostWorker<typeof PhimIdParser>(Worker, html, now)
  })
}
