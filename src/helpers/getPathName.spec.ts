import { describe, expect, test } from "vitest"

import { getPathName } from "./getPathName"

describe("getPathName", () => {
  test("url root", () => {
    expect(getPathName("https://google.com")).toBe("/")
  })
  test("url pathname", () => {
    expect(getPathName("https://google.com/not_found")).toBe("/not_found")
  })
  test("pathname", () => {
    expect(getPathName("/not_found")).toBe("/not_found")
  })
  test("not protocol", () => {
    expect(getPathName("//google.com/not_found")).toBe("/not_found")
  })
  test("relative path", () => {
    expect(getPathName("/not_found")).toBe("/not_found")
    expect(getPathName("./not_found")).toBe("/not_found")
  })
})
