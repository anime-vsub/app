import { useCache } from "src/apis/useCache"
import { get } from "src/logic/http"

import { PostWorker } from "../../wrap-worker"
import type IndexParser from "../parsers"
import Worker from "../workers/index?worker"

export async function Index() {
  return await useCache("https://s1.boctem.com/", async () => {
    const { data: html } = await get("https://s1.boctem.com/")

    return PostWorker<typeof IndexParser>(Worker, html)
  })
}
