<template>
  <q-pagination v-if="!settingsStore.infinityScroll && max" :model-value="(isFinite(route.query.page ) ? parseInt(route.query.page) : undefined )?? 1"
    flat active-color="primary" rounded :max="max" :max-pages="5" :to-fn="page => ({ query: { page } })"
    direction-links />
</template>

<script lang="ts" setup>
  import { useRoute } from "vue-router"
  import { useSettingsStore } from "stores/settings"


  const settingsStore = useSettingsStore()
  const route = useRoute()

  defineProps<{
    max?: number
  }>()
</script>
