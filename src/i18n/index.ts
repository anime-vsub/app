import langs from "virtual:i18n-langs"

export function loadLocalize(locale: string) {
  return import(`./messages/${locale}.json`).then((res) => res.default)
}

export function getNavigatorLanguage() {
  const lang = self.navigator?.language

  return langs.find((item) => item.code.startsWith(lang))?.code ?? "en-US"
}
