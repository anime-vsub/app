import Hls from "hls.js"

import { XMLHttpRequestJava } from "./XMLHttpRequestJava"

const { DefaultConfig } = Hls

const XhrLoader = DefaultConfig.loader

DefaultConfig.loader = class Loader extends XhrLoader {
  loadInternal() {
    const context = this.context
    const xhr = (this.loader = new XMLHttpRequestJava())

    const stats = this.stats
    stats.tfirst = 0
    stats.loaded = 0
    const xhrSetup = this.xhrSetup

    try {
      if (xhrSetup) {
        try {
          xhrSetup(xhr, context.url)
        } catch (e) {
          // fix xhrSetup: (xhr, url) => {xhr.setRequestHeader("Content-Language", "test");}
          // not working, as xhr.setRequestHeader expects xhr.readyState === OPEN
          xhr.open("get", context.url)
          xhrSetup(xhr, context.url)
        }
      }
      if (!xhr.readyState) {
        xhr.open("get", context.url)
      }
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
    } catch (e: any) {
      // IE11 throws an exception on xhr.open if attempting to access an HTTP resource over HTTPS
      this.callbacks.onError(
        { code: xhr.status, text: e.message },
        context,
        xhr
      )
      return
    }

    if (context.rangeEnd) {
      xhr.setRequestHeader(
        "Range",
        "bytes=" + context.rangeStart + "-" + (context.rangeEnd - 1)
      )
    }

    xhr.onreadystatechange = this.readystatechange.bind(this)
    xhr.onprogress = this.loadprogress.bind(this)
    xhr.responseType = context.responseType

    // setup timeout before we perform request
    this.requestTimeout = window.setTimeout(
      this.loadtimeout.bind(this),
      this.config.timeout
    )
    xhr.send()
  }
}

export { Hls }
