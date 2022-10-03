import { promises as fs } from "fs"

import { describe, expect, test } from "vitest"

import html from "../__test__/apis/data/index.txt?raw"

import { Index } from "."

describe("Index", () => {
  test("normal", async () => {
    import.meta.env.DATA = html

    const asset = JSON.parse(
      await fs.readFile(
        // eslint-disable-next-line n/no-path-concat
        `${__dirname}/../__test__/apis/assets/index.json`,
        "utf8"
      )
    ) as Awaited<ReturnType<typeof Index>>
    const result = JSON.parse(JSON.stringify(await Index()))

    asset.preRelease.forEach((item, index) => {
      item.time_release = result.preRelease[index].time_release
    })

    expect(result).toEqual(asset)
  })
})
