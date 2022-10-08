import { getInfoTPost } from "../__helpers__/getInfoTPost"
import { parserDom } from "../__helpers__/parserDom"

import { getPathName } from "../__helpers__/getPathName"

export default function AjaxNotification(html: string) {
  const $ = parserDom(html)

  return $(".notifi-item")
    .map((_i, item) => {
      const image = $(item).find("img").attr("src")
      const [name, chap]  = $(item).find(".notification-text").find("strong").map((_i, item) => $(item).text())
      const time = $(item).find(".notification-time").text()
      const path = getPathName($(item).find("a").attr("href"))
      const id = $(item).find(".notification-delete").attr("data-id")
      
      return { image, name , chap , time, path, id }
    })
    .toArray()
}
