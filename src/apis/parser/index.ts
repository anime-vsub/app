import type { IndexReturns } from "../types"

import { getInfoTPost } from "./__helpers__/getInfoTPost"
import { parserDom } from "./__helpers__/parserDom"

export default function Index(html: string): IndexReturns {
  const $ = parserDom(html)
  const now = Date.now()

  const thisSeason = $(".MovieListTopCn:eq(0)")
    .find(".TPostMv")
    .map((_i, item) => getInfoTPost($(item), now, true))
    .toArray()
  const carousel = $(".MovieListSldCn .TPostMv")
    .map((_i, item) => getInfoTPost($(item), now))
    .toArray()
  const lastUpdate = $("#tv-home .TPostMv")
    .map((_i, item) => getInfoTPost($(item), now))
    .toArray()
  const cn = $("#cn-home .TPostMv")
    .map((_i, item) => getInfoTPost($(item), now))
    .toArray()
  const sn = $("#sn-home .TPostMv")
    .map((_i, item) => getInfoTPost($(item), now))
    .toArray()
  const hotUpdate = $("#showTopPhim .TPost")
    .map((_i, item) => getInfoTPost($(item), now, false, true))
    .toArray()

  return [
    {
      name: "CarouselTop",
      props: {
        items: carousel,
        aspectRatio:  622 / 350,
      },
    },
    {
      name: "List",
      props: {
        items: thisSeason,
      },
    },
    {
      name: "GridCard",
      props: {
        name: "Mới cập nhật",
        to: "/danh-sach/phim-bo.html",
        items: lastUpdate,
      },
    },
    {
      name: "List",
      props: {
        name: "Hot trong ngày",
        to: "/bang-xep-hang/day.html",
        items: hotUpdate,
        rank: true
      },
    },
    {
      name: "GridCard",
      props: {
        name: "Trung Quốc",
        to: "/the-loai/cn-animation.html",
        items: cn,
      },
    },
    {
      name: "GridCard",
      props: {
        name: "Super sentai",
        to: "/danh-sach/phim-sieu-nhan.html",
        items: sn,
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
