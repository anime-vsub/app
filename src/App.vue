<template>
  <MainLayout />
</template>

<script setup lang="ts">
import MainLayout from "layouts/MainLayout.vue"
import { isNative } from "src/constants"

if (import.meta.env.DEV && !isNative) {
  import("eruda2")
    .then((eruda2) => eruda2.default.init())
    .catch(() => console.warn("load eruda2 failure"))
}
</script>
