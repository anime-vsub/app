import Worker from "src/apis/workers/index?worker"
import { get } from "src/logic/http"

import type IndexParser from "../parser"
import { PostWorker } from "../wrap-worker"

export async function Index() {
  const { data: html } = await get("/")

  return PostWorker<typeof IndexParser>(Worker, html)
}
