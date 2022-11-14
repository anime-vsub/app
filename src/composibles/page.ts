import { useRoute } from "vue-router"
import { computed } from "vue"
import { useSettingsStore } from "stores/settings"

export function usePage() {
  const route = useRoute()
  const settingsStore = useSettingsStore()

  return computed(() => {
    if (settingsStore.infinityScroll) return 1;
    
    const { page}  = route.query

    return (page && isFinite(page) ? parseInt(page) : undefined) ?? 1
  })
}
