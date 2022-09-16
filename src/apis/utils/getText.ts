export function getText(el: Element, def = ""): string {
  return el.textContent?.trim() ?? def
}
