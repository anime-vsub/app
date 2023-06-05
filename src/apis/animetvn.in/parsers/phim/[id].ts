import type { Cheerio, CheerioAPI, Element } from "cheerio"
import { getInfoAnchor } from "src/apis/parser/__helpers__/getInfoAnchor"
import { getPathName } from "src/apis/parser/__helpers__/getPathName"
import { int } from "src/apis/parser/__helpers__/int"
import { parserDom } from "src/apis/parser/__helpers__/parserDom"
import type { PhimReturns } from "src/apis/types/phim/[id]"

function findInfo($: CheerioAPI, infoList: Cheerio<Element>, q: string) {
  return $(
    infoList.toArray().find((item) => {
      return $(item).find("span").text().toLowerCase().startsWith(q)
    })
  )
}

export default function (html: string): PhimReturns {
  const $ = parserDom(html)

  const name = $(".name-vi:eq(0)").text()
  const othername = $(".name-eng:eq(0)").text()

  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const image = $(".small_img img").attr("src")!
  const poster = $(".big_img").attr("src")
  const pathToView = $(".play-now").length > 0
  ? // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      getPathName($(".play-now").attr("href")!)
    : null
  const description = $("#tab-film-content:eq(0)").text().trim()
  const rate = parseFloat($("#rating-text:eq(0)").text())
  // eslint-disable-next-line camelcase
  const count_rate = parseInt($(".count-rating-num:eq(0)").text())

  const hasColor$ = $(".more-info > .has-color")
  const duration = findInfo($, hasColor$, " Số tập: ").text()
  const yearOf$ = findInfo($, hasColor$, " Năm phát sóng: ")
    .text()
    .split(":", 2)[1]
  const yearOf = yearOf$ ? {
    path: `/tim-kiem/${yearOf$.replace(" ", "_")}.html`,
    name: yearOf$,
  }:undefined
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const views = int(
    findInfo($, hasColor$, " Lượt xem: ")
      .text()
      ?.trim()
      .match(/[\d,]+/)?.[0]
      ?.replace(/,/g, "")
  )!
  const season = $(".info-play-wrap .related_film a")
    .map((_i, item) => getInfoAnchor($(item)))
    .toArray()
  const genre = findInfo($, hasColor$, " Thể loại: ")
    .find("a")
    .map((_i, item) => getInfoAnchor($(item)))
    .toArray()
  // const quality = $(".Qlty:eq(0)").text()

  // ==== info ====
  // const infoListLeft = $(".mvici-left > .InfoList > .AAIco-adjust")
  // const infoListRight = $(".mvici-right > .InfoList > .AAIco-adjust")
  // const status = findInfo($, infoListLeft, "trạng thái")
  //   .text()
  //   .split(":", 2)[1]
  //   ?.trim()
  // const authors = findInfo($, infoListLeft, "đạo diễn")
  //   .find("a")
  //   .map((_i, item) => getInfoAnchor($(item)))
  //   .toArray()
  // const contries = findInfo($, infoListLeft, "quốc gia")
  //   .find("a")
  //   .map((_i, item) => getInfoAnchor($(item)))
  //   .toArray()
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const follows = int(
    findInfo($, hasColor$, " Thành viên theo dõi: ")
      .text()
      .split(":", 2)[1]
      ?.trim()
      ?.replace(/,/g, "")
  )!
  // const language = findInfo($, infoListRight, "ngôn ngữ")
  //   .text()
  //   .split(":", 2)[1]
  //   ?.trim()
  const studio = getInfoAnchor(
    findInfo($, hasColor$, " Nhà sản xuất: ").find("a")
  )
  // const seasonOf = getInfoAnchor(findInfo($, infoListRight, "season").find("a"))
  // const trailer = $("#Opt1 iframe").attr("src")

  // const toPut = $(".MovieListRelated .TPostMv")
  //   .map((_i, item) => getInfoTPost($(item), now))
  //   .toArray()

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
    // quality,
    // status,
    authors: [],
    // contries,
    follows,
    // language,
    studio,
    seasonOf: yearOf,
    toPut: []
    // trailer,
    // toPut,
  }
}
