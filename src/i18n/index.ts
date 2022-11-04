import ISO6391 from "iso-639-1"

export function loadLocalize(locale: string) {
  return import(`./messages/${locale}.json`).then((res) => res.default)
}

export const languages = Object.keys(import.meta.glob("./messages/*.json")).map(
  (code) => {
    return {
      code,
      name: ISO6391.getName(code),
    }
  }
)
