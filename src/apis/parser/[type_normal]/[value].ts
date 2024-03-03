import type TypeNormalValueParser from "src/apis/parser/[type_normal]/[value]"
import { useCache } from "src/apis/useCache"
import Worker from "src/apis/workers/[type_normal]/[value]?worker"
import { PostWorker } from "src/apis/wrap-worker"
import { get } from "src/logic/http"

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
  defaultsOptions: {
    genres?: string
    seaser?: string
    typer?: string
    year?: string
  } = {}
) {
  const isCustom =
    options &&
    (options.genres.length > 1 ||
      options.seaser ||
      options.typer ||
      options.year ||
      /* exclude */ type === "danh-sach")

  const url = isCustom
    ? [
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
        `/trang-${page}.html`
      ].join("/") + (options.sorter ? "?sort=" + options.sorter : "")
    : `/${type}/${
        Array.isArray(value) ? value.join("/") : value
      }/trang-${page}.html` + (options?.sorter ? "?sort=" + options.sorter : "")

  return await useCache(url, async () => {
    const { data: html } = await get(url)

    const now = Date.now()

    return PostWorker<typeof TypeNormalValueParser>(
      Worker,
      html,
      now,
      onlyItems
    )
  })
}
