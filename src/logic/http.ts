import type { HttpOptions } from "@capacitor-community/http";
import { Http } from "@capacitor-community/http"

export async function get(url: string | HttpOptions) {
  const response = await Http.get(
    typeof url === "object"
      ? url
      : {
          url: "https://animevietsub.cc" + url,
          headers: {
            "user-agent":
              "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36",
          },
        }
  )

  // eslint-disable-next-line functional/no-throw-statement
  if (response.status !== 200 && response.status !== 201) throw response

  return response
}

export async function post(
  url: string,
  data: Record<string, number | string | boolean>
) {
  const response = await Http.post({
    url: "https://animevietsub.cc" + url,
    headers: {
      "user-agent":
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36",
      "content-type": "application/x-www-form-urlencoded; charset=UTF-8",
    },
    data,
  })

  // eslint-disable-next-line functional/no-throw-statement
  if (response.status !== 200 && response.status !== 201) throw response

  return response
}
