import { useCache } from "src/apis/useCache"
import { PostWorker } from "src/apis/wrap-worker"
import { get } from "src/logic/http"

import type PhimIdParser from "../../parsers/phim/[id]"
import Worker from "../../workers/phim/[id]?worker"

export async function PhimId(seasonId: string) {
  return await useCache(
    `https://animetvn.in/thong-tin-phim/${seasonId}`,
    async () => {
      const { data: html } = await get(
        `https://animetvn.in/thong-tin-phim/${seasonId}`
      )

      return PostWorker<typeof PhimIdParser>(Worker, html)
    }
  )
}
