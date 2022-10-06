import { useCache } from "src/apis/useCache"
import Worker from "src/apis/workers/lich-chieu-phim?worker"
import { get } from "src/logic/http"

import type LichChieuPhimParser from "../parser/lich-chieu-phim"
import { PostWorker } from "../wrap-worker"

export async function LichChieuPhim() {
  return await useCache("/lich-chieu-phim", async () => {
    const { data: html } = await get("/lich-chieu-phim.html")
    const now = Date.now()

    return PostWorker<typeof LichChieuPhimParser>(Worker, html, now)
  })
}
