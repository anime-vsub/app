import { defineStore } from "pinia"
import type { servers } from "src/constants"
import { getNavigatorLanguage } from "src/i18n"

export const useSettingsStore = defineStore("settings", {
  state: () => ({
    player: {
      autoNext: true,
      enableRemindStop: true,
      volume: 1,
      server: <keyof typeof servers>"DU",
    },
    ui: {
      modeMovie: false,
      newPlayer: true,
      shortcutsQAP: true,
      menuTransparency: true,
      commentAnime: true,
    },
    locale: getNavigatorLanguage(),
    infinityScroll: true,
    restoreLastEp: true
  }),
  persist: true,
})
