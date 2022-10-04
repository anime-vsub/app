<template>
  <q-header class="bg-dark-page">
    <q-toolbar>
      <q-toolbar-title class="text-center absolute w-full"
        >Tin tức</q-toolbar-title
      >
      <q-space />

      <div
        class="rounded-[30px] border border-gray-500 inline-flex items-center justify-center overflow-hidden"
      >
        <div
          class="w-1/2 pl-3 pr-1 py-1 relative"
          v-ripple
          @click="viewMode = 1"
          :class="{
            'bg-light-300 text-dark': viewMode === 1,
          }"
        >
          <Icon
            v-if="viewMode === 1"
            icon="fluent:grid-24-filled"
            width="20"
            height="20"
          />
          <Icon v-else icon="fluent:grid-24-regular" width="20" height="20" />
        </div>
        <div
          class="w-1/2 pr-3 pl-1 py-1 relative"
          v-ripple
          @click="viewMode = 2"
          :class="{
            'bg-light-300 text-dark': viewMode === 2,
          }"
        >
          <Icon
            v-if="viewMode === 2"
            icon="fluent:apps-list-detail-24-filled"
            width="20"
            height="20"
          />
          <Icon
            v-else
            icon="fluent:apps-list-detail-24-regular"
            width="20"
            height="20"
          />
        </div>
      </div>
    </q-toolbar>
  </q-header>

  <div class="absolute fit overflow-hidden" v-if="data.length === 0">
    <q-card
      v-for="item in 12"
      :key="item"
      class="mx-3 mt-5 rounded-xl overflow-hidden"
      :class="{
        'news-item': viewMode === 2,
      }"
    >
      <div>
        <q-responsive :ratio="369 / 194">
          <q-skeleton class="absolute w-full h-full image" square />
        </q-responsive>
      </div>

      <div class="content">
        <q-card-section>
          <div class="text-h6 leading-snug !font-normal">
            <q-skeleton type="text" width="40%" />
          </div>
          <div class="text-grey mt-1 line-clamp-3 des">
            <q-skeleton type="text" width="100%" />
            <q-skeleton type="text" width="60%" />
          </div>
        </q-card-section>
        <q-card-section class="pt-0">
          <span class="text-caption text-grey flex items-center">
            <q-skeleton
              type="rect"
              width="20px"
              height="20px"
              class="inline-block mr-[5px]"
            />
            <q-skeleton type="text" width="4em" /> &bull;
            <q-skeleton type="text" width="6em" />
          </span>
        </q-card-section>
      </div>
    </q-card>
  </div>

  <q-infinite-scroll v-else @load="onLoad" :offset="250">
    <q-card
      v-for="item in data"
      :key="item.title"
      class="relative mx-3 mt-5 rounded-xl overflow-hidden"
      :class="{
        'news-item': viewMode === 2,
      }"
      v-ripple
      @click="open(item.href, item.title)"
    >
      <div>
        <q-img :src="item.image" class="image" />
      </div>

      <div class="content">
        <q-card-section>
          <div class="text-h6 leading-snug !font-normal">
            {{ item.title }}
          </div>
          <div class="text-grey mt-1 line-clamp-3 des">{{ item.intro }}</div>
        </q-card-section>
        <q-card-section class="pt-0">
          <span class="text-caption text-grey">
            <img
              :src="item.by.icon"
              width="18"
              height="18"
              class="inline-block mr-[5px]"
            />{{ item.by.name }} &bull;
            {{ dayjs(item.time).locale("vi").fromNow() }}
          </span>
        </q-card-section>
      </div>
    </q-card>

    <template v-slot:loading>
      <div class="row justify-center q-my-md">
        <q-spinner-dots color="primary" size="40px" />
      </div>
    </template>
  </q-infinite-scroll>
</template>

<script lang="ts" setup>
import "dayjs/locale/vi"
import { Browser } from "@capacitor/browser"
import { Icon } from "@iconify/vue"
import dayjs from "dayjs"
import relativeTime from "dayjs/plugin/relativeTime"
import { NewsAnime } from "src/apis/runs/news-anime"
import { ref, shallowReactive } from "vue"

// https://tinanime.com/api/news/?p=3

dayjs.extend(relativeTime)

const viewMode = ref<1 | 2>(1)

const data = shallowReactive<Awaited<ReturnType<typeof NewsAnime>>>([])

// eslint-disable-next-line @typescript-eslint/no-empty-function
onLoad(1, () => {})

async function onLoad(page: number, done: (noMore: boolean) => void) {
  console.log(page)
  const news = await NewsAnime(page)
  data.push(...news)

  done(news.length === 0)
}
function open(url: string, title: string) {
  Browser.open({ url, windowName: title })
}
</script>

<style lang="scss" scoped>
.news-item {
  display: flex;
  @apply bg-transparent;
  .image {
    width: 135px;
    height: auto;
    @apply rounded-lg;
  }
  .content {
    @apply flex-1 pl-2 py-1;
    .text-h6 {
      @apply text-[14px];
    }
    > * {
      padding: 0;
    }
    .des {
      display: none;
    }
    .text-caption {
      @apply pt-2;
      display: block;
    }
  }
}
</style>
