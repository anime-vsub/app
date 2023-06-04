import { useCache } from "src/apis/useCache"
import { PostWorker } from "src/apis/wrap-worker"
import { get } from "src/logic/http"

import type Parser from "../../../parsers/phim/[id]/[chap]"
import Worker from "../../../workers/phim/[id]/[chap]?worker"

export default async function (pathToView: string) {
  if (pathToView.endsWith(".html"))
    pathToView = pathToView.slice(0, -".html".length) + "-tap-00.html"
  else pathToView += "-tap-00.html"

  return await useCache(
    `https://animetvn.in/xem-phim/${pathToView}`,
    async () => {
      const { data: html } = await get(
        `https://animetvn.in/xem-phim/${pathToView}`
      )

      return PostWorker<typeof Parser>(Worker, html)
    }
  )
}
