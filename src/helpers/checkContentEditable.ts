
const tags = ["INPUT", "TEXTAREA"]

export function checkContentEditable (el?: HTMLElement | null): boolean {
  if (!el) return false

  return tags.includes(el.tagName)
}
