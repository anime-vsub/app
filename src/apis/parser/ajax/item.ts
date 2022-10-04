import { getInfoTPost } from "../__helpers__/getInfoTPost"
import { parserDom } from "../__helpers__/parserDom"

export default function AjaxItem(html: string) {
  const $ = parserDom(html)

  return $(".TPost")
    .map((_i, item) => getInfoTPost($(item), 0))
    .toArray()
}
