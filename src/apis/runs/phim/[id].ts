/* eslint-disable camelcase */
import type PhimIdParser from "src/apis/parser/phim/[id]"
import { useCache } from "src/apis/useCache"
import Worker from "src/apis/workers/phim/[id]?worker"
import { PostWorker } from "src/apis/wrap-worker"
import { get } from "src/logic/http"
import { useAuthStore } from "stores/auth"

export async function PhimId(seasonId: string) {
  return await useCache(`/phim/${seasonId}`, async () => {
    const { token_name, token_value } = useAuthStore()

    const { data: html } = await get(
      `/phim/${seasonId}/`,
      token_name && token_value
        ? {
            cookie: `${token_name}=${token_value}`
          }
        : undefined
    )
    const now = Date.now()

    return PostWorker<typeof PhimIdParser>(Worker, html, now)
  })
}
