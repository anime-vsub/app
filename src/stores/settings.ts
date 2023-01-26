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
      shortcutsQAP: false,
      menuTransparency: true,
      commentAnime: true,
    },
    locale: getNavigatorLanguage(),
    infinityScroll: true,
  }),
  persist: true,
})
