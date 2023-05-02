import type {
  LoaderCallbacks,
  LoaderConfiguration,
  LoaderContext,
  LoaderOnProgress,
} from "hls.js"
import type Hls from "hls.js"

import { fetchJava } from "./fetchJava"

function getRequestParameters(context: LoaderContext, signal): RequestInit {
  const initParams = {
    method: "GET",
    mode: "cors",
    credentials: "same-origin",
    signal,
    headers: new self.Headers(Object.assign({}, context.headers)),
  } as const

  if (context.rangeEnd) {
    initParams.headers.set(
      "Range",
      "bytes=" + context.rangeStart + "-" + String(context.rangeEnd - 1)
    )
  }

  return initParams
}

class FetchError extends Error {
  public code: number
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  public details: any
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  constructor(message: string, code: number, details: any) {
    super(message)
    this.code = code
    this.details = details
  }
}

export function patcher(hls: Hls) {
  if (hls.config.loader.prototype.is_patched) return

  hls.config.loader.prototype.is_patched = true
  hls.config.loader.prototype.load = function load(
    context: LoaderContext,
    config: LoaderConfiguration,
    callbacks: LoaderCallbacks<LoaderContext>
  ): void {
    const stats = this.stats
    if (stats.loading.start) {
      // eslint-disable-next-line functional/no-throw-statement
      throw new Error("Loader can only be used once.")
    }
    stats.loading.start = self.performance.now()

    const initParams = getRequestParameters(context, this.controller.signal)
    const onProgress: LoaderOnProgress<LoaderContext> | undefined =
      callbacks.onProgress
    const isArrayBuffer = context.responseType === "arraybuffer"
    const LENGTH = isArrayBuffer ? "byteLength" : "length"

    this.context = context
    this.config = config
    this.callbacks = callbacks
    this.request = this.fetchSetup(context, initParams)
    self.clearTimeout(this.requestTimeout)
    this.requestTimeout = self.setTimeout(() => {
      this.abortInternal()
      callbacks.onTimeout(stats, context, this.response)
    }, config.timeout)

    fetchJava(this.request.url, {
      headers: this.request.headers,
      signal: this.request.signal,
    })
      .then(
        async (res) =>
          new Response(await res.arrayBuffer(), {
            status: 200,
            headers: res.headers,
          })
      )
      .then((response: Response): Promise<string | ArrayBuffer> => {
        this.response = this.loader = response

        if (!response.ok) {
          const { status, statusText } = response
          // eslint-disable-next-line functional/no-throw-statement
          throw new FetchError(
            statusText || "fetch, bad network response",
            status,
            response
          )
        }
        stats.loading.first = Math.max(
          self.performance.now(),
          stats.loading.start
        )
        stats.total = parseInt(response.headers.get("Content-Length") || "0")

        if (onProgress && Number.isFinite(config.highWaterMark)) {
          return this.loadProgressively(
            response,
            stats,
            context,
            config.highWaterMark,
            onProgress
          )
        }

        if (isArrayBuffer) {
          return response.arrayBuffer()
        }
        return response.text()
      })
      .then((responseData: string | ArrayBuffer) => {
        const { response } = this
        self.clearTimeout(this.requestTimeout)
        stats.loading.end = Math.max(
          self.performance.now(),
          stats.loading.first
        )
        const total = responseData[LENGTH]
        if (total) {
          stats.loaded = stats.total = total
        }

        const loaderResponse = {
          url: response.url,
          data: responseData,
        }

        // eslint-disable-next-line promise/always-return
        if (onProgress && !Number.isFinite(config.highWaterMark)) {
          onProgress(stats, context, responseData, response)
        }

        callbacks.onSuccess(loaderResponse, stats, context, response)
      })
      .catch((error) => {
        self.clearTimeout(this.requestTimeout)
        if (stats.aborted) {
          return
        }
        // CORS errors result in an undefined code. Set it to 0 here to align with XHR's behavior
        // when destroying, 'error' itself can be undefined
        const code: number = !error ? 0 : error.code || 0
        const text: string = !error ? null : error.message
        callbacks.onError({ code, text }, context, error ? error.details : null)
      })
  }
}
