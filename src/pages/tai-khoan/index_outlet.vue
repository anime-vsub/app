<template>
  <router-view v-slot="{ Component }">
    <keep-alive exclude="_season">
      <component :is="Component" />
    </keep-alive>
  </router-view>
</template>

<script lang="ts" setup>
import { useAliveScrollBehavior } from "src/composibles/useAliveScrollBehavior"
// Import Swiper Vue.js components
useAliveScrollBehavior()
</script>
