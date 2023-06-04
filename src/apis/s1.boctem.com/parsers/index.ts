import { parserDom } from "src/apis/parser/__helpers__/parserDom"

import { getTPost } from "./__helpers__/getTPost"

export default function index(html: string) {
  const $ = parserDom(html)

  const isekai = $("#halim-carousel-widget-3 .grid-item")
    .toArray()
    .map((item) => getTPost($(item)))
  const lastUpdate = $("#halim-advanced-widget-2-ajax-box .grid-item")
    .toArray()
    .map((item) => getTPost($(item)))
  const hotDay = $("#halim-ajax-popular-post > .item")
    .toArray()
    .map((item) => getTPost($(item)))
  const trailer = $("#halim_trailer-widget-2 .item")
    .toArray()
    .map((item) => getTPost($(item)))

  return { isekai, lastUpdate, hotDay, trailer }
}
