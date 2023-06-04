import type { Cheerio, Element } from "cheerio"
import { getPathName } from "src/apis/parser/__helpers__/getPathName"

export type TPost = ReturnType<typeof getInfoTPost>

export function getInfoTPost($sliderItem: Cheerio<Element>) {
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const path = getPathName($sliderItem.find("a").attr("href")!)
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const image = $sliderItem.find("img").attr("data-src")!

  const views = parseInt(
    $sliderItem
      .find(".absolute > .font-extralight:last")
      .text()
      .replace(/,/g, "")
      .trim()
  )
  const epTitle =
    $sliderItem.find(".absolute > .font-extralight").length > 2
      ? $sliderItem.find(".absolute > .font-extralight").eq(0).text()
      : undefined
  const name = $sliderItem
    .find(".absolute .text-ellipsis, .absolute .line-clamp-1")
    .text()
  const isPreRelease = $sliderItem.find("div.bg-red-600/50").length > 0

  return { path, image, views, epTitle, name, isPreRelease }
}
