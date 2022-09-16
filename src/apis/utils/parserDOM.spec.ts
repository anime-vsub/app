import { describe, expect, test } from "vitest"

import { parserDOM } from "./parserDOM"

describe("parserDOM", () => {
  test("normal", () => {
    expect(
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      parserDOM("<div>hello world").querySelector("div")!.textContent
    ).toBe("hello world")
  })
})
