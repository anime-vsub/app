import { load } from "cheerio"
import { parseDocument } from "htmlparser2"
import { get } from "src/logic/http"

export async function parserDom(path: string, isHtml?: boolean) {
  const html = isHtml ? path : (await get(path)).data

  return load(parseDocument(html))
}
