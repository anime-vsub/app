
import {useAuthStore} from "stores/auth"
import { post }
 from "src/logic/http"

export async function AjaxLike(id: string) {
    const { token_name, token_value } = useAuthStore()

    if (!token_name || !token_value) throw new Error("TOKEN_REQUIRED_FOR_NOTIFICATION")

    const { data } = await post("/ajax/like", {
      filmLike: "1",
      filmID: id
    }, {
      cookie:`${token_name}=${token_value}`
    })

return parseInt(data)
}

export async function checkIsLile(id: string) {
    const { token_name, token_value } = useAuthStore()

    if (!token_name || !token_value) throw new Error("TOKEN_REQUIRED_FOR_NOTIFICATION")

  const {data } = await get(`/ajax/notification?Bookmark=true&filmId=${id}`, {
      cookie:`${token_name}=${token_value}`
  })
  
  return JSON.parse(data).status === 1
}