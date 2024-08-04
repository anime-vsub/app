window.Request = class extends Request {
  readonly headers: Headers
  constructor(url: RequestInfo | URL, initParams?: RequestInit) {
    super(url, initParams)
    this.headers = new Headers(initParams?.headers)
  }
}
