<template>
  <header class="fixed w-full top-0 left-0 z-200 bg-dark-page">
    <q-toolbar class="relative">
      <q-btn flat dense round class="mr-2" @click.stop="router.back()">
        <Icon icon="fluent:chevron-left-24-regular" width="25" height="25" />
      </q-btn>
      <q-toolbar-title
        class="absolute left-[50%] top-[50%] transform translate-x-[-50%] translate-y-[-50%] text-[16px] max-w-[calc(100%-34px*2)] line-clamp-1"
        >Lịch chiếu</q-toolbar-title
      >
    </q-toolbar>

    <q-toolbar>
      <div
        class="overflow-x-scroll whitespace-nowrap text-[#9a9a9a] text-center mb-2"
      >
        <div
          v-for="(item, index) in data"
          :ref="(el) => activeIndex === index && (pagItemActiveRef = el as HTMLDivElement)"
          :key="index"
          class="relative inline-block px-4"
          v-ripple
          :class="{
            'c--main text-weight-medium':
              activeIndex === index ||
              dayjs(
                `${item.month}/${item.date}/${new Date().getFullYear()}`
              ).isToday(),
          }"
          @click="swiperRef?.slideTo(index)"
        >
          T{{ dayTextToNum(item.day) }}
          <br />
          <span
            :class="
              activeIndex === index
                ? 'relative inline-block before:content-DEFAULT before:absolute before:h-[2px] before:w-full before:bg-[currentColor] before:bottom-0 pb-[2px] before:rounded'
                : undefined
            "
          >
            {{ item.date
            }}<template v-if="item.month !== data?.[index - 1]?.month"
              >/{{ item.month }}</template
            >
          </span>
        </div>
      </div>
    </q-toolbar>
  </header>

  <ScreenLoading v-if="loading" class="absolute mt-[47px]" />

  <div v-else-if="data" class="absolute top-0 h-[100%] w-full pt-[47px]">
    <swiper
      class="relative h-full w-full"
      :slides-per-view="1"
      @swiper="onSwiper"
      @slideChange="onSlideChange"
    >
      <swiper-slide
        v-for="(item, index) in data"
        :key="index"
        class="h-full overflow-y-auto scroll-smooth"
        style="white-space: pre-wrap"
      >
        <q-pull-to-refresh @refresh="refresh">
          <template
            v-if="
              dayjs(
                `${item.month}/${item.date}/${new Date().getFullYear()}`
              ).isToday()
            "
          >
            <!-- overtime -->
            <template
              v-for="[time, items] in (_tmp = splitOverTime(
              groupArray(item.items, 'time_release') as unknown as  Record<string, Awaited<ReturnType<typeof LichChieuPhim>>[0]['items']>
            ))[0]"
              :key="time"
            >
              <div class="text-grey text-[12px] mt-7 mb-2 flex items-center">
                <span class="h-[1px] w-[12px] bg-grey inline-block" />
                {{ dayjs(+time).format("HH:mm") }}
              </div>

              <CardVertical
                v-for="anime in items"
                :key="anime.name"
                :data="anime"
                class="mx-3"
              >
                <template v-slot:img-content>
                  <BottomBlur />
                </template>
              </CardVertical>
            </template>

            <div
              class="w-full my-7 flex relative items-center justify-center before:content-DEFAULT before:absolute before:w-full before:h-[1px] before:bg-[rgba(200,200,200,0.5)] text-[14px]"
            >
              <span class="px-2 bg-dark-page z-2 text-grey"
                >Bây giờ:{{ dayjs().format("HH:mm") }}</span
              >
            </div>

            <!-- next on today -->
            <template v-for="[time, items] in _tmp[1]" :key="time">
              <div class="text-[12px] mt-7 mb-2 flex items-center">
                <span class="h-[1px] w-[12px] bg-[currentColor] inline-block" />
                {{ dayjs(+time).format("HH:mm") }}
              </div>

              <CardVertical
                v-for="anime in items"
                :key="anime.name"
                :data="anime"
                class="mx-3"
              >
                <template v-slot:img-content>
                  <BottomBlur />
                </template>
              </CardVertical>
            </template>
          </template>
          <template
            v-else
            v-for="(items, time) in (groupArray(item.items, 'time_release') as Record<string, typeof item.items>)"
            :key="time"
          >
            <div class="text-[12px] mt-7 mb-2 flex items-center">
              <span class="h-[1px] w-[12px] bg-[currentColor] inline-block" />
              {{ dayjs(+time).format("HH:mm") }}
            </div>

            <CardVertical
              v-for="anime in items"
              :key="anime.name"
              :data="anime"
              class="mx-3"
            >
              <template v-slot:img-content>
                <BottomBlur />
              </template>
            </CardVertical>
          </template>
        </q-pull-to-refresh>
      </swiper-slide>
    </swiper>
  </div>

  <ScreenError v-else class="absolute mt-[47px]" />
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"
import BottomBlur from "components/BottomBlur.vue"
import CardVertical from "components/CardVertical.vue"
import ScreenError from "components/ScreenError.vue"
import ScreenLoading from "components/ScreenLoading.vue"
import dayjs from "dayjs"
import isToday from "dayjs/plugin/isToday"
import groupArray from "group-array"
import { LichChieuPhim } from "src/apis/runs/lich-chieu-phim"
// Import Swiper styles
import "swiper/css"
import { scrollXIntoView } from "src/helpers/scrollXIntoView"
import { dayTextToNum } from "src/logic/dayTextToNum"
import type { Swiper as TSwiper } from "swiper"
import { Swiper, SwiperSlide } from "swiper/vue"
import { ref, watchEffect } from "vue"
import { useRequest } from "vue-request"
import { useRouter } from "vue-router"

dayjs.extend(isToday)

const router = useRouter()

const { loading, data, refreshAsync } = useRequest(() => LichChieuPhim())
// eslint-disable-next-line promise/no-callback-in-promise
const refresh = (done: () => void) => refreshAsync().then(done)

// eslint-disable-next-line functional/no-let
let _tmp: ReturnType<typeof splitOverTime>

function splitOverTime(
  items: Record<string, Awaited<ReturnType<typeof LichChieuPhim>>[0]["items"]>
) {
  const overWrite = Object.entries(items)
  const now = Date.now()

  const indexFirstItemNextTime =
    overWrite.findIndex(([time]) => {
      return +time > now
    }) >>> 0

  return [
    overWrite.slice(0, indexFirstItemNextTime),
    overWrite.slice(indexFirstItemNextTime),
  ]
}

const swiperRef = ref()
const activeIndex = ref(0)
const pagItemActiveRef = ref<HTMLDivElement>()

watchEffect(() => {
  if (pagItemActiveRef.value) {
    console.log("scroll into view x")
    scrollXIntoView(pagItemActiveRef.value)
  }
})

function onSwiper(swiper: TSwiper) {
  swiperRef.value = swiper
  activeIndex.value = swiper.activeIndex
}
function onSlideChange(swiper: TSwiper) {
  activeIndex.value = swiper.activeIndex
}
</script>
