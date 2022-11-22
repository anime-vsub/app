/* eslint-disable camelcase */
import { i18n } from "src/boot/i18n"
import { get } from "src/logic/http"
import { useAuthStore } from "stores/auth"

export async function AjaxRate(id: string, score: number) {
  const { token_name, token_value } = useAuthStore()

  if (!token_name || !token_value)
    // eslint-disable-next-line functional/no-throw-statement
    throw new Error(
      i18n.global.t("errors.require_login_to", ["đánh giá Anime này"])
    )

  const { data } = await get(
    `/ajax/rate?_fxAjax=1&_fxResponseType=json&score=${score}&film=${id}`,
    {
      cookie: `${token_name}=${token_value}`,
    }
  )

  const { rateCount, ratePoint, _fxStatus } = JSON.parse(data)

  switch (_fxStatus) {
    case 1:
      return {
        success: true,
        count_rate: parseInt(rateCount) + 1,
        rate: ratePoint,
      }
    case 2:
      return {
        success: false,
        count_rate: parseInt(rateCount),
        rate: ratePoint,
      }
    default:
      // eslint-disable-next-line functional/no-throw-statement
      throw new Error("response not control")
  }
}
