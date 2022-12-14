import { useCache } from "src/apis/useCache"
import Worker from "src/apis/workers/pre-search?worker"
import { post } from "src/logic/http"

import type PreSearchParser from "../parser/pre-search"
import { PostWorker } from "../wrap-worker"

export async function PreSearch(query: string) {
  return await useCache(`/ajax/suggest?q=${query}`, async () => {
    const { data: html } = await post("/ajax/suggest", {
      ajaxSearch: "1",
      keysearch: query,
    })

    return PostWorker<typeof PreSearchParser>(Worker, html)
  })
}
