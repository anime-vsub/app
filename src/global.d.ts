interface GetOptions<ResponseType extends "arraybuffer" | undefined> {
  url: string
  headers?: Record<string, string>
  responseType?: ResponseType
}
interface PostOptions {
  url: string
  headers?: Record<string, string>
  responseType?: "arraybuffer"
  data?: Record<string, unknown>
}

interface HttpResponse<ResponseType extends "arraybuffer" | undefined> {
  headers: Record<string, string>
  data: ResponseType extends "arraybuffer" ? ArrayBuffer : string
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
