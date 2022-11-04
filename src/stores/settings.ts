import { defineStore } from "pinia"

export const useSettingsStore = defineStore("settings", {
  state: () => ({
    player: {
      autoNext: true,
      enableRemindStop: true,
      volume: 1,
    },
    locale: "en-US",
  }),
  persist: true,
})
