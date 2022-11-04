import ISO6391 from "iso-639-1"

export function loadLocalize(locale: string) {
  return import(`./messages/${locale}.json`).then((res) => res.default)
}

const reg = /[\w-]+(?=\.json$)/
const langs = Object.keys(import.meta.glob("./messages/*.json")).map(
  (path) => reg.exec(path)?.[0]
) as string[]

export const languages = langs.map((code) => {
  return {
    code,
    name: ISO6391.getNativeName(code?.slice(0, 2) ?? "en"),
  }
})

export function getNavigatorLanguage() {
  const lang = self.navigator?.language

  if (lang && langs.includes(lang)) return lang

  return "en-US"
}
