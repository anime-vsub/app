import removeAccents from "vn-remove-accents"

const rSpace = / /g
export function parseChapName(chapName: string) {
  return `tap-${removeAccents(chapName)}`.toLowerCase().replace(rSpace, "-")
}
