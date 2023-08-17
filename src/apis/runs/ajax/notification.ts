/* eslint-disable camelcase */
import type AjaxNotificationParser from "src/apis/parser/ajax/notification"
import Worker from "src/apis/workers/ajax/notification?worker"
import { PostWorker } from "src/apis/wrap-worker"
import { i18n } from "src/boot/i18n"
import { get } from "src/logic/http"
import { useAuthStore } from "stores/auth"

export async function AjaxNotification() {
  const { token_name, token_value } = useAuthStore()

  if (!token_name || !token_value)
    throw new Error(
      i18n.global.t("errors.require_login_to", [i18n.global.t("xem-thong-bao")])
    )

  const { data: json } = await get("/ajax/notification?notif=true", {
    cookie: `${token_name}=${token_value}`,
  })

  const data = JSON.parse(json)

  if (data.status === 0)
    // {status: 0, html: 'Not login', total: 0}

    throw new Error(
      data.html.toLowerCase() === "not login"
        ? "NOT_LOGIN"
        : i18n.global.t("errors.loi-khong-xac-dinh-khi", [
            i18n.global.t("cap-nhat-thong-bao"),
          ])
    )

  return {
    items: await PostWorker<typeof AjaxNotificationParser>(Worker, data.html),
    max: parseInt(data.total),
  }
}
