/* eslint-disable @typescript-eslint/no-non-null-assertion */

import { load } from "cheerio"

import { getHTML } from "../helpers/getHTML"
import { getInfoAnchor } from "../helpers/getInfoAnchor"
import { getInfoTPost } from "../helpers/getInfoTPost"
import { int } from "../utils/float"
import { getPathName } from "../utils/getPathName"

export async function PhimId(url: string) {
  const $ = load(await getHTML(url))

  const name = $(".Title:eq(0)").text()
  const othername = $(".SubTitle:eq(0)").text()

  const image = $(".Image img").attr("src")
  const poster = $(".TPostBg img").attr("src")
  const pathToView = $(".watch_button_more")
    ? getPathName($(".watch_button_more").attr("href")!)
    : null
  const description = $(".Description:eq(0)").text().trim()
  const rate = int($("#average_score:eq(0)").text())
  // eslint-disable-next-line camelcase
  const count_rate = int($(".num-rating:eq(0)").text())!
  const duration = $(".AAIco-access_time:eq(0)").text()
  const yearOf = int($(".AAIco-date_range:eq(0) > a").text())
  const views = int(
    $(".AAIco-remove_red_eye:eq(0)")
      .text()
      .match(/[\d,]+/)?.[0]
      ?.replace(/,/g, "")
  )!
  const season = $(".season_item > a")
    .map((_i, item) => getInfoAnchor($(item)))
    .toArray()
  const genre = $(".breadcrumb > li > a")
    .slice(1, -1)
    .map((_i, item) => getInfoAnchor($(item)))
    .toArray()
  const quality = $(".Qlty:eq(0)").text()

  // ==== info ====
  const status = $(".mvici-left > .InfoList > .AAIco-adjust:nth-child(2)")
    .text()
    .split(":", 2)[1]
    ?.trim()
  const authors = $(".mvici-left > .InfoList > .AAIco-adjust:nth-child(4) > a")
    .map((_i, item) => getInfoAnchor($(item)))
    .toArray()
  const contries = $(".mvici-left > .InfoList > .AAIco-adjust:nth-child(5) > a")
    .map((_i, item) => getInfoAnchor($(item)))
    .toArray()
  const follows = int(
    $(".mvici-left > .InfoList > .AAIco-adjust:nth-child(6)")
      .text()
      .split(":", 2)[1]
      ?.trim()
      ?.replace(/,/g, "")
  )!
  const language = $(".mvici-right > .InfoList > .AAIco-adjust:nth-child(4)")
    .text()
    .split(":", 2)[1]
    ?.trim()
  const studio = $(".mvici-right > .InfoList > .AAIco-adjust:nth-child(5)")
    .text()
    .split(":", 2)[1]
    ?.trim()
  const seasonOf = getInfoAnchor(
    $(".mvici-right > .InfoList > .AAIco-adjust:nth-child(6) > a")
  )
  const trailer = $("#Opt1 iframe").attr("src")

  const followed = $(".added").length > 0

  const toPut = $(".MovieListRelated .TPostMv")
    .map((_i, item) => getInfoTPost($(item)))
    .toArray()

  return {
    name,
    othername,
    image,
    poster,
    pathToView,
    description,
    rate,
    // eslint-disable-next-line camelcase
    count_rate,
    duration,
    yearOf,
    views,
    season,
    genre,
    quality,
    status,
    authors,
    contries,
    follows,
    language,
    studio,
    seasonOf,
    trailer,
    followed,
    toPut,
  }
}
