<template>
  <router-view />

  <q-btn class="fixed right-0 bottom-0 z-122" @click="reload">reload</q-btn>
</template>

<script setup lang="ts">
import eruda from "eruda2"
eruda.init()

import { Http } from "@capacitor-community/http"

Object.assign(window, { Http })
function reload() {
  location.reload()
}
</script>
