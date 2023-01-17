import { removeFirstSlash } from "./removeFirstSlash"

describe("removeFirstSlash", () => {
  it("should remove the first slash in a string", () => {
    expect(removeFirstSlash("/hello")).toEqual("hello")
    expect(removeFirstSlash("hello")).toEqual("hello")
  })
})
