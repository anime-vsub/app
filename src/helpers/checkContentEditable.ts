const tags = ["INPUT", "TEXTAREA"]

export function checkContentEditable(el: Element | null): boolean {
  if (!el) return false

  return tags.includes(el.tagName)
}
