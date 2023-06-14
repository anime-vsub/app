interface GetOptions{
  url: string
  headers?: Record<string, string>
  responseType?: string
}
interface PostOptions {
  url: string
  headers?: Record<string, string>
  responseType?: "arraybuffer"
  data?: Record<string, unknown> | string
}

interface HttpResponse {
  headers: Record<string, string>
  data: any
  url: string
  status: number
}

interface Http {
  version?: string
  get: (options: GetOptions) => Promise<HttpResponse>
  post: (options: PostOptions) => Promise<HttpResponse>
}

interface Window {
  Http: Http
}
