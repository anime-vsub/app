<template>
  <router-view />

  <q-btn class="fixed right-0 bottom-0 z-122" @click="reload">reload</q-btn>
</template>

<script setup lang="ts">
function reload() {
  location.reload()
}
</script>
