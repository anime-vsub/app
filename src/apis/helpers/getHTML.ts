import { get } from "../../logic/http"

export async function getHTML(url: string): Promise<string> {
  if (import.meta.env.TEST) {
    return url
  }

  return (await get(url)).data
}
