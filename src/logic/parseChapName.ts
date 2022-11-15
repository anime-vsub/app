import removeAccents from "remove-accents"

export function parseChapName(chapName: string) {
  return `tap-${removeAccents(chapName)}`
}
