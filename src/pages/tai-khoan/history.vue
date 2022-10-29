<template>
  <q-page-sticky position="top" class="children:w-full bg-dark-page z-10">
    <div class="text-[16px] py-2 px-4">Anime đã xem</div>
  </q-page-sticky>

  <div class="pt-[32px]">
    <ScreenLoading v-if="loading" />
    <template v-else-if="histories">
      <ScreenNotFound v-if="histories.length === 0" />

      <q-infinite-scroll v-else @load="onLoad" :offset="250">
        <template v-for="(item, index) in histories" :key="item.id">
          <div
            v-if="
              !histories[index - 1] ||
              !histories[index - 1].timestamp.isSame(item.timestamp, 'day')
            "
            class="text-subtitle2 text-weight-normal"
          >
            {{
              item.timestamp.isToday()
                ? "Hôm nay"
                : item.timestamp.isYesterday()
                ? "Hôm qua"
                : item.timestamp.get("date") +
                  " thg " +
                  (item.timestamp.get("month") + 1)
            }}
          </div>
          <div
            class="bg-transparent flex mt-1 mb-4"
            style="white-space: initial"
            @click="router.push(`/phim/${item.id}/${item.last.chap}`)"
          >
            <q-img
              no-spinner
              :src="item.poster"
              :ratio="1920 / 1080"
              class="!rounded-[4px] w-[min(210px,40%)]"
            >
              <BottomBlur class="px-0 h-[40%]">
                <div
                  class="absolute bottom-0 left-0 z-10 w-full min-h-0 !py-0 !px-0"
                >
                  <q-linear-progress
                    :value="item.last.cur / item.last.dur"
                    rounded
                    color="main"
                    class="!h-[3px]"
                  />
                </div>
              </BottomBlur>
              <span
                class="absolute text-white z-10 text-[12px] bottom-2 right-2"
                >{{ parseTime(item.last.cur) }}</span
              >
            </q-img>

            <div class="pl-2">
              <span class="line-clamp-3 mt-1">{{ item.name }}</span>
              <div class="text-grey mt-1">
                {{ item.seasonName }} tập {{ item.last.name }}
              </div>
              <div class="text-grey mt-2">
                Xem lúc
                {{
                  item.timestamp.format(
                    item.timestamp.isToday() ? "HH:mm" : "DD/MM/YYYY"
                  )
                }}
              </div>
            </div>
          </div>
        </template>

        <template v-slot:loading>
          <div class="row justify-center q-my-md">
            <q-spinner class="c--main" size="40px" />
          </div>
        </template>
      </q-infinite-scroll>
    </template>
    <ScreenError v-else @click:retry="run" />
  </div>
</template>

<script lang="ts" setup>
import BottomBlur from "components/BottomBlur.vue"
import ScreenError from "components/ScreenError.vue"
import ScreenLoading from "components/ScreenLoading.vue"
import ScreenNotFound from "components/ScreenNotFound.vue"
import { QInfiniteScroll } from "quasar"
import { History } from "src/apis/runs/history"
import { parseTime } from "src/logic/parseTime"
import { useRequest } from "vue-request"
import { useRouter } from "vue-router"

import {computed}from"vue"
import { useHead } from "@vueuse/head"
useHead(computed(() => {
const title = "Lịch sử xem Anime"
const description = title

  return {
    title : title,
    description,
    meta: [
      { property: "og:title", content: title },
      { property: "og:description", content: description },
      { property: "og:url" }
    ],
    link: [
      {
        rel: "canonical",
      }
    ]
  }
}))

const router = useRouter()

const {
  loading,
  data: histories,
  run,
} = useRequest(() => History())

async function onLoad(page: number, done: (end: boolean) => void) {
  const items = await History(histories.value)

  histories.value = [...(histories.value ?? []), ...items]
  done(items.length === 0)
}
</script>
