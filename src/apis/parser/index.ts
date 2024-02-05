import { getInfoTPost } from "./__helpers__/getInfoTPost"
import { parserDom } from "./__helpers__/parserDom"

export default function Index(html: string) {
  const $ = parserDom(html)
  const now = Date.now()

  const thisSeason = $(".MovieListTopCn:eq(0)")
    .find(".TPostMv")
    .map((_i, item) => getInfoTPost($(item), now))
    .toArray()
  const carousel = $(".MovieListSldCn .TPostMv")
    .map((_i, item) => getInfoTPost($(item), now))
    .toArray()
  const lastUpdate = $("#single-home .TPostMv")
    .map((_i, item) => getInfoTPost($(item), now))
    .toArray()
  const preRelease = $("#new-home .TPostMv")
    .map((_i, item) => getInfoTPost($(item), now))
    .toArray()
  const nominate = $("#hot-home .TPostMv")
    .map((_i, item) => getInfoTPost($(item), now))
    .toArray()
  const hotUpdate = $("#showTopPhim .TPost")
    .map((_i, item) => getInfoTPost($(item), now))
    .toArray()

  return {
    thisSeason,
    carousel,
    lastUpdate,
    preRelease,
    nominate,
    hotUpdate
  }
}

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
