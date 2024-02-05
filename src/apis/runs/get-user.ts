import type AccountInfoParser from "src/apis/parser/account/info"
import Worker from "src/apis/workers/account/info?worker"
import { i18n } from "src/boot/i18n"
import { get } from "src/logic/http"

import { PostWorker } from "../wrap-worker"

// eslint-disable-next-line camelcase
export async function GetUser(token_name: string, token_value: string) {
  // eslint-disable-next-line camelcase
  if (!token_name || !token_value)
    throw new Error(
      i18n.global.t("errors.require_login_to", [
        i18n.global.t("xem-anime-da-theo-doi")
      ])
    )

  const { data: html, headers } = await get("/account/info", {
    // eslint-disable-next-line camelcase
    cookie: `${token_name}=${token_value}`
  })

  if (html.includes("user-name-text")) {
    // login success
    // parse

    return {
      ...(await PostWorker<typeof AccountInfoParser>(Worker, html)),
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      cookie: new Headers(headers).get("set-cookie")!
    }
  } else {
    throw new Error(i18n.global.t("dang-nhap-that-bai"))
  }
}
