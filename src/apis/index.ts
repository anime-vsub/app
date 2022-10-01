import { load } from "cheerio"

import { getHTML } from "../helpers/getHTML"
import { getInfoTPost } from "../helpers/getInfoTPost"

export async function Index() {
  const $ = load(await getHTML("/"))

  const thisSeason = $(".MovieListTopCn:eq(0)")
    .find(".TPostMv")
    .map((_i, item) => getInfoTPost($(item)))
    .toArray()
  const carousel = $(".MovieListSldCn .TPostMv")
    .map((_i, item) => getInfoTPost($(item)))
    .toArray()
  const lastUpdate = $("#single-home .TPostMv")
    .map((_i, item) => getInfoTPost($(item)))
    .toArray()
  const preRelease = $("#new-home .TPostMv")
    .map((_i, item) => getInfoTPost($(item)))
    .toArray()
  const nominate = $("#hot-home .TPostMv")
    .map((_i, item) => getInfoTPost($(item)))
    .toArray()
  const hotUpdate = $("#showTopPhim .TPost")
    .map((_i, item) => getInfoTPost($(item)))
    .toArray()

  return {
    thisSeason,
    carousel,
    lastUpdate,
    preRelease,
    nominate,
    hotUpdate,
  }
}

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
