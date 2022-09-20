import { int } from "../utils/float"
import { getAttrs } from "../utils/getAttrs"
import { getPathName } from "../utils/getPathName"
import { getText } from "../utils/getText"

import { getInfoAnchor } from "./getInfoAnchor"

export type TPost = ReturnType<typeof getInfoTPost>

export function getInfoTPost(item: Element) {
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const path = getPathName(item.querySelector("a")!.getAttribute("href")!)
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const { src: image } = getAttrs(item.querySelector("img")!, ["src"])
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const name = getText(item.querySelector(".Title")!)

  const chap = item.querySelector(".mli-eps > i")?.textContent
  const rate = parseFloat(
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    getText(item.querySelector(".anime-avg-user-rating, .AAIco-star")!)
  )
  const views = int(
    item
      .querySelector(".Year")
      ?.textContent?.match(/[\d,]+/)?.[0]
      ?.replace(/,/g, "")
  )

  // =============== more =====================
  const quality = item.querySelector(".Qlty")?.textContent

  const process = item
    .querySelector(".AAIco-access_time")
    ?.textContent?.split("/") as unknown as ([string, string])

  const year = int(item.querySelector(".AAIco-date_range")?.textContent)

  const description = item.querySelector(".Description > p")?.textContent

  const studio = item.querySelector(".Studio")?.childNodes[1].nodeValue
  const genre = Array.from(item.querySelectorAll(".genre > a")).map(
    getInfoAnchor
  )
  const countdown =
    int(
      item.querySelector(".mli-timeschedule")?.getAttribute("data-timer_second")
    ) ?? undefined

  return {
    path,
    image,
    name,
    chap,
    rate,
    views,

    // === more ===
    quality,
    process,
    year,
    description,
    studio,
    genre,
    time_release:
      countdown === undefined ? undefined : Date.now() + countdown * 1e3,
  }
}
