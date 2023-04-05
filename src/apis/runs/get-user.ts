import type AccountInfoParser from "src/apis/parser/account/info"
import Worker from "src/apis/workers/account/info?worker"
import { i18n } from "src/boot/i18n"
import { C_URL } from "src/constants"
import { post } from "src/logic/http"
import { Md5 } from "ts-md5"

import { PostWorker } from "../wrap-worker"

export async function GetUser(token_name: string, token_value: string) {
   if (!token_name || !token_value)
     // eslint-disable-next-line functional/no-throw-statement
     throw new Error(
       i18n.global.t("errors.require_login_to", [
         i18n.global.t("xem-anime-da-theo-doi"),
       ])
     )
 
   const { data: html } = await get(`/account/info`, {
     cookie: `${token_name}=${token_value}`,
   })
 

  if (html.includes("user-name-text")) {
    // login success
    // parse

    return {
      ...(await PostWorker<typeof AccountInfoParser>(Worker, html)),
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      cookie: new Headers(headers).get("set-cookie")!,
    }
  } else {
    // eslint-disable-next-line functional/no-throw-statement
    throw new Error(i18n.global.t("dang-nhap-that-bai"))
  }
}
