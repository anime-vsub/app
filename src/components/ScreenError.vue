<template>
  <div class="h-full w-full flex items-center">
    <div class="text-center w-full">
      <img
        v-if="!noImage"
        src="~assets/ic_22_cry.png"
        width="240"
        class="mx-auto"
      />
      <div class="my-1">{{ t("da-xay-ra-loi") }}</div>
      <q-btn
        no-caps
        outline
        rounded
        @click="emit('click:retry')"
        style="color: #00be06"
        >{{ t("thu-lai") }}</q-btn
      >
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useI18n } from "vue-i18n"

defineProps<{
  noImage?: boolean
}>()
const emit = defineEmits<{
  (name: "click:retry"): void
}>()

const { t } = useI18n()
</script>
