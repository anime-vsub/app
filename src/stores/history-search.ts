import { defineStore } from "pinia"

export const useHistorySearchStore = defineStore("history-search", {
  state: () => ({
    items: <string[]>[]
  }),
  persist: true
})
