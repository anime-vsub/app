import { parserDom } from "src/apis/parser/__helpers__/parserDom"
import type { IndexReturns } from "src/apis/types"

import { getInfoTPost } from "./__helpers__/getInfoTPost"

export default function Index(html: string): IndexReturns {
  const $ = parserDom(html)
  const now = Date.now()

  return [
    {
      name: "CarouselTop",
      props: {
        aspectRatio: 900 / 450,
        items: $("#main-slider .item")
          .map((_i, item) => getInfoTPost($(item), now))
          .toArray(),
      },
    },
    {
      name: "GridCard",
      props: {
        name: "Anime",
        to: "/nhom/anime",
        items: $(".anime .item")
          .map((_i, item) => getInfoTPost($(item), now))
          .toArray(),
      },
    },
    {
      name: "List",
      props: {
        items: $(".home-list-trailers .item")
          .map((_i, item) => getInfoTPost($(item), now))
          .toArray(),
      },
    },
    {
      name: "List",
      props: {
        rank: true,
        items: $(".rank-film-list .item")
          .map((_i, item) => getInfoTPost($(item), now))
          .toArray(),
      },
    },
    {
      name: "GridCard",
      props: {
        name: "Cartoon",
        to: "/nhom/cartoon",
        items: $(".cartoon .item")
          .map((_i, item) => getInfoTPost($(item), now))
          .toArray(),
      },
    },
    {
      name: "List",
      props: {
        name: "Drama",
        to: "/nhom/japanese-drama",
        items: $(".japanese-drama .item")
          .map((_i, item) => getInfoTPost($(item), now))
          .toArray(),
      },
    },
    {
      name: "List",
      props: {
        name: "Super sentai",
        to: "/nhom/sieu-nhan",
        items: $(".sieu-nhan .item")
          .map((_i, item) => getInfoTPost($(item), now))
          .toArray(),
      },
    },
  ]
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
