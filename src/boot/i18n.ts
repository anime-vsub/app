import dayjs from "dayjs"
import { set } from "idb-keyval"
import { boot } from "quasar/wrappers"
import { loadLocalize } from "src/i18n"
import enUS from "src/i18n/messages/en-US.json"
import { useSettingsStore } from "stores/settings"
import type { Ref } from "vue"
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

let langChangeCast: BroadcastChannel | undefined

export const i18n = createI18n({
  fallbackLocale: "en-US",
  legacy: false,
  messages: {
    "en-US": enUS
  }
})
export default boot(({ app }) => {
  const settingsStore = useSettingsStore()

  watch(
    () => settingsStore.locale,
    async (locale, _, onCleanup) => {
      const messages = await loadLocalize(locale)

      i18n.global.setLocaleMessage(locale, messages)
      ;(i18n.global.locale as Ref<string>).value = locale
      document.querySelector("html")?.setAttribute("lang", locale)
      dayjs.locale(locale)

      void set("lang", locale).then(() => {
        // eslint-disable-next-line promise/always-return
        langChangeCast ??= new BroadcastChannel("lang-change")
        langChangeCast.postMessage(locale)
      })

      onCleanup(() => {
        langChangeCast?.close()
        langChangeCast = undefined
      })
    },
    { immediate: true }
  )
  // Set i18n instance on app
  app.use(i18n)
})
