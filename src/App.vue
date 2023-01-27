<template>
  <MainLayout />
</template>

<script setup lang="ts">
import MainLayout from "layouts/MainLayout.vue"

if (process.env.MODE !== "spa") {
  import("eruda2")
    .then((eruda2) => eruda2.default.init())
    .catch(() => console.warn("load eruda2 failure"))
}
</script>
