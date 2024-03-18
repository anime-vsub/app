<template>
  <div class="flex items-center justify-between px-4">
    <h1 class="text-h6">Task mananger App</h1>

    <div class="">
      <q-btn flat round>
        <i-uil-pause class="text-1.2em" @click="pauseAll" />
      </q-btn>
    </div>
  </div>

  <q-list>
    <q-item v-for="[tabId, tab] in memoryTabs" :key="tabId">
      <q-item-section side class="pr-4 min-w-0">
        <img v-if="!tab.playing" class="w-20px" src="~assets/app_icon.svg" />
        <i-solar-clapperboard-play-broken v-else class="w-20px" />
      </q-item-section>

      <q-item-section>
        <q-item-label>{{ tab.title }}</q-item-label>
        <q-item-label caption>{{ tab.url }}</q-item-label>
        <q-item-label caption class="mt-2"
          >Memory used:
          {{ filesize(tab.memory.usedJSHeapSize, { standard: "jedec" }) }} -
          {{
            filesize(tab.memory.totalJSHeapSize, { standard: "jedec" })
          }}</q-item-label
        >
      </q-item-section>

      <q-item-section side class="flex-row">
        <q-btn v-if="tab.canPlay" flat round>
          <i-uil-info-circle class="text-1.2em" />

          <BasicInfoAnime :url="tab.url" />
        </q-btn>

        <q-btn
          v-if="tab.canPlay"
          flat
          round
          @click="togglePlayer(tab, !tab.playing)"
        >
          <i-uil-pause v-if="tab.playing" class="text-1.2em" />
          <i-uil-play v-else class="text-1.2em" />
        </q-btn>
      </q-item-section>
    </q-item>
  </q-list>
</template>

<script lang="ts" setup>
import { useIntervalFn } from "@vueuse/core"
import { useHead } from "@vueuse/head"
import type { MessageReturnMemory } from "boot/task-manager"
import {
  broadcastGetMemory,
  getMemoryAllTabs,
  memoryTabs
} from "boot/task-manager"
import BasicInfoAnime from "components/task-manager/basic-info-anime.vue"
import { filesize } from "filesize"
import { v4 } from "uuid"

useHead({
  title: "Task Manager"
})

useIntervalFn(getMemoryAllTabs, 1_000, { immediateCallback: true })

function togglePlayer(
  { tabId }: Exclude<MessageReturnMemory, "id">,
  play: boolean
) {
  broadcastGetMemory.postMessage({
    id: v4(),
    tabId,
    type: "player",
    play
  })
}

async function pauseAll() {
  broadcastGetMemory.postMessage({
    id: v4(),
    type: "pause all"
  })
}
</script>
