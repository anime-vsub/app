import { promises as fs } from "fs"

import { describe, expect, test } from "vitest"

import html from "../../__test__/apis/data/phim/tonikaku-kawaii-a3860.txt?raw"

import { PhimId } from "./[id]"

describe("[id]", () => {
  test("normal", async () => {
    import.meta.env.DATA = html

    const asset = JSON.parse(
      await fs.readFile(
        // eslint-disable-next-line n/no-path-concat
        `${__dirname}/../../__test__/apis/assets/tonikaku-kawaii-a3860.json`,
        "utf8"
      )
    ) as Awaited<ReturnType<typeof PhimId>>
    const result = JSON.parse(JSON.stringify(await PhimId("")))

    asset.toPut.forEach((item, index) => {
      item.time_release = result.toPut[index].time_release
    })

    expect(result).toEqual(asset)
  })
})
