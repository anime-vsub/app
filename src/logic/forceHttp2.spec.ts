import { forceHttp2 } from "./forceHttp2"

describe("forceHttp2", () => {
  test("should use http 1", () => {
    expect(forceHttp2("http://example.com/image.png")).toEqual(
      "https://example.com/image.png"
    )
  })
  test("should use http 2", () => {
    expect(forceHttp2("https://example.com/image.png")).toEqual(
      "https://example.com/image.png"
    )
  })
})
