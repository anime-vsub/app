import fs from "fs/promises"

import { describe, expect, test } from "vitest"

import html from "../../__test__/apis/data/lich-chieu-phim.txt?raw"

import LichChieuPhim from "./lich-chieu-phim"

describe("lich-chieu-phim", () => {
  test("normal", async () => {
    const asset = JSON.parse(
      await fs.readFile(
        // eslint-disable-next-line n/no-path-concat
        `${__dirname}/../../__test__/apis/assets/lich-chieu-phim.json`,
        "utf8"
      )
    ) as Awaited<ReturnType<typeof LichChieuPhim>>
    const result = JSON.parse(JSON.stringify(await LichChieuPhim(html, 0)))

    asset.forEach(({ items }, index1) =>
      items.forEach((item, index2) => {
        item.time_release = result[index1].items[index2].time_release
      })
    )

    expect(result).toEqual(asset)
  })
})
