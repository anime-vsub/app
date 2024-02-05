/* eslint-disable camelcase */
import { i18n } from "src/boot/i18n"
import { get } from "src/logic/http"
import { useAuthStore } from "stores/auth"

export async function AjaxLike(id: string, value: boolean) {
  const { token_name, token_value } = useAuthStore()

  if (!token_name || !token_value)
    throw new Error(
      i18n.global.t("errors.require_login_to", [
        i18n.global.t("theo-doi-anime-nay")
      ])
    )

  const { data } = await get(
    `/ajax/notification?Bookmark=true&filmId=${id}&type=${
      value ? "add" : "remove"
    }`,
    {
      cookie: `${token_name}=${token_value}`
    }
  )

  return parseInt(data)
}

export async function checkIsLike(id: string) {
  const { token_name, token_value } = useAuthStore()

  if (!token_name || !token_value)
    throw new Error("REQUIRE_LOGiN_TO_FETCH_LIKE")

  const { data } = await get(`/ajax/notification?Bookmark=true&filmId=${id}`, {
    cookie: `${token_name}=${token_value}`
  })

  return JSON.parse(data).status === 1
}
