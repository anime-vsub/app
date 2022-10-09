/* eslint-disable camelcase */
import { get } from "src/logic/http"
import { useAuthStore } from "stores/auth"

export async function AjaxLike(id: string, value: boolean) {
  const { token_name, token_value } = useAuthStore()

  if (!token_name || !token_value)
    // eslint-disable-next-line functional/no-throw-statement
    throw new Error("TOKEN_REQUIRED_FOR_NOTIFICATION")

  const { data } = await get(
    `/ajax/notification?Bookmark=true&filmId=${id}&type=${
      value ? "add" : "remove"
    }`,
    {
      cookie: `${token_name}=${token_value}`,
    }
  )

  return parseInt(data)
}

export async function checkIsLile(id: string) {
  const { token_name, token_value } = useAuthStore()

  if (!token_name || !token_value)
    // eslint-disable-next-line functional/no-throw-statement
    throw new Error("TOKEN_REQUIRED_FOR_NOTIFICATION")

  const { data } = await get(`/ajax/notification?Bookmark=true&filmId=${id}`, {
    cookie: `${token_name}=${token_value}`,
  })

  return JSON.parse(data).status === 1
}
