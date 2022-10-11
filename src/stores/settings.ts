import { defineStore } from "pinia"

export const useSettingsStore = defineStore("settings", {
  state: () => ({
    player: {
      autoNext: true,
      enableRemindStop: false,
    },
  }),
})
