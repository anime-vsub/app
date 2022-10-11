<template>
  <q-header class="bg-dark-page">
    <q-toolbar class="relative">
      <q-btn flat dense round class="mr-2" @click.stop="router.back()">
        <Icon icon="fluent:chevron-left-24-regular" width="25" height="25" />
      </q-btn>
      <q-toolbar-title
        class="absolute left-[50%] top-[50%] transform translate-x-[-50%] translate-y-[-50%] text-[16px] max-w-[calc(100%-34px*2)] line-clamp-1"
      >
        Anime đã xem</q-toolbar-title
      >
    </q-toolbar>
  </q-header>

  <ScreenLoading v-if="loading" class="absolute" />
  <template v-else-if="histories">
    <ScreenNotFound v-if="data.items.length === 0" />

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
                item.timestamp.get("month")
          }}
        </div>
        <div
          class="bg-transparent flex mt-1 mb-4"
          style="white-space: initial"
          @click="router.push(`/phim/${item.id}/${item.last.chap}`)"
        >
          <q-img
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
  <ScreenError v-else @click:retry="run" class="absolute" />
</template>

<script lang="ts" setup>
import type { Timestamp } from "@firebase/firestore"
import {
  collection,
  getDocs,
  getFirestore,
  limit,
  orderBy,
  query,
  where,
} from "@firebase/firestore"
import { Icon } from "@iconify/vue"
import { app } from "boot/firebase"
import BottomBlur from "components/BottomBlur.vue"
import LaodingAnim from "components/LaodingAnim.vue"
import dayjs from "dayjs"
import isToday from "dayjs/plugin/isToday"
import isYesterday from "dayjs/plugin/isYesterday"
import { parseTime } from "src/logic/parseTime"
import { useAuthStore } from "stores/auth"
import { useRequest } from "vue-request"
import { useRouter } from "vue-router"
import { History } from "src/apis/runs/history"

import ScreenLoading from "components/ScreenLoading.vue"
import ScreenError from "components/ScreenError.vue"
import ScreenNotFound from "components/ScreenNotFound.vue"

dayjs.extend(isToday)
dayjs.extend(isYesterday)

const router = useRouter()

const { loading, data: histories, run } = useRequest(() => History())

async function onLoad(page: number, done: (end: boolean) => void) {
  const items = await History(histories.value)

  histories.value = [...(histories.value ?? []), ...items]
  done(items.length === 0)
}
</script>
