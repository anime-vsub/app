import { HOST_CURL } from "./../constants"
import { addHostUrlImage, removeHostUrlImage } from "./urlImage"

describe("urlImage", () => {
  describe("remove host", () => {
    test("should url is HOST_CURL", () => {
      expect(removeHostUrlImage(`https://${HOST_CURL}/image.png`)).toEqual(
        "$@/image.png"
      )
      expect(removeHostUrlImage(`https://cdn.${HOST_CURL}/image.png`)).toEqual(
        "cdn.$@/image.png"
      )
      expect(removeHostUrlImage(`https://${HOST_CURL}:443/image.png`)).toEqual(
        "$@:443/image.png"
      )
      expect(
        removeHostUrlImage(`https://cdn.${HOST_CURL}:443/image.png`)
      ).toEqual("cdn.$@:443/image.png")
    })
    test("should url is not HOST_CURL", () => {
      expect(removeHostUrlImage("https://example.com/image.png")).toEqual(
        "https://example.com/image.png"
      )
      expect(removeHostUrlImage("https://cdn.example.com/image.png")).toEqual(
        "https://cdn.example.com/image.png"
      )
      expect(removeHostUrlImage("https://example.com:443/image.png")).toEqual(
        "https://example.com:443/image.png"
      )
      expect(
        removeHostUrlImage("https://cdn.example.com:443/image.png")
      ).toEqual("https://cdn.example.com:443/image.png")
    })
  })
  describe("add host", () => {
    test("should url is removed host", () => {
      expect(addHostUrlImage("$@/image.png")).toEqual(
        `https://${HOST_CURL}/image.png`
      )
      expect(addHostUrlImage("$@:433/image.png")).toEqual(
        `https://${HOST_CURL}:433/image.png`
      )
      expect(addHostUrlImage("cdn.$@/image.png")).toEqual(
        `https://cdn.${HOST_CURL}/image.png`
      )
      expect(addHostUrlImage("cdn.$@:433/image.png")).toEqual(
        `https://cdn.${HOST_CURL}:433/image.png`
      )
    })
    test("should url is not remove host", () => {
      expect(addHostUrlImage("https://example.com/image.png")).toEqual(
        "https://example.com/image.png"
      )
      expect(addHostUrlImage(`https://${HOST_CURL}/image.png`)).toEqual(
        `https://${HOST_CURL}/image.png`
      )
      expect(addHostUrlImage("https://cdn.example.com/image.png")).toEqual(
        "https://cdn.example.com/image.png"
      )
      expect(addHostUrlImage("https://example.com:443/image.png")).toEqual(
        "https://example.com:443/image.png"
      )
      expect(addHostUrlImage("https://cdn.example.com:443/image.png")).toEqual(
        "https://cdn.example.com:443/image.png"
      )
    })
  })
})
