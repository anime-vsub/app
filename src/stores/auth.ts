import { defineStore } from 'pinia';
  import { DangNhap } from "src/apis/runs/dang-nhap"
import { parse } from "set-cookie-parser"
import cookie from "js-cookie"

interface User {
          avatar: string
          email:string
          name:string
          sex: string
          username: string
        }

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user_data: parseJSON(cookie.get("user_data")) as (null | {
      value: User
      expires: number
    }),
    token_name: parseJSON(cookie.get("token_name")) as null | string,
    token_value: parseJSON(cookie.get("token_value")) as null | string
  }),
  getters: {
    user(state) {
      if (!state.user_data && state.user_data.expires < Date.now()) return null

      return state.user_data.value
    }
  },
  actions: {
    setUser(value: User, expires: number) {
      this.user_data = { value, expires }
      cookie.set("user_data", JSON.stringify({value, expires}), { expires })
    },
    setToken(name: string, value: string, expires: number) {
      this.token_name = name
      this.token_value = value
      cookie.set("token_name", JSON.stringify(name), { expires })
      cookie.set("token_value", JSON.stringify(value), { expires })
    },
    deleteUser() {
      this.user_data = null
      cookie.delete("user_data")
    },
    deleteToken() {
      this.token_name = null
      this.token_value = null;
      cookie.delete("token_name")
      cookie.delete("token_value")
    },
    // ** actions **
    async login(email: string, password: string) {
      const data = await DangNhap(email, password)
      
      const token = parse(data.cookie).find(item => item.name.startsWith("token"))!
      // set token
      this.setToken(token.name, token.value, token.expires)
      this.setUser({
          avatar: data.avatar,
          email: data.email,
          name: data.name,
          sex: data.sex,
          username: data.username
        },
        token.expires
      )

      return data;
    },
    async logout() {
      this.deleteToken()
    }
  }
});

function parseJSON(value?: string) {
  if (!value) return null

  try {
    return JSON.parse(value) ?? null
  } catch {
    return null
  }
}
