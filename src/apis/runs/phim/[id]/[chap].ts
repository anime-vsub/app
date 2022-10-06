import type PhimIdChapParser from "src/apis/parser/phim/[id]/[chap]"
import { useCache } from "src/apis/useCache"
import Worker from "src/apis/workers/phim/[id]/[chap]?worker"
import { PostWorker } from "src/apis/wrap-worker"
import { get } from "src/logic/http"

export async function PhimIdChap(season: string) {
  return await useCache(`/phim/${season}/xem-phim`, async () => {
    const { data: html } = await get(`/phim/${season}/xem-phim.html`)

    return PostWorker<typeof PhimIdChapParser>(Worker, html)
  })
}
