import { HOST_CURL } from "src/constants"

import { redirectOldDomainCDN } from "./redirectOldDomainCDN"

describe("redirectOldDomainCDN", () => {
  test("should url new domain", () => {
    expect(redirectOldDomainCDN(`http://${HOST_CURL}/foo`)).toBe(
      `http://${HOST_CURL}/foo`
    )
    expect(redirectOldDomainCDN(`https://${HOST_CURL}/foo`)).toBe(
      `https://${HOST_CURL}/foo`
    )
  })
  test("should url old domain", () => {
    expect(redirectOldDomainCDN("http://animevietsub.cc/foo")).toBe(
      `http://${HOST_CURL}/foo`
    )
    expect(redirectOldDomainCDN("https://animevietsub.cc/foo")).toBe(
      `https://${HOST_CURL}/foo`
    )
  })
})
