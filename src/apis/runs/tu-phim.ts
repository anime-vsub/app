/* eslint-disable camelcase */
import type TypeNormalValueParser from "src/apis/parser/[type_normal]/[value]"
import Worker from "src/apis/workers/[type_normal]/[value]?worker"
import { PostWorker } from "src/apis/wrap-worker"
import { i18n } from "src/boot/i18n"
import { get } from "src/logic/http"
import { useAuthStore } from "stores/auth"

export async function TuPhim(page: number) {
  const { token_name, token_value } = useAuthStore()

  if (!token_name || !token_value)
    throw new Error(
      i18n.global.t("errors.require_login_to", [
        i18n.global.t("xem-anime-da-theo-doi")
      ])
    )

  const { data: html } = await get(`/tu-phim/trang-${page}.html`, {
    cookie: `${token_name}=${token_value}`
  })

  const now = Date.now()

  return PostWorker<typeof TypeNormalValueParser>(Worker, html, now, true)
}
