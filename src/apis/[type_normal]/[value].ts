import type { CheerioAPI } from "cheerio"

import { getInfoTPost } from "../__helpers__/getInfoTPost"
import { parserDom } from "../__helpers__/parserDom"

export async function TypeNormalValue(
  type: string,
  value: string | string[],
  page = 1,
  onlyItems: boolean,
  options: {
    genres: string[]
    seaser: string | null
    sorter: string | null
    typer: string | null
    year: string | null
  },
  defaultsOptions: {
    genres?: string
    seaser?: string
    typer?: string
    year?: string
  }
) {
  const isCustom =
    options.genres.length > 0 ||
    options.seaser ||
    options.typer ||
    options.year ||
    /* exclude */ type === "danh-sach"
  // eslint-disable-next-line functional/no-let
  let $: CheerioAPI
  if (isCustom) {
    $ = await parserDom(
      [
        "/danh-sach",
        options.typer ?? defaultsOptions.typer ?? "all",
        options.genres
          .concat(
            defaultsOptions.typer && defaultsOptions.typer !== "all"
              ? [defaultsOptions.typer]
              : []
          )
          .join("-") || "all",
        options.seaser ?? defaultsOptions.seaser ?? "all",
        options.year ?? defaultsOptions.year ?? "all",
        `/trang-${page}.html`,
      ].join("/") + (options.sorter ? "?sort=" + options.sorter : "")
    )
  } else {
    $ = await parserDom(
      `/${type}/${
        Array.isArray(value) ? value.join("/") : value
      }/trang-${page}.html` + (options?.sorter ? "?sort=" + options.sorter : "")
    )
  }
  const now = Date.now()

  const items = $(".MovieList:eq(0)")
    .find(".TPostMv")
    .map((_i, item) => getInfoTPost($(item), now))
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
    .replace(/^Danh sách /i, "")

  const filter = {
    sorter: $("#filter")
      .find(".fc-main-list > li > a")
      .map((_i, anchor) => {
        const text = $(anchor).text()
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        const value = new URL(
          $(anchor).attr("href") ?? "",
          "https://animevietsub.cc"
        ).searchParams.get("sort")!
        return { text, value }
      })
      .toArray(),
    typer: $("#filter")
      .find(".fc-filmtype label")
      .map((_i, item) => {
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        const value = $(item).find("input").attr("value")!

        // eslint-disable-next-line array-callback-return
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
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        const value = $(item).find("input").attr("value")!

        // eslint-disable-next-line array-callback-return
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
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        const value = $(item).find("input").attr("value")!

        // eslint-disable-next-line array-callback-return
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
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        const value = $(item).find("input").attr("value")!

        // eslint-disable-next-line array-callback-return
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