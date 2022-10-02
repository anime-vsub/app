<template>
  <q-header class="bg-dark-page">
    <q-toolbar class="relative">
      <q-btn flat dense round class="mr-2" @click.stop="router.back()">
        <Icon icon="fluent:chevron-left-24-regular" width="25" height="25" />
      </q-btn>
      <q-toolbar-title
        class="absolute left-[50%] top-[50%] transform translate-x-[-50%] translate-y-[-50%] text-[16px] max-w-[calc(100%-34px*2)] line-clamp-1"
        >Lịch chiếu</q-toolbar-title
      >
    </q-toolbar>

    <div
      class="overflow-x-scroll whitespace-nowrap text-[#9a9a9a] text-center mb-2"
    >
      <div
        v-for="(item, index) in data"
        :ref="(el) => activeIndex === index && (pagItemActiveRef = el)"
        :key="index"
        class="inline-block px-4"
        :class="{
          'c--main text-weight-medium':
            activeIndex === index ||
            dayjs(
              `${item.month}/${item.date}/${new Date().getFullYear()}`
            ).isToday(),
        }"
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
          }}<template v-if="item.month !== data[index - 1]?.month"
            >/{{ item.month }}</template
          >
        </span>
      </div>
    </div>
  </q-header>
  <q-page
    :style-fn="
      (offset, height) => ({
        height: `${height - offset}px`,
      })
    "
  >
    <swiper
      :slides-per-view="1"
      @swiper="onSwiper"
      @slideChange="onSlideChange"
      class="h-full"
    >
      <swiper-slide
        v-for="(item, index) in data"
        :key="index"
        class="h-full overflow-y-auto scroll-smooth"
        style="white-space: pre-wrap"
      >
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
              groupArray(item.items, 'time_release')
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
            />
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
            />
          </template>
        </template>
        <template
          v-else
          v-for="(items, time) in groupArray(item.items, 'time_release')"
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
          />
        </template>
      </swiper-slide>
    </swiper>
  </q-page>
</template>

<script lang="ts" setup>
import { LichChieuPhim } from "src/apis/lich-chieu-phim"
import { watch } from "vue"
import { useRequest } from "vue-request"
import { useRoute, useRouter } from "vue-router"
import { Icon } from "@iconify/vue"
import GridCard from "components/GridCard.vue"
import Card from "components/Card.vue"
import CardVertical from "components/CardVertical.vue"

import { dayTextToNum } from "src/logic/dayTextToNum"

import dayjs from "dayjs"
import isToday from "dayjs/plugin/isToday"

import { Swiper, SwiperSlide } from "swiper/vue"
import groupArray from "group-array"
// Import Swiper styles
import "swiper/css"

dayjs.extend(isToday)

const route = useRoute()
const router = useRouter()

const { data, loading, error } = useRequest(() => LichChieuPhim())
watch(error, (error) => {
  if (error)
    router.push({
      name: "not_found",
      params: { pathMatch: route.path },
      query: {
        message: error.message,
        cause: error.cause + "",
      },
    })
})

let _tmp

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
import { ref, watchEffect } from "vue"
import { scrollXIntoView } from "src/helpers/scrollXIntoView"

const swiperRef = ref()
const activeIndex = ref(0)
const pagItemActiveRef = ref<HTMLDivElement>()

watchEffect(() => {
  if (pagItemActiveRef.value) {
    console.log("scroll into view x")
    scrollXIntoView(pagItemActiveRef.value)
  }
})

function onSwiper(swiper) {
  swiperRef.value = swiper
  activeIndex.value = swiper.activeIndex
}
function onSlideChange(swiper) {
  activeIndex.value = swiper.activeIndex

  console.log(activeIndex.value)
}
</script>
