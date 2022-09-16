import { describe, test, expect } from "vitest"
import { parserDOM } from "./parserDOM"

describe("parserDOM", () => {
   test("normal", () => {
expect(parserDOM("<div>hello world").querySelector("div")!.textContent).toBe("hello world")
   })
})
