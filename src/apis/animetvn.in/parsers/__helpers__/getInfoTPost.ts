import type { Cheerio, Element } from "cheerio"
import { getPathName } from "src/apis/parser/__helpers__/getPathName"
import { int } from "src/apis/parser/__helpers__/int"

export type TPost = ReturnType<typeof getInfoTPost>

export function getInfoTPost(cheerio: Cheerio<Element>, now: number) {
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const path = getPathName(cheerio.find("a").attr("href")!)
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const image = (cheerio.find("img").attr("src") ??
    cheerio.attr("style")?.match(/url\(([^)]+)\)/)?.[1])!
  const name = cheerio.find(".title:eq(0), h3:eq(0)").text()

  const time$ = cheerio.find(".time").text()
  const isTime$ = time$.match(
    /^(?:Tháng (\d{1,2}-\d+))|(\d{1,2}-\d{1,2}-\d+)$/g
  )

  const chap = isTime$ ? undefined : time$.replace("Tập", "").trim()
  const rate = null
  const views = int(
    cheerio
      .find(".viewed")
      .text()
      .match(/[\d,]+/)?.[0]
      ?.replace(/,/g, "")
  )

  // =============== more =====================
  const process = chap

  const year = cheerio.find(".mode .year").text()

  const description = cheerio.find(".f-description .tooltip_content, .data .txt").text()

  return {
    path,
    image,
    name,
    chap,
    rate,
    views,

    // === more ===
    // quality,
    process,
    year,
    description,
    // studio,
    // genre,
    time_release: isTime$
      ? parseTimeToNow(isTime$[1] || isTime$[2] || "")
      : undefined,
  }
}

function parseTimeToNow(time: string) {
  // eslint-disable-next-line functional/no-let
  let [date, month, year] = time.split("-")

  if (!year) [month, year] = [date, month]

  return new Date(`${month}/${date}/${year}`).toString()
}
