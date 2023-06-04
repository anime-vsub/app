import { useCache } from "src/apis/useCache"
import { get } from "src/logic/http"

import { PostWorker } from "../../wrap-worker"
import type IndexParser from "../parsers"
import Worker from "../workers/index?worker"

export async function Index() {
  return await useCache("https://animetvn.in/", async () => {
    const { data: html } = await get("https://animetvn.in/")

    return PostWorker<typeof IndexParser>(Worker, html)
  })
}
