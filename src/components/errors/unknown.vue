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
      Đã xảy ra lỗi
    </div>

    <q-btn
      class="q-mt-md"
      dense
      no-caps
      outline
      rounded
      @click="retry"
      style="color: #00be06"
      >Thử lại</q-btn
    >
  </div>
</template>

<script lang="ts" setup>
defineProps<{
  noImage?: boolean
  retry: () => void
}>()
</script>

<style lang="scss" scoped></style>
