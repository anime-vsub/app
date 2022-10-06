<template>
  <router-view />

  <q-btn v-if="isDev" class="fixed right-0 bottom-0 z-9999" @click="reload">reload</q-btn>
</template>

<script setup lang="ts">
import { Http } from "@capacitor-community/http"
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-expect-error
import eruda from "eruda2"

const isDev = import.meta.NODE_ENV === "development"
if (isDev) {
    eruda.init()

    Object.assign(window, { Http })

    function reload() {
      location.reload()
    }
}
</script>
