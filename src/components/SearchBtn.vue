<template>
  <q-btn flat round dense icon="search" class="q-mr-xs" @click="open = true" />

  <q-header class="bg-dark" v-if="open">
    <q-toolbar>
      <q-input
        dense
        flat
        class="w-full"
        placeholder="Tìm với AnimeVsub"
        borderless
        autofocus
        v-model="query"
      >
        <template v-slot:prepend>
          <q-btn dense flat icon="arrow_back" @click="open = false" />
        </template>
        <template v-slot:append v-if="query">
          <q-btn
            dense
            flat
            icon="close"
            @click.prevent="query = ''"
            @mousedown.prevent
          />
        </template>
      </q-input>
    </q-toolbar>
  </q-header>

  <q-page
    v-if="open"
    class="fixed bg-dark left-0 w-full overflow-y-scroll"
    @mousedown.prevent
    :style-fn="
      (offset, height) => ({
        top: offset + 'px',
        height: height - offset + 'px',
      })
    "
    tabindex="0"
  >
  </q-page>
</template>

<script lang="ts" setup>
import { ref, watchEffect } from "vue"

const open = ref(true)
const query = ref("")

watchEffect(() => {
  document.body.style.overflow = open.value ? "hidden" : ""
})
</script>
