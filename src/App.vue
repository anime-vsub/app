<template>
  <MainLayout />
</template>

<script setup lang="ts">
if (process.env.MODE !== "spa") {
  import('eruda2').then(eruda2 => eruda2.default.init())
}

import MainLayout from "layouts/MainLayout.vue"
</script>
