import { describe, expect, test } from "vitest"

import { parseMdBasic } from "./parseMdBasic"

describe("parseMdBasic", () => {
  test("normal", () => {
    expect(
      parseMdBasic(`- [2189766](http://github.com/anime-vsub/desktop-web/commit/21897664102f3f6a1ecffa685382384177d16861) - chore: release v0.0.68
   - [f9f4bf9](http://github.com/anime-vsub/desktop-web/commit/f9f4bf93cbde0d206ad2b4c0d3b9747c0d3e3a00) - change host \`api\`
   - [5be8051](http://github.com/anime-vsub/desktop-web/commit/5be80518dcf0139ce16c500dbca643e0d94e45a8) - format`)
    )
      .toEqual(`<li> <a href="http://github.com/anime-vsub/desktop-web/commit/21897664102f3f6a1ecffa685382384177d16861" target="_blank" alt="2189766">2189766</a> - chore: release v0.0.68</li>
   - <a href="http://github.com/anime-vsub/desktop-web/commit/f9f4bf93cbde0d206ad2b4c0d3b9747c0d3e3a00" target="_blank" alt="f9f4bf9">f9f4bf9</a> - change host <strong>api</strong>
   - <a href="http://github.com/anime-vsub/desktop-web/commit/5be80518dcf0139ce16c500dbca643e0d94e45a8" target="_blank" alt="5be8051">5be8051</a> - format`)
  })
})
