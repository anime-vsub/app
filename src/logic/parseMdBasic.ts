function parseAnchor(md: string) {
  return md.replace(
    /(?:\[(.+)\]){1}?(?:\((.+)\)){1}?/g,
    '<a href="$2" target="_blank" alt="$1">$1</a>'
  )
}
function parseLiterals(md: string) {
  return md.replace(/(?:`(.+)`){1}?/g, "<strong>$1</strong>")
}
function parseUList(md: string) {
  return md.replace(/^-(.+)/gm, "<li>$1</li>")
}

export function parseMdBasic(md: string) {
  return parseUList(parseAnchor(parseLiterals(md)))
}
