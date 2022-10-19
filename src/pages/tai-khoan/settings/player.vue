<template>
  <header class="fixed w-full z-200 top-0 left-0 bg-dark-page">
    <q-toolbar class="relative">
      <q-btn flat dense round class="mr-2" @click.stop="router.back()">
        <Icon icon="fluent:chevron-left-24-regular" width="25" height="25" />
      </q-btn>
      <q-toolbar-title
        class="absolute left-[50%] top-[50%] transform translate-x-[-50%] translate-y-[-50%] text-[16px] max-w-[calc(100%-34px*2)] line-clamp-1"
      >
        Cài đặt phát
      </q-toolbar-title>
    </q-toolbar>
  </header>

  <q-list class="pt-[47px]">
    <q-item clickable v-ripple>
      <q-item-section>
        <q-item-label>Tự động phát</q-item-label>
      </q-item-section>
      <q-item-section side>
        <q-toggle
          v-model="settingsStore.player.autoNext"
          size="sm"
          color="green"
        />
      </q-item-section>
    </q-item>
    <q-item clickable v-ripple>
      <q-item-section>
        <q-item-label>Nhắc tôi tạm dừng xem</q-item-label>
      </q-item-section>
      <q-item-section side>
        <q-toggle
          v-model="settingsStore.player.enableRemindStop"
          size="sm"
          color="green"
        />
      </q-item-section>
    </q-item>
  </q-list>
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"
import { useSettingsStore } from "stores/settings"
import { useRouter } from "vue-router"

const router = useRouter()
const settingsStore = useSettingsStore()
</script>
