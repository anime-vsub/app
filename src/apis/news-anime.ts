import { parserDom } from "./__helpers__/parserDom"
import { get } from "src/logic/http"

async function fetchFromMgn(page: number) {
  const by = {
    name: "MGN",
    icon: "https://sf.ex-cdn.com/mgn.vn/v0.5.636/templates/themes/images/icon/icon-mgn.png",
  }
  const $ = await parserDom(`https://mgn.vn/anime/p${page}`)

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
async function fetchFromTinAnime(page: number) {
  const {
    data: { data },
  } = await get(`https://tinanime.com/api/news/?p=${page}`)

  const by = {
    icon: "https://tinanime.com/logos/114x114.png",
    name: "TinAnime",
  }
  return data.map((item) => {
    return {
      image: item.thumbnail,
      href: `https://tinanime.com/${item.slug}`,
      title: item.title,
      time: new Date(item.published_at).getTime(),
      intro: item.description,
      by,
    }
  })
}
import sort from "sort-array"
export async function NewsAnime(page: number) {
  const [mgn, tinanime] = await Promise.all([
    fetchFromMgn(page),
    fetchFromTinAnime(page),
  ])

  return sort([...mgn, ...tinanime], {
    by: "time",
    order: "desc",
  })
}
