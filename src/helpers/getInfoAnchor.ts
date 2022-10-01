import type { Cheerio, Element } from "cheerio"

import { getPathName } from "./getPathName"

export function getInfoAnchor(a: Cheerio<Element>) {
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const path = getPathName(a.attr("href")!)
  const name = a.text()

  return { path, name }
}
