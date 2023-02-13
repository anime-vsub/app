import { installedAsync } from "boot/installed-extension"
import { i18n } from "src/boot/i18n"
import { C_URL } from "src/constants"

export async function get<
  ResponseType extends "arraybuffer" | undefined = undefined
>(url: string | GetOptions<ResponseType>, headers?: Record<string, string>) {
  console.log("get: ", url)

  await installedAsync.value

  if (!window.Http)
    // eslint-disable-next-line functional/no-throw-statement
    throw Object.assign(
      new Error(
        i18n.global.t(
          "trang-web-can-extension-animevsub-helper-de-hoat-dong-binh-thuong"
        )
      ),
      { extesionNotExists: true }
    )

  const response = (await window.Http.get(
    typeof url === "object"
      ? url
      : {
          url: url.includes("://") ? url : C_URL + url,
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
            ...headers,
          },
        }
  ).then((response) => {
    if (response.status === 403 || response.status === 520) {
      console.log("response fail")
    }

    return response
  })) as HttpResponse<ResponseType>

  console.log("get-result: ", response)
  // eslint-disable-next-line functional/no-throw-statement
  if (response.status !== 200 && response.status !== 201) throw response

  return response
}

export async function post(
  url: string,
  data: Record<string, number | string | boolean>,
  headers?: Record<string, string>
) {
  console.log("post: ", {
    url: C_URL + url,
    data,
    headers: {
      "user-agent":
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36",
      // "content-type": "application/x-www-form-urlencoded; charset=UTF-8",
      ...headers,
    },
  })

  await installedAsync.value

  if (!window.Http)
    // eslint-disable-next-line functional/no-throw-statement
    throw Object.assign(
      new Error(
        i18n.global.t(
          "trang-web-can-extension-animevsub-helper-de-hoat-dong-binh-thuong"
        )
      ),
      { extesionNotExists: true }
    )

  const response = (await window.Http.post({
    url: C_URL + url,
    headers: {
      "user-agent":
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36",
      // "content-type": "application/x-www-form-urlencoded; charset=UTF-8",
      ...headers,
    },
    data,
  })) as HttpResponse<undefined>

  console.log("post-result: ", response)
  // eslint-disable-next-line functional/no-throw-statement
  if (response.status !== 200 && response.status !== 201) throw response

  return response
}
