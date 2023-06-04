import { getPathName } from "src/apis/parser/__helpers__/getPathName"
import { parserDom } from "src/apis/parser/__helpers__/parserDom"

export default function (html: string) {
  const $ = parserDom(html)

  const name = $(".entry-title:eq(0)").text()
  const othername = $(".org_title:eq(0)").text()

  const image = $(".movie-thumb").attr("data-src")
  const poster = null
  const pathToView = getPathName($(".watch-movie").attr("href"))
  const description = $(".item-content").text()
  const rate = parseInt($(".score").text()) * 2
  const count_rate = parseInt($(".total_votes").text())
  const duration$ = $(".duration").text()
  const episode$ = $(".episode").text()
  const duration =
    (episode$ ? `${episode$} / ` : "") +
    duration$.slice((duration$.indexOf(": ") >>> 0) + 2)
  const yearOf = {
    name: $(".title-year a").text(),
    path: getPathName($(".title-year a").attr("href")),
  }
  const views = null
  
}
