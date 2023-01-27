import html from "src/__test__/apis/data/account/info.txt?raw"
import { C_URL } from "src/constants"
import { describe, expect, test } from "vitest"

import AccountInfo from "./info"

describe("info", () => {
  test("normal", () => {
    expect(AccountInfo(html)).toEqual({
      avatar: `http://cdn.${C_URL.slice(
        C_URL.indexOf("://") + 3
      )}/data/avatar/user-35738.jpg`,
      name: "しん",
      email: "oppoepgergh@gmail.com",
      username: "tachib.shin",
      sex: "male",
    })
  })
})
