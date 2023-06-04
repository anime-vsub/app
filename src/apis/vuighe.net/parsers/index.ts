import { getPathName } from "src/apis/parser/__helpers__/getPathName"
import { parserDom } from "src/apis/parser/__helpers__/parserDom"

import { getInfoTPost } from "./__helper__/getInfoTPost"

export default function parseIndex(html: string) {
  const $ = parserDom(html)

  const carousel = $("#slider-container .slider-item")
    .map((_i, item) => getInfoTPost($(item)))
    .toArray()
  const lastUpdate = $(".container > section")
    .eq(2)
    .find(".group/episode")
    .map((_i, item) => getInfoTPost($(item)))
    .toArray()
  const preRelease = $(".container > section")
    .eq(9)
    .find(".group/film")
    .map((_i, item) => getInfoTPost($(item)))
    .toArray()
    .filter((item) => item.isPreRelease)
}
