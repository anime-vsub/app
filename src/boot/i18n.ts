import { boot } from "quasar/wrappers"
import { loadLocalize } from "src/i18n"
import { useSettingsStore } from "stores/settings"
import { watch } from "vue"
import { createI18n } from "vue-i18n"

/* eslint-disable @typescript-eslint/no-empty-interface */
declare module "vue-i18n" {
  // define the datetime format schema
  export interface DefineDateTimeFormat {}

  // define the number format schema
  export interface DefineNumberFormat {}
}
/* eslint-enable @typescript-eslint/no-empty-interface */

export default boot(({ app }) => {
  const i18n = createI18n({
    locale: "en-US",
    legacy: false,
  })
  const settingsStore = useSettingsStore()

  watch(
    () => settingsStore.locale,
    async (locale) => {
      const messages = await loadLocalize(locale)

      i18n.global.setLocaleMessage(locale, messages)
    },
    { immediate: true }
  )
  // Set i18n instance on app
  app.use(i18n)
})
