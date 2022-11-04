/* eslint-disable @typescript-eslint/no-explicit-any */
import { get } from "src/logic/http"

import { base64ToArrayBuffer } from "./base64ToArrayBuffer"

export class XMLHttpRequestJava {
  currentTarget = this
  private readonly headers = new Headers()
  private resHeaders: Headers | null = null
  private url = ""
  private aborted = false

  public responseType?: PostOptions["responseType"]
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
        headers: Object.fromEntries((this.headers as unknown as any).entries()),
      })

      res.data =
        this.responseType === "arraybuffer"
          ? typeof res.data === "object"
            ? res.data
            : base64ToArrayBuffer(res.data)
          : res.data

      if (this.aborted) return

      const size =
        typeof res.data === "string" ? res.data.length : res.data.byteLength
      this.resHeaders = new Headers(res.headers)
      this.readyState = 2
      this.status = 200
      this.onprogress?.({
        ...this,
        loaded: 0,
        total: size,
      })
      this.onreadystatechange?.(this)

      if (this.responseType === "arraybuffer")
        this.response = res.data as ArrayBuffer
      else this.responseText = res.data as string

      this.readyState = 4
      if (this.aborted) return
      this.onreadystatechange?.(this)
      this.onprogress?.({
        ...this,
        loaded: size,
        total: size,
      })
    } catch (err: any) {
      this.onerror?.(err)
    }
  }
}
