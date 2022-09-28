import type { HttpOptions } from "@capacitor-community/http"
import { get } from "src/logic/http"

import { base64ToArrayBuffer } from "./base64ToArrayBuffer"

export class XMLHttpRequestJava {
  currentTarget = this
  private readonly headers = new Headers()
  private resHeaders: Headers | null = null
  private url = ""
  private aborted = false

  public responseType?: HttpOptions["responseType"]
  public readyState: 0 | 1 | 2 | 3 | 4 = 0
  public status = 0
  public response?: ArrayBuffer
  public responseText?: string

  public onprogress?: (
    event: typeof this & { loaded: number; total: number }
  ) => void

  public onreadystatechange?: (event: typeof this) => void
  public onerror?: (event: typeof this) => void

  setRequestHeader(k: string, v: string) {
    this.headers.set(k, v)
  }

  getResponseHeader(k: string) {
    return this.resHeaders?.get(k)
  }

  getAllResponseHeaders(): string {
    return (
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      Array.from((this.headers as unknown as any).entries() ?? []) as [
        string,
        string
      ][]
    )
      .map(([key, val]) => `${key}: ${val}`)
      .join("\r\n")
  }

  open(method: "get" | "post", url: string) {
    if (method.toUpperCase() !== "GET") {
      console.warn("XMLHttpRequestJava not support POST method.")
    }
    this.url = url
  }

  abort() {
    this.aborted = true
  }

  async send() {
    try {
      if (this.aborted) return
      const res = await get({
        url: this.url,
        responseType: this.responseType,
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        headers: Object.fromEntries((this.headers as unknown as any).entries()),
      })

      res.data =
        this.responseType === "arraybuffer"
          ? typeof res.data === "object"
            ? res.data
            : base64ToArrayBuffer(res.data)
          : res.data

      if (this.aborted) return
      this.resHeaders = new Headers(res.headers)
      this.readyState = 2
      this.status = 200
      this.onprogress?.({ ...this, loaded: 0, total: res.data.length })
      this.onreadystatechange?.(this)

      if (this.responseType === "arraybuffer") this.response = res.data
      else this.responseText = res.data

      this.readyState = 4
      if (this.aborted) return
      this.onreadystatechange?.(this)
      this.onprogress?.({
        ...this,
        loaded: res.data.length,
        total: res.data.length,
      })
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    } catch (err: any) {
      this.onerror?.(err)
    }
  }
}
