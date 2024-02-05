import { get } from "src/logic/http"

import { base64ToArrayBuffer } from "./base64ToArrayBuffer"

export async function fetchJava(
  url: string,
  options?: {
    headers?: Headers
    signal?: AbortSignal
  }
) {
  if (options?.signal?.aborted) throw new Error("ABORTED")

  const res = await get({
    url,
    responseType: "arraybuffer",
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    headers: Object.fromEntries((options?.headers as any)?.entries() ?? [])
  })

  if (options?.signal?.aborted) throw new Error("ABORTED")

  return {
    async arrayBuffer() {
      return typeof res.data === "object"
        ? res.data
        : base64ToArrayBuffer(res.data)
    },
    async text() {
      return new TextDecoder().decode(await this.arrayBuffer())
    },
    get headers() {
      return new Headers(res.headers)
    },
    url: res.url
  }
}
