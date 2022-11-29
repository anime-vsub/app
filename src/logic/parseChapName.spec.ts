import { describe, expect, test } from "vitest"

import { parseChapName } from "./parseChapName"

describe("parseChapName", () => {
  test("normal name", () => {
    expect(parseChapName("01")).toEqual("tap-01")
  })
  test("upper name", () => {
    expect(parseChapName("12_End")).toEqual("tap-12_end")
  })
  test("space name", () => {
    expect(parseChapName("12 End")).toEqual("tap-12-end")
  })
  test("accents name", () => {
    expect(parseChapName("Bản BD")).toEqual("tap-ban-bd")
  })
})
