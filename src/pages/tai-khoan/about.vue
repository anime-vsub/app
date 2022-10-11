<template>
  <q-header class="bg-dark-page">
    <q-toolbar class="relative">
      <q-btn flat dense round class="mr-2" @click.stop="router.back()">
        <Icon icon="fluent:chevron-left-24-regular" width="25" height="25" />
      </q-btn>
      <q-toolbar-title
        class="absolute left-[50%] top-[50%] transform translate-x-[-50%] translate-y-[-50%] text-[16px] max-w-[calc(100%-34px*2)] line-clamp-1"
      >
        Giới thiệu về AnimeVsub
      </q-toolbar-title>
    </q-toolbar>
  </q-header>


  <q-list v-if="infoApp && infoDev">
    <q-item>
      <q-item-section>
        <q-item-label>Phiên bản ứng dụng</q-item-label>
        <q-item-label caption>{{ infoApp.name }} {{ infoApp.version }} build {{ infoApp.build }}</q-item-label>
      </q-item-section>
    </q-item>
    <q-item>
      <q-item-section>
        <q-item-label>Hệ điều hành</q-item-label>
        <q-item-label caption>{{infoDev.operatingSystem}} {{ infoDev.osVersion }}; {{infoDev.name}}</q-item-label>
      </q-item-section>
    </q-item>
    <q-item>
      <q-item-section>
        <q-item-label>WebView</q-item-label>
        <q-item-label caption>{{infoDev.webViewVersion}}</q-item-label>
      </q-item-section>
    </q-item>
  </q-list>
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"
import { useRouter } from "vue-router"
import { App } from '@capacitor/app'
import { Device } from '@capacitor/device'
import {shallowRef} from "vue"

const router = useRouter()

const infoApp = shallowRef()
const infoDev = shallowRef()

App.getInfo().then(data => infoApp.value= data)
Device.getInfo().then(data => infoDev.value = data)
</script>
