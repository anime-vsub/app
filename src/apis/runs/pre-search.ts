import { useCache } from "src/apis/useCache"
import Worker from "src/apis/workers/pre-search?worker"
import { C_URL } from "src/constants"
// import { post } from "src/logic/http"

import type PreSearchParser from "../parser/pre-search"
import { PostWorker } from "../wrap-worker"

export async function PreSearch(query: string) {
  return await useCache(`/ajax/suggest?q=${query}`, async () => {
    // const { data: html } = await post("/ajax/suggest", {
    //   ajaxSearch: "1",
    //   keysearch: query
    // })

    const html = await fetch(`${C_URL}/ajax/suggest#animevsub-vsub_extra`, {
      method: "post",
      body: new URLSearchParams({
        ajaxSearch: "1",
        keysearch: query
      })
    }).then((res) => res.text())

    return PostWorker<typeof PreSearchParser>(Worker, html)
  })
}
