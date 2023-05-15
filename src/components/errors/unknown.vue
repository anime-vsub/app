<template>
  <div>
    <img
      v-if="!noImage"
      src="~assets/ic_22_cry.png"
      width="240"
      class="mx-auto"
    />
    <div class="text-h4 mt-4">Error</div>

    <div class="text-h6 text-weight-regular mt-4" style="opacity: 0.4">
      {{ t("da-xay-ra-loi") }}
    </div>

    <q-btn
      class="q-mt-md"
      no-caps
      outline
      rounded
      @click="retry"
      style="color: #00be06"
      >{{ t("thu-lai") }}</q-btn
    >
  </div>
</template>

<script lang="ts" setup>
import { useI18n } from "vue-i18n"
defineProps<{
  noImage?: boolean
  retry: () => void
}>()

const { t } = useI18n()
</script>

<style lang="scss" scoped></style>
