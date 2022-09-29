<template>
  <div class="relative row mt-1">
    <div
      v-for="item in items"
      :key="item.name"
      class="col-4 col-lg-3 col-xl-2 px-[5px] py-2"
    >
      <Card :data="item" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import Card from "./Card.vue"
import type { TPost } from "src/apis/helpers/getInfoTPost"

defineProps<{
  items: TPost[]
}>()
</script>
