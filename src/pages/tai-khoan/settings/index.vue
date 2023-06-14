<template>
  <header class="fixed w-full top-0 left-0 z-200 bg-dark-page">
    <q-toolbar class="relative">
      <q-btn flat dense round class="mr-2" @click.stop="router.back()">
        <Icon icon="fluent:chevron-left-24-regular" width="25" height="25" />
      </q-btn>
      <q-toolbar-title
        class="absolute left-[50%] top-[50%] transform translate-x-[-50%] translate-y-[-50%] text-[16px] max-w-[calc(100%-34px*2)] line-clamp-1"
      >
        {{ t("cai-dat-chung") }}
      </q-toolbar-title>
    </q-toolbar>
  </header>

  <q-list class="pt-[47px]">
    <q-item clickable v-ripple to="/tai-khoan/settings/player">
      <q-item-section>
        <q-item-label>{{ t("cai-dat-phat") }}</q-item-label>
      </q-item-section>
    </q-item>
    <q-item
      clickable
      v-ripple
      href="mailto:ogmo2r3q@duck.com?subject=Phản hồi ứng dụng git.shin.animevsub"
    >
      <q-item-section>
        <q-item-label>{{ t("phan-hoi") }}</q-item-label>
      </q-item-section>
    </q-item>
    <q-item clickable v-ripple to="/tai-khoan/settings/player">
      <q-item-section>
        <q-item-label>{{ t("cai-dat-phat") }}</q-item-label>
      </q-item-section>
    </q-item>
    <q-item clickable v-ripple to="/tai-khoan/settings/language">
      <q-item-section>
        <q-item-label>{{ t("ngon-ngu") }}</q-item-label>
      </q-item-section>
    </q-item>
    <q-item clickable v-ripple to="/tai-khoan/settings/about">
      <q-item-section>
        <q-item-label>{{ t("ve-animevsub") }}</q-item-label>
      </q-item-section>
    </q-item>
    <q-item
      clickable
      v-ripple
      @click="open('https://anime-vsub.github.io/about/tems-of-use?embed=true')"
    >
      <q-item-section>
        <q-item-label>{{ t("dieu-khoan-su-dung") }}</q-item-label>
      </q-item-section>
    </q-item>
    <q-item
      clickable
      v-ripple
      @click="
        open('https://anime-vsub.github.io/about/privacy-police?embed=true')
      "
    >
      <q-item-section>
        <q-item-label>{{ t("chinh-sach-rieng-tu") }}</q-item-label>
      </q-item-section>
    </q-item>
    <q-item
      clickable
      v-ripple
      @click="open('https://anime-vsub.github.io/about/disclaimer?embed=true')"
    >
      <q-item-section>
        <q-item-label>{{ t("tuyen-bo-tu-choi-trach-nhiem") }}</q-item-label>
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
