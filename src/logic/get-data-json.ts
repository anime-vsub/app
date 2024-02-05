export function getDataJson<T>(id: string): T | null {
  const data = document.getElementById(id) as HTMLScriptElement | null
  if (data?.type === "data/json" && data.textContent) {
    return JSON.parse(data.textContent)
  }

  return null
}
