import fs from "fs/promises"

import { describe, expect, test } from "vitest"

import html from "../../__test__/apis/data/bang-xep-hang.txt?raw"

import { BangXepHangType } from "./[type]"

describe("bang-xep-hang", () => {
  test("normal", async () => {
    import.meta.env.DATA = html

    const asset = JSON.parse(
      await fs.readFile(
        // eslint-disable-next-line n/no-path-concat
        `${__dirname}/../../__test__/apis/assets/bang-xep-hang.json`,
        "utf8"
      )
    ) as Awaited<ReturnType<typeof BangXepHangType>>
    const result = JSON.parse(JSON.stringify(await BangXepHangType()))

    expect(result).toEqual(asset)
  })
})
