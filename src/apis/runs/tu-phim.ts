/* eslint-disable camelcase */
import type TypeNormalValueParser from "src/apis/parser/[type_normal]/[value]"
import Worker from "src/apis/workers/[type_normal]/[value]?worker"
import { PostWorker } from "src/apis/wrap-worker"
import { get } from "src/logic/http"
import { useAuthStore } from "stores/auth"

export async function TuPhim(page: number) {
  const { token_name, token_value } = useAuthStore()

  if (!token_name || !token_value)
    // eslint-disable-next-line functional/no-throw-statement
    throw new Error("TOKEN_REQUIRED_FOR_NOTIFICATION")

  const { data: html } = await get(`/tu-phim/trang-${page}.html`, {
    cookie: `${token_name}=${token_value}`,
  })

  const now = Date.now()

  return PostWorker<typeof TypeNormalValueParser>(Worker, html, now, true)
}
