<template>
  <MainLayout v-if="route.name !== 'task-manager'" />
  <router-view v-else />
</template>

<script setup lang="ts">
import MainLayout from "layouts/MainLayout.vue"
import { useRoute } from "vue-router"

const route = useRoute()
</script>
