import { describe, expect, test } from "vitest"

import { getRealSeasonId } from "./getRealSeasonId"

describe("getRealSeasonId", () => {
  test("normal id", () => {
    expect(getRealSeasonId("tonikaku-kawaii")).toEqual("tonikaku-kawaii")
  })
  test("$ id", () => {
    expect(getRealSeasonId("tham-thu-lung-danh-conan$23d32")).toEqual(
      "tham-thu-lung-danh-conan"
    )
  })
  test("multiple $id", () => {
    expect(getRealSeasonId("tham-tu-trieu-phu-$$1")).toEqual(
      "tham-tu-trieu-phu-$"
    )
  })
})
