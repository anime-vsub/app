<template>
  <header class="fixed w-full top-0 left-0 z-200 bg-dark-page">
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
  </header>

  <ScreenLoading v-if="loading" class="absolute pt-[47px]" />
  <template v-else-if="histories">
    <ScreenNotFound v-if="histories.length === 0" class="pt-[47px]" />

    <q-pull-to-refresh v-else @refresh="refresh" class="pt-[47px] px-4">
      <q-infinite-scroll @load="onLoad" :offset="250">
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
              :src="forceHttp2(item.poster)"
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
    </q-pull-to-refresh>
  </template>
  <ScreenError v-else @click:retry="run" class="absolute mt-[47px]" />
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"
import BottomBlur from "components/BottomBlur.vue"
import ScreenError from "components/ScreenError.vue"
import ScreenLoading from "components/ScreenLoading.vue"
import ScreenNotFound from "components/ScreenNotFound.vue"
import { QInfiniteScroll } from "quasar"
import { History } from "src/apis/runs/history"
import { forceHttp2 } from "src/logic/forceHttp2"
import { parseTime } from "src/logic/parseTime"
import { ref } from "vue"
import { useRequest } from "vue-request"
import { useRouter } from "vue-router"

const router = useRouter()
const infiniteScrollRef = ref<QInfiniteScroll>()

const {
  loading,
  data: histories,
  run,
  refreshAsync,
} = useRequest(() => History())
const refresh = (done: () => void) =>
  refreshAsync()
    .then(() => infiniteScrollRef.value?.reset())
    // eslint-disable-next-line promise/no-callback-in-promise
    .then(done)

async function onLoad(page: number, done: (end: boolean) => void) {
  const items = await History(histories.value)

  histories.value = [...(histories.value ?? []), ...items]
  done(items.length === 0)
}
</script>
