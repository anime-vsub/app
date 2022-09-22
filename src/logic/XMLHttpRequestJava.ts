import type { HttpOptions } from "@capacitor-community/http"

import { base64ToArrayBuffer } from "./base64ToArrayBuffer"
import { get } from "./http"

export class XMLHttpRequestJava {
  currentTarget = this
  private readonly headers = new Headers()
  private url = ""
  private aborted = false

  public responseType?: HttpOptions["responseType"]
  public readyState: 1 | 2 | 3 | 4 = 1
  public status = 0
  public response?: ArrayBuffer
  public responseText?: string

  public onprogress?: (
    event: typeof this & { loaded: number; total?: number }
  ) => void

  public onreadystatechange?: (event: typeof this) => void

  setRequestHeader(k: string, v: string) {
    this.headers.set(k, v)
  }

  open(method: "get" | "post", url: string) {
    this.url = url
  }

  abort() {
    this.aborted = true
  }

  async send() {
    if (this.aborted) return
    const res = await get({ url: this.url, responseType: this.responseType })

    res.data =
      this.responseType === "arraybuffer"
        ? typeof res.data === "object"
          ? res.data
          : base64ToArrayBuffer(res.data)
        : res.data

    if (this.aborted) return
    this.readyState = 2
    this.status = 200
    this.onprogress?.({ ...this, loaded: 0 })
    this.onreadystatechange?.(this)

    if (this.responseType === "arraybuffer") this.response = res.data
    else this.responseText = res.data

    this.readyState = 4
    this.onprogress?.({
      ...this,
      loaded: res.data.length,
      total: res.data.length,
    })
    this.onreadystatechange?.(this)
  }
}
