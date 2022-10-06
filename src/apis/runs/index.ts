import { useCache } from "src/apis/useCache"
import Worker from "src/apis/workers/index?worker"
import { get } from "src/logic/http"

import type IndexParser from "../parser"
import { PostWorker } from "../wrap-worker"

export async function Index() {
  return await useCache("/", async () => {
    const { data: html } = await get("/")

    return PostWorker<typeof IndexParser>(Worker, html)
  })
}
