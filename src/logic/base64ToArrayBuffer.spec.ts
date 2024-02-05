import { describe, expect, test } from "vitest"

import { base64ToArrayBuffer } from "./base64ToArrayBuffer"

describe("base64ToArrayBuffer", () => {
  test("normal", () => {
    expect([
      ...new Uint8Array(base64ToArrayBuffer(btoa("hello world")))
    ]).toEqual([104, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100])
  })
})
