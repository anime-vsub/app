<template>
  <q-header class="bg-dark-page">
    <q-toolbar class="relative">
      <q-btn flat dense round class="mr-2" @click.stop="router.back()">
        <Icon icon="fluent:chevron-left-24-regular" width="25" height="25" />
      </q-btn>
      <q-toolbar-title
        class="absolute left-[50%] top-[50%] transform translate-x-[-50%] translate-y-[-50%] text-[16px] max-w-[calc(100%-34px*2)] line-clamp-1"
      >
        Cài đặt chung
      </q-toolbar-title>
    </q-toolbar>
  </q-header>

  <q-list>
    <q-item clickable v-ripple to="/tai-khoan/settings/player">
      <q-item-section>
        <q-item-label>Cài đặt phát</q-item-label>
      </q-item-section>
    </q-item>
    <q-item
      clickable
      v-ripple
      href="mailto:tachibshin@duck.com?subject=Phản hồi ứng dụng git.shin.animevsub"
    >
      <q-item-section>
        <q-item-label>Phản hồi</q-item-label>
      </q-item-section>
    </q-item>
    <q-item clickable v-ripple to="/tai-khoan/settings/about">
      <q-item-section>
        <q-item-label>Về AnimeVsub</q-item-label>
      </q-item-section>
    </q-item>
    <q-item
      clickable
      v-ripple
      @click="open('https://animevsub.github.io/tems-of-use')"
    >
      <q-item-section>
        <q-item-label>Điều khoản sử dụng</q-item-label>
      </q-item-section>
    </q-item>
    <q-item
      clickable
      v-ripple
      @click="open('https://animevsub.github.io/privacy-police')"
    >
      <q-item-section>
        <q-item-label>Chính sách riêng tư</q-item-label>
      </q-item-section>
    </q-item>
    <q-item
      clickable
      v-ripple
      @click="open('https://animevsub.github.io/disclaimer')"
    >
      <q-item-section>
        <q-item-label>Tuyên bố từ chối trách nhiệm</q-item-label>
      </q-item-section>
    </q-item>
  </q-list>
</template>

<script lang="ts" setup>
import { Browser } from "@capacitor/browser"
import { Icon } from "@iconify/vue"
import { useRouter } from "vue-router"

const router = useRouter()

function open(url: string) {
  Browser.open({ url })
}
</script>
