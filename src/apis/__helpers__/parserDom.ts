import { parseDocument } from "htmlparser2"
import { load } from "cheerio"
import { get } from "src/logic/http"

export async function parserDom(path: string, isHtml?: boolean) {
  const html = isHtml ? path : (await get(path)).data

  return load(parseDocument(html))
}
