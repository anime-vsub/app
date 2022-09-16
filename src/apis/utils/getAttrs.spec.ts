import { describe, expect, test } from "vitest"
import { getAttrs } from "./getAttrs"

describe("getAttrs", () => {
  const el = document.createElement("a")
  el.setAttribute("href", "http://example.com")
  el.setAttribute("alt", "example")

  test("one attr", () => {
    expect(getAttrs(el, ["href"])).toEqual({ href: "http://example.com" })
  })
  test("multi attrs", () => {
    expect(getAttrs(el, ["href", "alt"])).toEqual({
      href: "http://example.com",
      alt: "example",
    })
  })
})
