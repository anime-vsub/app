import { test, describe, expect } from "vitest"
import { getText } from "./getText"
import { parserDOM } from "./parserDOM"

describe("getText", () => {
  test("exists text", () => {
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    expect(getText(parserDOM("<div>hello</div>").querySelector("div")!)).toBe(
      "hello"
    )
  })
  test("empty text", () => {
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    expect(getText(parserDOM("<div>").querySelector("div")!)).toBe("")
  })
})
