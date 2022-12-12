import { defineStore } from "pinia"
import { getNavigatorLanguage } from "src/i18n"

export const useSettingsStore = defineStore("settings", {
  state: () => ({
    player: {
      autoNext: true,
      enableRemindStop: true,
      volume: 1,
    },
    ui: {
      modeMovie: false,
      newPlayer: true,
      shortcutsQAP: true,
    },
    locale: getNavigatorLanguage(),
    infinityScroll: true,
  }),
  persist: true,
})
