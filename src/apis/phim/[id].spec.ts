import { promises as fs } from "fs"

import { describe, expect, test } from "vitest"

import html from "../__test__/data/phim/tonikaku-kawaii-a3860.txt?raw"

// eslint-disable-next-line camelcase
import { Phim_Id } from "./[id]"


describe("[id]", () => {
  test("normal", async () => {
    expect(JSON.parse(JSON.stringify(await Phim_Id(html)))).toEqual(
      JSON.parse(
        await fs.readFile(
          // eslint-disable-next-line n/no-path-concat
          `${__dirname}/../__test__/assets/tonikaku-kawaii-a3860.json`,
          "utf8"
        )
      )
    )
  })
})
