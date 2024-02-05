import type {
  LoaderCallbacks,
  LoaderConfiguration,
  LoaderContext,
  LoaderOnProgress,
  LoaderResponse
} from "hls.js"
import type Hls from "hls.js"

import { fetchJava } from "./fetchJava"

const BYTERANGE = /(\d+)-(\d+)\/(\d+)/

function getRequestParameters(
  context: LoaderContext,
  signal: AbortSignal
): RequestInit {
  const initParams = {
    method: "GET",
    mode: "cors",
    credentials: "same-origin",
    signal,
    headers: new self.Headers(Object.assign({}, context.headers))
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
      throw new Error("Loader can only be used once.")
    }
    stats.loading.start = self.performance.now()

    const initParams = getRequestParameters(context, this.controller.signal)
    const onProgress: LoaderOnProgress<LoaderContext> | undefined =
      callbacks.onProgress
    const isArrayBuffer = context.responseType === "arraybuffer"
    const LENGTH = isArrayBuffer ? "byteLength" : "length"
    const { maxTimeToFirstByteMs, maxLoadTimeMs } = config.loadPolicy

    this.context = context
    this.config = config
    this.callbacks = callbacks
    this.request = this.fetchSetup(context, initParams)
    self.clearTimeout(this.requestTimeout)
    config.timeout =
      maxTimeToFirstByteMs && Number.isFinite(maxTimeToFirstByteMs)
        ? maxTimeToFirstByteMs
        : maxLoadTimeMs
    this.requestTimeout = self.setTimeout(() => {
      this.abortInternal()
      callbacks.onTimeout(stats, context, this.response)
    }, config.timeout)

    fetchJava(this.request.url, {
      headers: this.request.headers,
      signal: this.request.signal
    })
      .then(
        async (res) =>
          new Response(await res.arrayBuffer(), {
            status: 200,
            headers: res.headers
          })
      )
      .then((response: Response): Promise<string | ArrayBuffer> => {
        this.response = this.loader = response

        const first = Math.max(self.performance.now(), stats.loading.start)

        self.clearTimeout(this.requestTimeout)
        config.timeout = maxLoadTimeMs
        this.requestTimeout = self.setTimeout(
          () => {
            this.abortInternal()
            callbacks.onTimeout(stats, context, this.response)
          },
          maxLoadTimeMs - (first - stats.loading.start)
        )

        if (!response.ok) {
          const { status, statusText } = response

          throw new FetchError(
            statusText || "fetch, bad network response",
            status,
            response
          )
        }
        stats.loading.first = first

        stats.total = getContentLength(response.headers) || stats.total

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
        if (context.responseType === "json") {
          return response.json()
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
        const total = (responseData as string)[LENGTH as "length"]
        if (total) {
          stats.loaded = stats.total = total
        }

        const loaderResponse: LoaderResponse = {
          url: response.url,
          data: responseData,
          code: response.status
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
        callbacks.onError(
          { code, text },
          context,
          error ? error.details : null,
          stats
        )
      })
  }
}

function getByteRangeLength(byteRangeHeader: string): number | undefined {
  const result = BYTERANGE.exec(byteRangeHeader)
  if (result) {
    return parseInt(result[2]) - parseInt(result[1]) + 1
  }
}

function getContentLength(headers: Headers): number | undefined {
  const contentRange = headers.get("Content-Range")
  if (contentRange) {
    const byteRangeLength = getByteRangeLength(contentRange)
    if (Number.isFinite(byteRangeLength)) {
      return byteRangeLength
    }
  }
  const contentLength = headers.get("Content-Length")
  if (contentLength) {
    return parseInt(contentLength)
  }
}
