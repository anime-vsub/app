import { load } from "cheerio"

import { getHTML } from "../../helpers/getHTML"

export async function PhimIdChap(season: string) {
  const $ = load(await getHTML(`/phim/${season}/xem-phim.html`))

  const chaps = $(".list-episode:eq(0)")
    .find("a")
    .map((_i, item) => {
      const $item = $(item)
      return {
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        id: $item.attr("data-id")!,
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        play: $item.attr("data-play")!,
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        hash: $item.attr("data-hash")!,

        name: $item.text().trim(),
      }
    })
    .toArray()
  const [day, hour, minus] =
    $(".schedule-title-main > h4 > strong:nth-child(3)")
      .text()
      .match(/Thứ ([^\s]+) vào lúc (\d+) giờ (\d+) phút/i)
      ?.slice(1) ?? []

  return {
    chaps,
    update: !day ? null : [dayMap[day.toLowerCase()], +hour, +minus],
  }
}

const dayMap: Record<string, number> = {
  hai: 1,
  ba: 2,
  bốn: 3,
  năm: 4,
  sáu: 5,
  bảy: 6,
  "chủ nhật": 7,
  cn: 7,
}
