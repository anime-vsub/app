<template>
  <header class="fixed w-full top-0 left-0 z-200 bg-dark-page">
    <q-toolbar class="relative">
      <q-btn flat dense round class="mr-2" @click.stop="router.back()">
        <Icon icon="fluent:chevron-left-24-regular" width="25" height="25" />
      </q-btn>
      <q-toolbar-title
        class="absolute left-[50%] top-[50%] transform translate-x-[-50%] translate-y-[-50%] text-[16px] max-w-[calc(100%-34px*2)] line-clamp-1"
      >
        {{ t("gioi-thieu-ve-animevsub") }}
      </q-toolbar-title>
    </q-toolbar>
  </header>

  <q-list v-if="infoApp && infoDev" class="pt-[47px]">
    <q-item clickable v-ripple>
      <q-item-section>
        <q-item-label>{{ t("phien-ban-ung-dung") }}</q-item-label>
        <q-item-label caption
          >{{ infoApp.name }} {{ infoApp.version }} build
          {{ infoApp.build }}</q-item-label
        >
      </q-item-section>
    </q-item>
    <q-item clickable v-ripple>
      <q-item-section>
        <q-item-label>{{ t("he-dieu-hanh") }}</q-item-label>
        <q-item-label caption
          >{{ infoDev.operatingSystem }} {{ infoDev.osVersion }};
          {{ infoDev.name }}</q-item-label
        >
      </q-item-section>
    </q-item>
    <q-item clickable v-ripple>
      <q-item-section>
        <q-item-label>WebView</q-item-label>
        <q-item-label caption>{{ infoDev.webViewVersion }}</q-item-label>
      </q-item-section>
    </q-item>
  </q-list>
</template>

<script lang="ts" setup>
import { App } from "@capacitor/app"
import { Device } from "@capacitor/device"
import { Icon } from "@iconify/vue"
import { useQuasar } from "quasar"
import { shallowRef } from "vue"
import { useI18n } from "vue-i18n"
import { useRouter } from "vue-router"

const router = useRouter()
const $q = useQuasar()
const { t } = useI18n()

const infoApp = shallowRef()
const infoDev = shallowRef()

Promise.all([
  App.getInfo().then((data) => (infoApp.value = data)),
  Device.getInfo().then((data) => (infoDev.value = data)),
]).catch(() => {
  $q.notify({
    position: "bottom-right",
    message: t("khong-the-lay-du-lieu"),
  })
})
</script>
