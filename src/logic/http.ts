import type { GetOption } from "client-ext-animevsub-helper"
import { Http } from "client-ext-animevsub-helper"
import { i18n } from "src/boot/i18n"
import { C_URL } from "src/constants"

const noExt = () =>
  Promise.reject(
    Object.assign(
      new Error(
        i18n.global.t(
          "trang-web-can-extension-animevsub-helper-de-hoat-dong-binh-thuong"
        )
      ),
      { extesionNotExists: true }
    )
  )

async function httpGet(
  url: string | GetOption,
  headers?: Record<string, string>
) {
  console.log("get: ", url)

  const response = await Http.get(
    typeof url === "object"
      ? url
      : {
          url: url.includes("://") ? url : C_URL + url + "#animevsub-vsub",
          headers: {
            accept:
              "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
            // "accept-encoding": "deflate, br",F
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
            ...headers,
          },
        }
  ).then((response) => {
    if (response.status === 403 || response.status === 520) {
      console.log("response fail")
    }

    return response
  })

  console.log("get-result: ", response)
  // eslint-disable-next-line functional/no-throw-statement
  if (response.status !== 200 && response.status !== 201) throw response

  return response as Omit<typeof response, "data"> & { data: string }
}

async function httpPost(
  url: string,
  data: Record<string, number | string | boolean>,
  headers?: Record<string, string>
) {
  console.log("post: ", {
    url: C_URL + url,
    data,
    headers,
  })

  const response = await Http.post({
    url: C_URL + url + "#animevsub-vsub",
    headers,
    data,
  })

  console.log("post-result: ", response)
  // eslint-disable-next-line functional/no-throw-statement
  if (response.status !== 200 && response.status !== 201) throw response

  return response as Omit<typeof response, "data"> & { data: string }
}

export const get = Http.version ? httpGet : noExt
export const post = Http.version ? httpPost : noExt
