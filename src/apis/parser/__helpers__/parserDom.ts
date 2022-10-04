import { load } from "cheerio"
import { parseDocument } from "htmlparser2"

export function parserDom(html: string) {
  return load(parseDocument(html))
}
