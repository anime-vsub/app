import { describe, expect, test } from "vitest"
import html from "../../__test__/data/phim/tonikaku-kawaii-a3860/xem-phim-72839.txt?raw"
import html2 from "../../__test__/data/phim/hataraku-maou-sama-2nd-season-a4257/tap-01-85227.txt?raw"
import { Phim_Id_Chap } from "./[chap]"
import { promises as fs } from "fs"

describe("[chap]", () => {
  test("no update", async () => {
    expect(JSON.parse(JSON.stringify(await Phim_Id_Chap(html)))).toEqual(
      JSON.parse(
        await fs.readFile(
          `${__dirname}/../../__test__/assets/tonikaku-kawaii-a3860/xem-phim-72839.json`,
          "utf8"
        )
      )
    )
  })
  test("exists update", async () => {
    expect(JSON.parse(JSON.stringify(await Phim_Id_Chap(html2)))).toEqual(
      JSON.parse(
        await fs.readFile(
          `${__dirname}/../../__test__/assets/hataraku-maou-sama-2nd-season-a4257/tap-01-85227.json`,
          "utf8"
        )
      )
    )
  })
})
