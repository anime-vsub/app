import { promises as fs } from "fs"

import { describe, expect, test } from "vitest"

import html from "./__test__/data/index.txt?raw"

import { Index } from "."

describe("Index", () => {
  test("normal", async () => {
    expect(JSON.parse(JSON.stringify(await Index(html)))).toEqual(
      JSON.parse(
        // eslint-disable-next-line n/no-path-concat
        await fs.readFile(`${__dirname}/__test__/assets/index.json`, "utf8")
      )
    )
  })
})
