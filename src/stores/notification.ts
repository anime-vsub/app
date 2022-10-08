import { defineStore } from 'pinia';
import { shallowRef, ref , watch } from "vue"
import { AjaxNotification } from "src/apis/runs/ajax/notification"
import { useQuasar } from "quasar"
import { post } from "src/logic/http"
import { useAuthStore } from "./auth"

export const useNotificationStore = defineStore('notification', () => {
  const authStore = useAuthStore()

  const items = shallowRef([])
  const max = ref(0)

  const $q = useQuasar()

let timeout: NodeJS.Timeout | number
async function updateNotification() {
  if (timeout) clearTimeout(timeout)

  try {
    const result = await AjaxNotification()

    items.value = result.items
    max.value  = result.max
  } catch (err) {
    console.error(err)

    $q.notify({
      position: "bottom-right",
      message: "Nhận thông báo thất bại",
      caption: err.message
    })

    timeout = setTimeout(updateNotification, 10 * 60_000)
  }
}

watch(() => authStore.isLogged, (isLogged) => {
  if (isLogged) updateNotification()
  else  {
    clearTimeout(timeout)
    items.value = []
    max.value = 0
    }
}, { immediate: true })

async function remove(id: string) {
const { data } = await post("/ajax/notification", {
Delete:
"true",
id
})


if (JSON.parse(data).status !== 1) throw new Error("DELETE_FAILED")
updateNotification()
}

  return { items, max , remove }
});

