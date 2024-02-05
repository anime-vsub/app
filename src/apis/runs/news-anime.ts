import sort from "sort-array"
import type MgnParser from "src/apis/parser/Mgn"
import Worker from "src/apis/workers/Mgn?worker"
import { get } from "src/logic/http"

import { PostWorker } from "../wrap-worker"

async function fetchFromMgn(page: number): Promise<string> {
  const { data: html } = await get(`https://mgn.vn/anime/p${page}`)

  return html
}
async function fetchFromTinAnime(page: number) {
  const { data } = await get(`https://tinanime.com/api/news/?p=${page}`)

  const by = {
    icon: "https://tinanime.com/logos/114x114.png",
    name: "TinAnime"
  }
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  return JSON.parse(data).map((item: any) => {
    return {
      image: item.thumbnail,
      href: `https://tinanime.com/${item.slug}`,
      title: item.title,
      time: new Date(item.published_at).getTime(),
      intro: item.description,
      by
    }
  })
}
export async function NewsAnime(page: number) {
  const [mgn, tinanime] = await Promise.all([
    fetchFromMgn(page).then((html) => {
      return PostWorker<typeof MgnParser>(Worker, html)
    }),
    fetchFromTinAnime(page)
  ])

  return sort([...mgn, ...tinanime], {
    by: "time",
    order: "desc"
  })
}
