import type { HttpOptions, HttpResponse } from "@capacitor-community/http"
import { CapacitorHttp as Http } from '@capacitor/core';


export async function get(url: string | HttpOptions,
  headers?: Record<string, string>) {

  const response = await Http.get(
    typeof url === "object"
      ? url
      : {
          url: url.includes("://") ? url : "https://animevietsub.cc" + url,
          headers: {
            accept:
              "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
            // "accept-encoding": "deflate, br",
            "accept-language": "vi-VN,vi;q=0.9,en;q=0.8,ja;q=0.7",
            "cache-control": "max-age=0",
            dnt: "1",
            "sec-ch-ua":
              '"Google Chrome";v="105", "Not)A;Brand";v="8", "Chromium";v="105"',
            "sec-ch-ua-mobile": "?0",
            "sec-ch-ua-platform": "Windows",
            "sec-fetch-dest": "document",
            "sec-fetch-mode": "navigate",
            "sec-fetch-site": "none",
            "sec-fetch-user": "?1",
            "sec-gpc": "1",
            "upgrade-insecure-requests": "1",
            "user-agent":
              "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36",
              ...headers
          },
        }
  ).then((response) => {
    if (response.status === 403 || response.status === 520) {
      console.log("response fail")
    }

    return response
  })
 
  // eslint-disable-next-line functional/no-throw-statement
  if (response.status !== 200 && response.status !== 201) throw response

  return response
}

export async function post(
  url: string,
  data: Record<string, number | string | boolean>,
  headers?: Record<string, string>
) {
  const response = await Http.post({
    url: "https://animevietsub.cc" + url,
    headers: {
      "user-agent":
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36",
      "content-type": "application/x-www-form-urlencoded; charset=UTF-8",
      ...headers
    },
    data,
  })

  // eslint-disable-next-line functional/no-throw-statement
  if (response.status !== 200 && response.status !== 201) throw response

  return response
}
