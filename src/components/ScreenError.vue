<template>
  <div class="h-full w-full flex items-center text-center">
    <component
      :is="componentErrors[typeError ?? 'unknown'] ?? componentErrors.unknown"
      :no-image="noImage"
      :retry="() => emit('click:retry')"
    />
  </div>
</template>

<script lang="ts" setup>
import type { HttpResponse } from "@capacitor/core"
import { computed } from "vue"

import ErrorCloudflare from "./errors/cloudflare.vue"
import ErrorUnknown from "./errors/unknown.vue"

const props = defineProps<{
  noImage?: boolean
  error: Error | HttpResponse | undefined
}>()
const emit = defineEmits<{
  (name: "click:retry"): void
}>()

const componentErrors = {
  cloudflare: ErrorCloudflare,
  unknown: ErrorUnknown,
}

const typeError = computed(() => {
  if (!props.error) return null

  if (
    !(props.error instanceof Error) &&
    props.error.data?.includes("<title>Just a moment...</title>") &&
    props.error.data?.includes("window._cf_chl_opt=") &&
    props.error.status === 403
  )
    return "cloudflare"

  return null
})
</script>
