import { CapacitorHttp } from "@capacitor/core"
import { isNative } from "src/constants"
import { base64ToArrayBuffer } from "src/logic/base64ToArrayBuffer"

async function getResponse(
  promise: Promise<{ data: string }>
): Promise<Response> {
  const stream = new ReadableStream({
    start(controller) {
      // eslint-disable-next-line promise/always-return
      void promise.then(({ data }) => {
        controller.enqueue(base64ToArrayBuffer(data))
        controller.close()
      })
    },
  })

  return new Response(stream, { status: 200 })
}

export async function fetchJava(
  url: string,
  options?: {
    headers?: Headers
    signal?: AbortSignal
  }
) {
  if (url.startsWith("data:app")) return fetch(url)
  if (!isNative) return CapacitorWebFetch(url, options)
  if (isNative && url.startsWith("/_capacitor_file_"))
    return CapacitorWebFetch(url, options)

  if (options?.signal?.aborted) throw new Error("ABORTED")

  const promise = CapacitorHttp.get({
    url,
    responseType: "arraybuffer",
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    headers: Object.fromEntries((options?.headers as any)?.entries() ?? []),
  })

  const response = getResponse(promise)
  Object.defineProperty(response, "url", {
    get: () => url,
  })

  return response

  // const res = await
  // // eslint-disable-next-line functional/no-throw-statement
  // if (options?.signal?.aborted) throw new Error("ABORTED")

  // return {
  //   async arrayBuffer() {
  //     return res.data as ArrayBuffer
  //   },
  //   async text() {
  //     return new TextDecoder().decode(await this.arrayBuffer())
  //   },
  //   get headers() {
  //     return new Headers(res.headers)
  //   },
  //   url: res.url,
  // }
}
