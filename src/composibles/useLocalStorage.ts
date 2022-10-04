import { ref, watch } from "vue"

export function useLocalStorage<T>(key: string, def?: T) {
  const value = ref<T>(get(key) ?? def)

  watch(
    value,
    (value) => {
      set(key, value)
    },
    { deep: true }
  )

  return value
}

function get(key: string) {
  try {
    return JSON.parse(localStorage.getItem(key))
  } catch {
    return undefined
  }
}
function set(key: string, value: unknown) {
  localStorage.setItem(key, JSON.stringify(value))
}
