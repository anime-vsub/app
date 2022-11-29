import { useSettingsStore } from "stores/settings"
import { computed } from "vue"
import { useRoute } from "vue-router"

export function usePage() {
  const route = useRoute()
  const settingsStore = useSettingsStore()

  return computed(() => {
    if (settingsStore.infinityScroll) return 1

    const { page } = route.query

    return (
      (page && isFinite(page as unknown as number)
        ? parseInt(page as string)
        : undefined) ?? 1
    )
  })
}
