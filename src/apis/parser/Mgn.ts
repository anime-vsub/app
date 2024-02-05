import { parserDom } from "./__helpers__/parserDom"

export default function Mgn(html: string) {
  const by = {
    name: "MGN",
    icon: "https://sf.ex-cdn.com/mgn.vn/v0.5.636/templates/themes/images/icon/icon-mgn.png"
  }
  const $ = parserDom(html)

  return $(".left-body-cat-tp > .list-body-cat-tp > .item-lbc")
    .map((i, item) => {
      const $item = $(item)
      const image = $item.find("img").attr("data-src")
      const href = $item.find("a").attr("href")
      const title = $item.find(".title-lbc").text().trim()
      const _time = $item
        .find(".category-lbc > span")
        .text()
        .replace(",", " ")
        .split("/", 3)

      const time = new Date([_time[1], _time[0], _time[2]].join("/")).getTime()

      const intro = $item.find(".intro-lbc").text().trim()
      return { image, href, title, time, intro, by }
    })
    .toArray()
}
