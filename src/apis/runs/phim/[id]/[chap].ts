import type PhimIdChapParser from "src/apis/parser/phim/[id]/[chap]"
import Worker from "src/apis/workers/phim/[id]/[chap]?worker"
import { PostWorker } from "src/apis/wrap-worker"
import { get } from "src/logic/http"

export async function PhimIdChap(season: string) {
  const { data: html } = await get(`/phim/${season}/xem-phim.html`)

  return PostWorker<typeof PhimIdChapParser>(Worker, html)
}
