import { i18n } from "boot/i18n"
import { defineStore } from "pinia"
import { useQuasar } from "quasar"
import { AjaxNotification } from "src/apis/runs/ajax/notification"
import { post } from "src/logic/http"
import { ref, shallowRef, watch } from "vue"

import { useAuthStore } from "./auth"

export const useNotificationStore = defineStore(
  "notification",
  () => {
    const authStore = useAuthStore()

    const items = shallowRef<
      Awaited<ReturnType<typeof AjaxNotification>>["items"]
    >([])
    const max = ref(0)

    const $q = useQuasar()

    const loading = ref(false)

    let timeout: NodeJS.Timeout | number

    let countFail = 0
    async function updateNotification() {
      if (timeout) clearTimeout(timeout)

      try {
        loading.value = true
        const result = await AjaxNotification()

        items.value = result.items
        max.value = result.max
      } catch (err) {
        if ((err as Error)?.message === "NOT_LOGIN") {
          // cookie not sync
          $q.dialog({
            title: i18n.global.t("yeu-cau-dang-nhap-lai"),
            message: i18n.global.t(
              "cookie-hien-khong-dong-bo-ban-can-dang-nhap-lai"
            ),
            ok: {
              flat: true,
              rounded: true
            },
            persistent: true // TODO
          }).onOk(() => {
            authStore.logout()
            // updateNotification()
          })

          return
        }
        console.error(err)

        // allow failure 3 pinia
        if (countFail > 3)
          $q.notify({
            position: "bottom-right",
            message: i18n.global.t("nhan-thong-bao-that-bai"),
            caption: (err as Error).message
          })
        else countFail++

        timeout = setTimeout(updateNotification, 10 * 60_000)
      } finally {
        loading.value = false
      }
    }

    watch(
      () => authStore.isLogged,
      (isLogged) => {
        if (isLogged) updateNotification()
        else {
          clearTimeout(timeout)
          items.value = []
          max.value = 0
        }
      },
      { immediate: true }
    )

    async function remove(id: string) {
      const { data } = await post("/ajax/notification", {
        Delete: "true",
        id
      })

      if (JSON.parse(data).status !== 1)
        throw new Error(i18n.global.t("errors.xoa-thong-bao-that-bai"))
      // eslint-disable-next-line @typescript-eslint/no-empty-function
      refresh(() => {})
    }

    async function refresh(done: () => void) {
      try {
        const result = await AjaxNotification()

        items.value = result.items
        max.value = result.max
      } catch {}

      done()
    }

    return { items, max, remove, loading, refresh }
  },
  {
    persist: true
  }
)
