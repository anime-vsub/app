import fs from "fs/promises"

import { describe, expect, test } from "vitest"

import html from "../../../__test__/apis/data/bang-xep-hang.txt?raw"

import BangXepHangType from "./[type]"

describe("bang-xep-hang", () => {
  test("normal", async () => {
    const asset = JSON.parse(
      await fs.readFile(
        // eslint-disable-next-line n/no-path-concat
        `${__dirname}/../../../__test__/apis/assets/bang-xep-hang.json`,
        "utf8"
      )
    ) as Awaited<ReturnType<typeof BangXepHangType>>
    const result = JSON.parse(JSON.stringify(await BangXepHangType(html)))

    expect(result).toEqual(asset)
  })
})
