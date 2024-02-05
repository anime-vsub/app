<template>
  <q-pagination
    v-if="!settingsStore.infinityScroll && max"
    :model-value="
      (isFinite(route.query.page as unknown as number)
        ? parseInt(route.query.page as string)
        : undefined) ?? 1
    "
    flat
    active-color="primary"
    rounded
    :max="max"
    :max-pages="5"
    :to-fn="(page) => ({ query: { page } })"
    direction-links
  />
</template>

<script lang="ts" setup>
import { useSettingsStore } from "stores/settings"
import { useRoute } from "vue-router"

const settingsStore = useSettingsStore()
const route = useRoute()

defineProps<{
  max?: number
}>()
</script>
