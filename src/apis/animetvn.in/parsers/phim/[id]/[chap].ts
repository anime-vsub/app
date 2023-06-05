import { getPathName } from "src/apis/parser/__helpers__/getPathName"
import { parserDom } from "src/apis/parser/__helpers__/parserDom"
import type { PhimIdChapReturns } from "src/apis/types/phim/[id]/[chap]"

export interface PropsGetListServers {
  href: string
  csrf: string
}

export default function (html: string): PhimIdChapReturns<PropsGetListServers> {
  const $ = parserDom(html)
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const csrf = $('meta[name="csrf-token"]').attr("content")!

  const chaps = $("#_listep .tapphim")
    .toArray()
    .map((item) => {
      const $item = $(item)
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      const path = getPathName($item.attr("href")!)
      return {
        props: { href: path, csrf },
        name: $item.text(),
        epId: path.split("/")[2].slice(1).match(/^\d+/)?.[0] ?? path,
      }
    })

  return { chaps }
}
