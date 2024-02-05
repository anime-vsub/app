/* eslint-disable @typescript-eslint/no-non-null-assertion */
import { parserDom } from "../__helpers__/parserDom"

export default function AjaxPlayerFBParser(html: string) {
  const $ = parserDom(html)
  const a = $("a:eq(1)")

  return {
    id: a.attr("data-id")!,
    play: a.attr("data-play")!,
    hash: a.attr("data-href")!
  }
}
