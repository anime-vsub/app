import { dayTextToNum } from "src/logic/dayTextToNum"

import { parserDom } from "../../__helpers__/parserDom"

export default function PhimIdChap(html: string) {
  const $ = parserDom(html)

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
      .match(/(Thứ [^\s]+) vào lúc (\d+) giờ (\d+) phút/i)
      ?.slice(1) ?? []
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const image = $(".Image img").attr("src")!
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const poster = $(".TPostBg img").attr("src")!

  return {
    chaps,
    update: !day ? null : [dayTextToNum(day.toLowerCase()), +hour, +minus],
    image,
    poster,
  }
}
