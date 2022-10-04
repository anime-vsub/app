/**
 * Get update:
 * POST: https://animevietsub.cc/ajax/item
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
import { post } from "src/logic/http"
import { parserDom } from "../__helpers__/parserDom"
import { getInfoTPost } from "../__helpers__/getInfoTPost"

export async function AjaxItem(type: "top-bo-week" | "top-le-week") {
  const $ = await parserDom(
    (
      await post("/ajax/item", {
        widget: "list-film",
        type,
      })
    ).data,
    true
  )

  return $(".TPost")
    .map((_i, item) => getInfoTPost($(item)))
    .toArray()
}
