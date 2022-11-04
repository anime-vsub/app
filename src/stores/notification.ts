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

    // eslint-disable-next-line functional/no-let
    let timeout: NodeJS.Timeout | number
    async function updateNotification() {
      if (timeout) clearTimeout(timeout)

      try {
        loading.value = true
        const result = await AjaxNotification()

        items.value = result.items
        max.value = result.max
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
      } catch (err: any) {
        console.error(err)

        $q.notify({
          position: "bottom-right",
          message: "Nhận thông báo thất bại",
          caption: err.message,
        })

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
        id,
      })

      // eslint-disable-next-line functional/no-throw-statement
      if (JSON.parse(data).status !== 1) throw new Error("DELETE_FAILED")
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
    persist: true,
  }
)
