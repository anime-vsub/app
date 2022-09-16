export function getAttrs<T extends string>(
  el: HTMLElement,
  attrsName: T[]
): Record<T, string | null> {
  return attrsName.reduce((attrs, name) => {
    attrs[name] = el.getAttribute(name)
    return attrs
  }, {} as Record<T, string | null>)
}
