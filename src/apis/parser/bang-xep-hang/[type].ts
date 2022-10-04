import { getPathName } from "../__helpers__/getPathName"
import { parserDom } from "../__helpers__/parserDom"

export default function BangXepHangType(html: string) {
  const $ = parserDom(html)

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
