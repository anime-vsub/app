import { Http } from "@capacitor-community/http"

export async function getHTML(url: string): Promise<string> {
  const response = await Http.get({
    url: `https://animevietsub.cc` + url,
  })

  return response.data
}
