import { getQualityByLabel } from "./get-quality-by-label"

describe("get-quality-by-label", () => {
  test("should label is HD", () => {
    expect(getQualityByLabel("HD")).toBe("720p")
  })
  test("should label is 720p", () => {
    expect(getQualityByLabel("720p")).toBe("720p")
  })
  test("should label is FHD|HD", () => {
    expect(getQualityByLabel("FHD|HD")).toBe("1080p|720p")
  })
})
