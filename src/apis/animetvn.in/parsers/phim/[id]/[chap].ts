import { getPathName } from "src/apis/parser/__helpers__/getPathName"
import { parserDom } from "src/apis/parser/__helpers__/parserDom"
import type { PhimIdChapReturns } from "src/apis/types/phim/[id]/[chap]"

export interface PropsGetListServers {
  href: string
}

export default function (html: string): PhimIdChapReturns<PropsGetListServers> {
  const $ = parserDom(html)

  const chaps = $("#_listep .tapphim")
    .toArray()
    .map((item) => {
      const $item = $(item)
      return {
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        props: { href: getPathName($item.attr("href")!) },
        name: $item.text(),
      }
    })

  return { chaps }
}
