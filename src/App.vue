<template>
  <router-view />

  <q-btn class="fixed right-0 bottom-0 z-122" @click="reload">reload</q-btn>
</template>

<script setup lang="ts">
import { Http } from "@capacitor-community/http"
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-expect-error
import eruda from "eruda2"

eruda.init()

Object.assign(window, { Http })

function reload() {
  location.reload()
}
</script>
