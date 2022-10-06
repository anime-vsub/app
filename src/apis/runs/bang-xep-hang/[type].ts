import type BangXepHangTypeParser from "src/apis/parser/bang-xep-hang/[type]"
import { useCache } from "src/apis/useCache"
import Worker from "src/apis/workers/bang-xep-hang/[type]?worker"
import { PostWorker } from "src/apis/wrap-worker"
import { get } from "src/logic/http"

export async function BangXepHangType(type?: string) {
  return await useCache(`/bang-xep-hang/${type}`, async () => {
    const { data: html } = await get(`/bang-xep-hang/${type ?? ""}`)

    return PostWorker<typeof BangXepHangTypeParser>(Worker, html)
  })
}
