<template>
  <div
    class="h-full w-full flex items-center text-white text-center q-pa-md flex flex-center q-pa-md"
  >
    <component
      :is="componentErrors[typeError ?? 'unknown'] ?? componentErrors.unknown"
      :no-image="noImage"
      :retry="() => emit('click:retry')"
    />
  </div>
</template>

<script lang="ts" setup>
import { computed } from "vue"

import ErrorCloudflare from "./errors/cloudflare.vue"
import ErrorUnknown from "./errors/unknown.vue"

const props = defineProps<{
  noImage?: boolean
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  error?: any
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

  if (props.error.data?.includes("<title>Just a moment...</title>"))
    return "cloudflare"

  return null
})
</script>
