import { dayTextToNum } from "src/logic/dayTextToNum"

import { parserDom } from "../../__helpers__/parserDom"

export default function PhimIdChap(html: string) {
  const $ = parserDom(html)

  const chaps = $("#list-server .list-episode .episode")
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

        name: $item.text().trim()
      } as const
    })
    .toArray()
  const [day, hour, minus] =
    $(".schedule-title-main > h4 > strong:nth-child(3)")
      .text()
      .match(/(Thứ [^\s]+|chủ nhật) vào lúc (\d+) giờ (\d+) phút/i)
      ?.slice(1) ?? []
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const image = $(".Image img").attr("src")!
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const poster = $(".TPostBg img").attr("src")!

  return {
    chaps: chaps as Readonly<typeof chaps>,
    update: !day
      ? null
      : ([dayTextToNum(day.toLowerCase()), +hour, +minus] as [
          number,
          number,
          number
        ]),
    image,
    poster
  }
}
