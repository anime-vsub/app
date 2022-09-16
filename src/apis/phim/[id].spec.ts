import { describe, expect, test } from "vitest"
import { Phim_Id } from "./[id]"

import html from "../__test__/data/phim/tonikaku-kawaii-a3860.txt?raw"
import { promises as fs } from "fs"

describe("[id]", () => {
  test("normal", async () => {
    expect(JSON.parse(JSON.stringify(await Phim_Id(html)))).toEqual(
      JSON.parse(
        await fs.readFile(
          `${__dirname}/../__test__/assets/tonikaku-kawaii-a3860.json`,
          "utf8"
        )
      )
    )
  })
})
