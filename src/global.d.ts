interface GetOptions {
  url: string
  headers?: Record<string, string>
  responseType?: "arraybuffer"
}
interface PostOptions {
  url: string
  headers?: Record<string, string>
  responseType?: "arraybuffer"
  data?: Record<string, unknown>
}

interface HttpResponse {
  headers: Record<string, string>
  data: ArrayBuffer | string
  url: string
  status: number
}

interface Http {
  get: (options: GetOptions) => Promise<HttpResponse>
  post: (options: PostOptions) => Promise<HttpResponse>
}

interface Window {
  Http?: Http
}
