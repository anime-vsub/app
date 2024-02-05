import html from "src/__test__/apis/data/account/info.txt?raw"
import { describe, expect, test } from "vitest"

import AccountInfo from "./info"

describe("info", () => {
  test("normal", () => {
    expect(AccountInfo(html)).toEqual({
      avatar: "http://cdn.animevietsub.cc/data/avatar/user-35738.jpg",
      name: "しん",
      email: "oppoepgergh@gmail.com",
      username: "tachib.shin",
      sex: "male"
    })
  })
})
