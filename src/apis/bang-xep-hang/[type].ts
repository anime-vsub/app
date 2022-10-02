import { load } from "cheerio"

import { getHTML } from "../../helpers/getHTML"
import { getPathName } from "../../helpers/getPathName"

export async function BangXepHangType(type?: string) {
  const $ = load(await getHTML(`/bang-xep-hang/${type ?? ""}`))

  return $(".bxh-movie-phimletv > .group")
    .map((_i, item) => {
      const $item = $(item)
      const image =
        $item.find("img").attr("src") ??
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        $item
          .attr("style")!
          .split(";")
          .find((item) => item.startsWith("background-image"))!
          .split(":", 3)
          .slice(1)
          .join(":")
          .trim()
          .replace(/^url\((?:'|")?/, "")
          .replace(/(?:'|")?\)$/, "")
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      const path = getPathName($item.find("a").attr("href")!)
      const name = $item.find("a").text()
      const othername = $item.find(".txt-info").text()
      const process = $item.find(".score").text()

      return { image, path, name, othername, process }
    })
    .toArray()
}
