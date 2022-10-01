import { load } from "cheerio"

import { getHTML } from "./helpers/getHTML"
import { getInfoTPost } from "./helpers/getInfoTPost"

export async function LichChieuPhim() {
  const $ = load(await getHTML("/lich-chieu-phim.html"))

  return $("#sched-content > .Homeschedule")
    .map((_i, item) => {
      const day = $(item).find(".Top > h1 > b").text()
      const _tmp = $(item)
        .find(".Top > h1")
        .text()
        .split(",", 2)[1]
        .match(/\d{1,2}/g)

        const date = _tmp?.[0] ?? null
        const month = _tmp?.[1] ?? null

      const items = $(item)
        .find(".MovieList:eq(0)")
        .find(".TPostMv")
        .map((_i, item) => getInfoTPost($(item)))
        .toArray()

      return { day, date, month, items }
    })
    .toArray()
}
