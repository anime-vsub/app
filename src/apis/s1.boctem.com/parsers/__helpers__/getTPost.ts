import type { Cheerio, Element } from "cheerio"
import { getPathName } from "src/apis/parser/__helpers__/getPathName"

function parseTime(str: string) {
  const [date, month, year] = str.split("/")

  return new Date(`${month}/${date}/${year} GMT+7`).toString()
}

function parseCount(str: string): number | null {
  const part = str.match(/(^\d+\.?\d*[A-Z]?) /)?.[1]

  if (!part) return undefined

  const n = parseFloat(part)
  const k = part.at(-1)

  switch (k) {
    case "K":
      return n * 1_000
    case "M":
      return n * 1_000_000
    case "B":
      return n * 1_000_000_000
    default:
      return n
  }
}

export function getTPost($post: Cheerio<Element>) {
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const path = getPathName($post.find("a").attr("href")!)
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const image = $post.find("img").attr("data-src")!
  const name = $post.find(".entry-title, .title").text()
  const originName = $post.find(".original_title").text()

  const status$ = $post.find(".status").text()

  const process = status$.startsWith("Tập ") ? status$.slice(4) : undefined
  const timeRelease = process
    ? undefined
    : status$
    ? parseTime(status$)
    : undefined

  const views = parseCount($post.find(".viewsCount").text())

  const isTrailer = $post.find(".is_trailer").length > 0

  return {
    path,
    image,
    name,
    originName,
    process,
    timeRelease,
    views,
    isTrailer,
  }
}
