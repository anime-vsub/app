<template>
  <div class="relative row mt-1">
    <div
      v-for="item in count"
      :key="item"
      class="col-4 col-lg-3 col-xl-2 px-[5px] py-2"
    >
      <SkeletonCard />
    </div>
  </div>
</template>

<script lang="ts" setup>
import SkeletonCard from "./SkeletonCard.vue"

defineProps<{
  count: number
}>()
</script>
