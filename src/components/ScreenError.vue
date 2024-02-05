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
import NotExistsExtension from "src/layouts/NotExistsExtension.vue"
import { computed } from "vue"

import ErrorCloudflare from "./errors/cloudflare.vue"
import ErrorDomainStrange from "./errors/domain-strange.vue"
import RequireUpdateExt from "./errors/require-update-ext.vue"
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
  "domain-strange": ErrorDomainStrange,
  unknown: ErrorUnknown,
  "extension-not-exists": NotExistsExtension,
  "require-update-ext": RequireUpdateExt
}

const typeError = computed(() => {
  if (!props.error) return null

  if (
    !(props.error instanceof Error) &&
    props.error.data?.includes("window._cf_chl_opt=") &&
    props.error.status === 403
  )
    return "cloudflare"
  if (
    props.error.message?.includes(
      "Your domain is not permission to access the Http API"
    )
  )
    return "domain-strange"
  if (props.error?.extesionNotExists) return "extension-not-exists"
  if (props.error?.requireUpdate) return "require-update-ext"

  return null
})
</script>
