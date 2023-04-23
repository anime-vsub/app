import { defineStore } from "pinia"
import { ref } from "vue"

export const useStateStorageStore = defineStore("state-storage", () => {
  const disableAutoRestoration = ref<0 | 1 | 2>(0)

  return { disableAutoRestoration }
})
