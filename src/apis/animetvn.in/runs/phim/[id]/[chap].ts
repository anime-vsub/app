import { useCache } from "src/apis/useCache"
import { PostWorker } from "src/apis/wrap-worker"
import { get } from "src/logic/http"

import type ParserInfo from "../../../parsers/phim/[id]"
import type Parser from "../../../parsers/phim/[id]/[chap]"
import Worker from "../../../workers/phim/[id]/[chap]?worker"
import WorkerInfo from "../../../workers/phim/[id]?worker"

export default async function (seasonId: string) {
  return await useCache(
    `https://animetvn.in/xem-phim/${seasonId}`,
    async () => {
      const { data: html } = await get(
        `https://animetvn.in/thong-tin-phim/${seasonId}`
      )

      const { pathToView } = await PostWorker<typeof ParserInfo>(
        WorkerInfo,
        html
      )

      const { data: htmlC } = await get(`https://animetvn.in${pathToView}`)

      const data = await PostWorker<typeof Parser>(Worker, htmlC)

      return data
    }
  )
}
