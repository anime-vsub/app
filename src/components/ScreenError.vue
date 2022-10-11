<template>
  <div class="h-full w-full flex items-center">
    <div class="text-center w-full">
      <img v-if="!noImage" src="~assets/ic_22_cry.png" width="240" class="mx-auto" />
      <div class="my-1">Đã xảy ra lỗi</div>
      <q-btn
        dense
        no-caps
        outline
        class="px-2"
        @click="emit('click:retry')"
        style="color: #00be06"
        >Thử lại</q-btn
      >
    </div>
  </div>
</template>

<script lang="ts" setup>
  defineProps<{
    noImage?: boolean
  }>()
const emit = defineEmits<{
  (name: "click:retry"): void
}>()
</script>
