import { load } from "cheerio"

import { getHTML } from "./helpers/getHTML"
import { getInfoTPost } from "./helpers/getInfoTPost"

export function LichChieuPhim() {
  const $ = load(await getHTML("/lich-chieu-phim.html"))

return  $("#sched-content > .Homeschedule").map((_i, item) => {
	  const day = $(item).find(".Top > h1 > b").text()
	  const [date, month] = $(item).find(".Top > h1").text().split(",", 2)[1].match(/\d{1,2}/g)
	  const items = $(item).find(".MovieList:eq(0)")
	    .find(".TPostMv")
	    .map((_i, item) => getInfoTPost($(item)))
	    .toArray()

	  return { day, date, month, items }
	}).toArray()
}