import { getPathName } from "../utils/getPathName"

export function getInfoAnchor(a: Element) {
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const path = getPathName(a.getAttribute("href")!)
  const name = a.textContent

  return { path, name }
}
