import { describe, expect, test } from "vitest"

import { formatView } from "./formatView"

describe("formatView", () => {
  test("< 1N", () => {
    expect(formatView(999)).toBe("999")
  })
  test("< 1Tr", () => {
    expect(formatView(1000)).toBe("1N")
    expect(formatView(1500)).toBe("1,5N")
    expect(formatView(1523)).toBe("1,52N")
  })
  test("< 1T", () => {
    expect(formatView(1_000_000)).toBe("1Tr")
    expect(formatView(1_500_000)).toBe("1,5Tr")
    expect(formatView(1_050_000)).toBe("1,05Tr")
  })
  test("< 1V", () => {
    expect(formatView(1_000_000_000)).toBe("1T")
  })
})
