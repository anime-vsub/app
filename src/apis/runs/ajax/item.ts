/**
 * Get update:
 * POST: /ajax/item
      @Mới_cập_nhật
      params:
        widget: list-film
        type: anime-new | anime-season | anime-series | anime-single | hh-trungquoc
      @Đề_cử
      params:
        widget: list-film
        type: hot-viewed-today | hot-viewed-season | hot-top-voted | hot-viewed-month
      @Hot_tuần
      params:
        widget: list-film
        type: top-bo-week | top-le-week
 */
import type AjaxItemParser from "src/apis/parser/ajax/item"
import { useCache } from "src/apis/useCache"
import Worker from "src/apis/workers/ajax/item?worker"
import { PostWorker } from "src/apis/wrap-worker"
import { post } from "src/logic/http"

export async function AjaxItem(type: "top-bo-week" | "top-le-week") {
  return await useCache(`/ajax/item/${type}`, async () => {
    const { data: html } = await post("/ajax/item", {
      widget: "list-film",
      type
    })

    return PostWorker<typeof AjaxItemParser>(Worker, html)
  })
}
