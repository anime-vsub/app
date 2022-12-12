import { describe, expect, test } from "vitest"

import ranks from "./ranks"

describe("ranks", () => {
  test.each(ranks)("index $i is string", (item) => {
    expect(typeof item).toEqual("string")
  })
})
