import { get } from "src/logic/http"

export async function fetchJava(
  url: string,
  options?: {
    headers?: Headers
    signal?: AbortSignal
  }
) {
  // eslint-disable-next-line functional/no-throw-statement
  if (options?.signal?.aborted) throw new Error("ABORTED")

  const res = await get({
    url,
    responseType: "arraybuffer",
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    headers: Object.fromEntries((options?.headers as any)?.entries() ?? []),
  })

  // eslint-disable-next-line functional/no-throw-statement
  if (options?.signal?.aborted) throw new Error("ABORTED")

  return {
    async arrayBuffer() {
      return res.data as ArrayBuffer
    },
    async text() {
      return new TextDecoder().decode(await this.arrayBuffer())
    },
    get headers() {
      return new Headers(res.headers)
    },
    url: res.url,
  }
}
