export function getDataJson<T>(id: string, $for: string): T | null {
  const data = document.getElementById(id) as HTMLScriptElement | null
  if (
    data?.type === "data/json" &&
    data.getAttribute("for") === $for &&
    data.textContent
  ) {
    return JSON.parse(data.textContent)
  }

  return null
}
