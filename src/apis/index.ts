import { getHTML } from "./helpers/getHTML"
import { getInfoTPost } from "./helpers/getInfoTPost"
import { parserDOM } from "./utils/parserDOM"

export async function Index(url: string) {
  const dom = parserDOM(await getHTML(url))

  const thisSeason = Array.from(
    dom.querySelectorAll(".MovieListTopCn .TPostMv")
  ).map(getInfoTPost)
  const carousel = Array.from(
    dom.querySelectorAll(".MovieListSldCn .TPostMv")
  ).map(getInfoTPost)
  const lastUpdate = Array.from(
    dom.querySelectorAll("#single-home .TPostMv")
  ).map(getInfoTPost)
  const preRelease = Array.from(dom.querySelectorAll("#new-home .TPostMv")).map(
    getInfoTPost
  )
  const nominate = Array.from(dom.querySelectorAll("#hot-home .TPostMv")).map(
    getInfoTPost
  )
  const hotUpdate = Array.from(dom.querySelectorAll("#showTopPhim .TPost")).map(
    getInfoTPost
  )

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
