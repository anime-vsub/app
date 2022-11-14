import type AccountInfoParser from "src/apis/parser/account/info"
import Worker from "src/apis/workers/account/info?worker"
import { i18n } from "src/boot/i18n"
import { C_URL } from "src/constants"
import { post } from "src/logic/http"
import { Md5 } from "ts-md5"

import { PostWorker } from "../wrap-worker"

export async function DangNhap(email: string, password: string) {
  // eslint-disable-next-line camelcase
  const password_md5 = Md5.hashAsciiStr(password)

  const { data: html, headers } = await post(
    `/account/login/?_fxRef=${C_URL}/account/info`,
    {
      email,
      password: "",
      // eslint-disable-next-line camelcase
      password_md5,
      save_password: "1",
      submit: "",
    }
  )

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
