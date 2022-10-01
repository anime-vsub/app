import { get } from "../logic/http"

export async function getHTML(url: string): Promise<string> {
  return (await get(url)).data
}
