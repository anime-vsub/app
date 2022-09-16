import { describe, test, expect } from "vitest"
import { Index } from "."
import { promises as fs } from "fs"
import html from "./__test__/data/index.txt?raw"

describe("Index", () => {
  test("normal", async () => {
    expect(JSON.parse(JSON.stringify(await Index(html)))).toEqual(
      JSON.parse(
        await fs.readFile(`${__dirname}/__test__/assets/index.json`, "utf8")
      )
    )
  })
})
