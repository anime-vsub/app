import { defineStore } from "pinia";
import { ref } from "vue";

export const useStateStorageStore =
defineStore('state-storage', () => {
  const disableAutoRestoration = ref(false)

  return { disableAutoRestoration }
})
