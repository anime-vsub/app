<template>
  <header class="fixed w-full top-0 left-0 z-200 bg-dark-page">
    <q-toolbar class="relative">
      <q-btn flat dense round class="mr-2" @click.stop="router.back()">
        <Icon icon="fluent:chevron-left-24-regular" width="25" height="25" />
      </q-btn>
      <q-toolbar-title
        class="absolute left-[50%] top-[50%] transform translate-x-[-50%] translate-y-[-50%] text-[16px] max-w-[calc(100%-34px*2)] line-clamp-1"
      >
        {{ t("chon-ngon-ngu-cua-ban") }}
      </q-toolbar-title>
    </q-toolbar>
  </header>

  <q-list class="pt-[47px]">
    <q-item
      v-for="{ name, code } in languages"
      :key="code"
      clickable
      v-ripple
      class="rounded-xl"
      @click="settingsStore.locale = code"
    >
      <q-item-section avatar class="min-w-0">
        <Icon
          v-if="settingsStore.locale === code"
          icon="fluent:checkmark-24-regular"
          width="20"
          height="20"
        />
        <span v-else class="block w-[20px]" />
      </q-item-section>
      <q-item-section>{{ name }}</q-item-section>
    </q-item>
  </q-list>
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"
import { languages } from "src/i18n"
import { useSettingsStore } from "stores/settings"
import { useI18n } from "vue-i18n"
import { useRouter } from "vue-router"

const settingsStore = useSettingsStore()
const { t } = useI18n()
const router = useRouter()
</script>
