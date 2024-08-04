window.Request = class extends Request {
  readonly headers: Headers
  constructor(url, initParams) {
    super(url, initParams)
    this.headers = new Headers(initParams?.headers)
  }
}
