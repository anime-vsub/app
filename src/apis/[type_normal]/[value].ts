import { load } from "cheerio"

import { getHTML } from "../helpers/getHTML"
import { getInfoTPost } from "../helpers/getInfoTPost"

export async function TypeNormalValue(
  type: string,
  value: string | string[],
  page = 1,
  onlyItems: boolean,
  options?: {
    genres: string[]
    seaser: string | null
    sorter: string | null
    typer: string | null
    year: string | null
  },
  defaultOptions?:
    | {
        type_normal: "danh-sach" | "the-loai" | "season"
        value: string
      }
    | {
        type_normal: "season"
        value: string | string[]
      }
) {
  const isCustom =
    options &&
    (options.genres.length > 0 ||
      options.seaser ||
      options.typer ||
      options.year)

  let $: Cheerio
  if (isCustom) {
    $ = load(
      await getHTML(
        [
          "/danh-sach",
          options.typer ?? "all",
          options.genres.join("-"),
          options.seaser,
          options.year,
          `/trang-${page}.html`,
        ]
          .filter(Boolean)
          .join("/") + (options.sorter ? "?sort=" + options.sorter : "")
      )
    )
  } else {
    $ = load(
      await getHTML(
        `/${type}/${
          Array.isArray(value) ? value.join("/") : value
        }/trang-${page}.html` +
          (options?.sorter ? "?sort=" + options.sorter : "")
      )
    )
  }

  const items = $(".MovieList:eq(0)")
    .find(".TPostMv")
    .map((_i, item) => getInfoTPost($(item)))
    .toArray()
  const curPage = parseInt($(".current").attr("data") ?? "1")
  const maxPage = parseInt($(".larger:last-child").attr("data") ?? "1")

  if (onlyItems) {
    return { items, curPage, maxPage }
  }

  const title = $(".breadcrumb:eq(0) > li")
    .slice(1)
    .map((_i, item) => $(item).text().trim())
    .toArray()
    .join(" ")
    .replace(/:/g, "")

  const filter = {
    sorter: $("#filter")
      .find(".fc-main-list > li > a")
      .map((_i, anchor) => {
        const text = $(anchor).text()
        const value = new URL(
          $(anchor).attr("href"),
          "https://animevietsub.cc"
        ).searchParams.get("sort")
        return { text, value }
      })
      .toArray(),
    typer: $("#filter")
      .find(".fc-filmtype label")
      .map((_i, item) => {
        const value = $(item).find("input").attr("value")

        if (value === "all") return

        const text = $(item).text()

        return {
          text,
          value,
        }
      })
      .toArray()
      .filter(Boolean),
    seaser: $("#filter")
      .find(".fc-quality label")
      .map((_i, item) => {
        const value = $(item).find("input").attr("value")

        if (value === "all") return

        const text = $(item).text()

        return {
          text,
          value,
        }
      })
      .toArray()
      .filter(Boolean),
    gener: $("#filter")
      .find(".fc-genre label")
      .map((_i, item) => {
        const value = $(item).find("input").attr("value")

        if (value === "all") return

        const text = $(item).text()

        return {
          text,
          value,
        }
      })
      .toArray()
      .filter(Boolean),
    year: $("#filter")
      .find(".fc-release label")
      .map((_i, item) => {
        const value = $(item).find("input").attr("value")

        if (value === "all") return

        const text = $(item).text()

        return {
          text,
          value,
        }
      })
      .toArray()
      .filter(Boolean),
  }

  return {
    title,
    items,
    curPage,
    maxPage,
    filter,
  }
}
