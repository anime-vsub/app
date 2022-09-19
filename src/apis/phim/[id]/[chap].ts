import { parserDOM } from "../../utils/parserDOM"

import html from "src/apis/__test__/data/phim/tonikaku-kawaii-a3860/xem-phim-72839.txt?raw"

export async function Phim_Id_Chap() {
  const dom = parserDOM(html)

  const chaps = Array.from(
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    dom.querySelector(".list-episode")!.querySelectorAll("a")
  ).map((item) => {
    return {
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      id: item.getAttribute("data-id")!,
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      play: item.getAttribute("data-play")!,
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      hash: item.getAttribute("data-hash")!,
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      name: item.textContent!,
    }
  })
  const [day, hour, minus] =
    dom
      .querySelector(".schedule-title-main > h4 > strong:nth-child(3)")
      ?.textContent?.match(/Thứ ([^\s]+) vào lúc (\d+) giờ (\d+) phút/i)
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
