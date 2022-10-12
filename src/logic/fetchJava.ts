
import { base64ToArrayBuffer } from "./base64ToArrayBuffer"
import { get } from "src/logic/http"

export async function fetchJava(url: string, options?: {
  headers?: Headers
  signal?: Signal
}) {
  if (options?.signal?.aborted) throw new Error( "ABORTED")

  const res = await get({
    url,
    responseType: "arraybuffer",
    headers: Object.fromEntries(options?.headers?.entries() ?? [])
  })

  if (options?.signal?.aborted) throw new Error( "ABORTED")

  return {
    async arrayBuffer() {
      return typeof res.data === "object"
            ? res.data
            : base64ToArrayBuffer(res.data)
    },
    async text() {
      return new TextDecoder().decode(
        await this.arrayBuffer()
      )
    },
    get headers() {
      return new Headers(res.headers)
    },
    url: res.url
  }
}
