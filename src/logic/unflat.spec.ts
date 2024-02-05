import { describe, expect, test } from "vitest"

import { unflat } from "./unflat"

describe("unflat", () => {
  test("length % size = 0", () => {
    expect(unflat([1, 2, 3, 4, 5, 6, 7, 8], 2)).toEqual([
      [1, 2],
      [3, 4],
      [5, 6],
      [7, 8]
    ])
  })
  test("length % size > 0", () => {
    expect(unflat([1, 2, 3, 4, 5, 6, 7], 2)).toEqual([
      [1, 2],
      [3, 4],
      [5, 6],
      [7]
    ])
  })
})
