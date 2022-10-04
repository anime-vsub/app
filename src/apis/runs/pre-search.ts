import Worker from "src/apis/workers/pre-search?worker"
import { get, post } from "src/logic/http"

import type PreSearchParser from "../parser/pre-search"
import { PostWorker } from "../wrap-worker"

export async function PreSearch(query: string) {
  const { data: html } = await get(
    (
      await post("/ajax/suggest", {
        ajaxSearch: "1",
        keysearch: query,
      })
    ).data
  )

  return PostWorker<typeof PreSearchParser>(Worker, html)
}
