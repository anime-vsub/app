import type {
  HlsConfig,
  LoaderCallbacks,
  LoaderConfiguration,
  LoaderContext,
  LoaderOnProgress,
  LoaderResponse
} from "hls.js"
import Hls from "hls.js"

function getRequestParameters(
  context: LoaderContext,
  signal: AbortSignal
): RequestInit & { headers: Headers } {
  const initParams: RequestInit & { headers: Headers } = {
    method: "GET",
    mode: "cors",
    credentials: "same-origin",
    signal,
    headers: new self.Headers(Object.assign({}, context.headers))
  }

  if (context.rangeEnd) {
    initParams.headers.set(
      "Range",
      "bytes=" + context.rangeStart + "-" + String(context.rangeEnd - 1)
    )
  }

  return initParams
}

const BYTERANGE = /(\d+)-(\d+)\/(\d+)/

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

class FetchError extends Error {
  public code: number
  public details: any
  constructor(message: string, code: number, details: any) {
    super(message)
    this.code = code
    this.details = details
  }
}

export class HlsPatched extends Hls {
  constructor(
    userConfig: Partial<HlsConfig>,
    fetch: (request: Request) => Promise<Response>,
    onSlow?: () => void
  ) {
    super(userConfig)

    this.config.loader = class extends this.config.loader {
      load(
        this: any,
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

        const now = performance.now()
        fetch(this.request as Request)
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

            let data: any

            if (isArrayBuffer) {
              data = response.arrayBuffer()
            }
            if (context.responseType === "json") {
              data = response.json()
            }
            data = response.text()

            return data
          })
          .then((responseData: string | ArrayBuffer) => {
            const dur = performance.now() - now
            if (dur > 14_000) {
              onSlow?.()
            }

            const response = this.response
            if (!response) {
              throw new Error("loader destroyed")
            }
            self.clearTimeout(this.requestTimeout)
            stats.loading.end = Math.max(
              self.performance.now(),
              stats.loading.first
            )
            const total = (responseData as unknown as any)[LENGTH]
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
  }
}
