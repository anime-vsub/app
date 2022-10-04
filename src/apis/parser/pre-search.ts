import { getPathName } from "./__helpers__/getPathName"
import { parserDom } from "./__helpers__/parserDom"

interface DataItem {
  image: string
  path: string
  name: string
  status: string
}
/**
  * const form = new FormData()
  form.append("ajaxSearch", "1")
  form.append("keysearch", query.value)
  loading.value = true
  const html = await fetch(
    props.domain
      ? `${props.domain}/ajax/suggest`
      : `https://cors-any.vercel.app/api/ajax/${encodeURIComponent(
          "https://animevietsub.tv/ajax/suggest"
        )}`,
    {
      method: "POST",
      body: form
    }
  ).then((e) => e.text())

  */
export default function PreSearch(html: string) {
  const $ = parserDom(html)

  return $("li:not(.ss-bottom)")
    .map((_i, li): DataItem => {
      const $li = $(li)
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      const anchor = $li.find("a")!
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      const image = anchor
        .attr("style")!
        .split(";")
        .find((item) => item.startsWith("background-image"))!
        .split(":", 3)
        .slice(1)
        .join(":")
        .trim()
        .replace(/^url\((?:'|")?/, "")
        .replace(/(?:'|")?\)$/, "")!
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      const path = getPathName(anchor.attr("href")!)
      const name = $li.find(".ss-title").text() ?? "unknown"
      const status = $li.find("p").text() ?? "unknown"
      return { image, path, name, status }
    })
    .toArray()
}
