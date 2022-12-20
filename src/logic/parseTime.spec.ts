import { describe, expect, test } from "vitest"

import { parseTime } from "./parseTime"

describe("parseTime", () => {
  test("seconds", () => {
    expect(parseTime(10)).toEqual("00:10")
    expect(parseTime(59)).toEqual("00:59")
  })
  test("minutes", () => {
    expect(parseTime(60)).toEqual("01:00")
    expect(parseTime(61)).toEqual("01:01")
    expect(parseTime(60 * 60 - 1)).toEqual("59:59")
  })
  test("hours", () => {
    expect(parseTime(3600)).toEqual("01:00:00")
    expect(parseTime(3601)).toEqual("01:00:01")
    expect(parseTime(60 * 60 * 10)).toEqual("10:00:00")
    expect(parseTime(60 * 60 * 100)).toEqual("100:00:00")
  })
})
