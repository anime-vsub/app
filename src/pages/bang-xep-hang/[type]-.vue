<template>
  <q-header class="bg-dark-page">
    <q-toolbar class="relative">
      <q-btn flat dense round class="mr-2" @click.stop="router.back()">
        <Icon icon="fluent:chevron-left-24-regular" width="25" height="25" />
      </q-btn>
      <q-toolbar-title
        class="absolute left-[50%] top-[50%] transform translate-x-[-50%] translate-y-[-50%] text-[16px] max-w-[calc(100%-34px*2)] line-clamp-1"
        >Bảng xếp hạng</q-toolbar-title
      >
    </q-toolbar>
    <div
      class="overflow-x-scroll whitespace-nowrap text-[#9a9a9a] text-center mb-2"
    >
      <div
        v-for="([name, value], index) in types"
        :ref="(el) => index === activeIndex && (pagItemActiveRef = el as HTMLDivElement)"
        :key="value"
        class="relative inline-block px-4 py-1"
        v-ripple
        :class="{
          'c--main text-weight-medium': index === activeIndex,
        }"
        @click="swiperRef?.slideTo(index)"
      >
        <span
          :class="
            index === activeIndex
              ? `relative inline-block before:content-DEFAULT before:absolute before:h-[2px] before:w-full before:bg-[currentColor] before:bottom-0 pb-[2px] before:rounded`
              : undefined
          "
          >{{ name }}</span
        >
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
        v-for="[, type] in types"
        :key="type"
        class="h-full overflow-y-auto scroll-smooth"
        style="white-space: pre-wrap"
      >
        <div
          v-if="
            !(_dataInStoreTmp = dataStore.get(type)) ||
            _dataInStoreTmp.status === 'pending'
          "
          class="h-full flex items-center"
        >
          <LaodingAnim />
        </div>
        <CardVertical
          v-else-if="_dataInStoreTmp.status === 'success'"
          v-for="(item, index) in _dataInStoreTmp.response"
          :key="item.name"
          :data="{
            ...item,
            description: item.othername,
            process: item.process.replace('Tập ', ''),
          }"
          class="mt-4 mx-3"
        >
          <template v-slot:img-content>
            <div class="update-info-layer" />
            <img
              v-if="index < 10"
              :src="`src/assets/bangumi_rank_ic_${index + 1}.png`"
              class="h-[1.5rem]"
            />
          </template>
        </CardVertical>
        <div v-else class="h-full flex items-center">
          <div class="text-center w-full">
            <img src="src/assets/ic_22_cry.png" width="240" class="mx-auto" />
            <br />
            <q-btn
              dense
              no-caps
              outline
              class="px-2"
              @click="fetchRankType(type)"
              style="color: #00be06"
              >Thử lại</q-btn
            >
          </div>
        </div>
      </swiper-slide>
    </swiper>
  </q-page>
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"
import CardVertical from "components/CardVertical.vue"
import LaodingAnim from "components/LaodingAnim.vue"
import { BangXepHangType } from "src/apis/bang-xep-hang/[type]"
import { scrollXIntoView } from "src/helpers/scrollXIntoView"
import type { Swiper as TSwiper } from "swiper"
import { Swiper, SwiperSlide } from "swiper/vue"
import { ref, shallowReactive, watch, watchEffect } from "vue"
import { useRouter } from "vue-router"

import "swiper/css"

const router = useRouter()

const types = [
  ["Mặc định", ""],
  ["Ngày", "day"],
  ["Tháng", "month"],
  ["Năm", "year"],
  ["Đánh giá", "voted"],
  ["Mùa", "season"],
]

const dataStore = shallowReactive<
  Map<
    string,
    | {
        status: "pending" | "error"
        response?: unknown
      }
    | {
        status: "success"
        response: Awaited<ReturnType<typeof BangXepHangType>>
      }
  >
>(new Map())
// eslint-disable-next-line functional/no-let
let _dataInStoreTmp:
  | { status: "pending" | "error"; response?: unknown }
  | {
      status: "success"
      response: {
        image: string
        path: string
        name: string
        othername: string
        process: string
      }[]
    }
  | undefined

async function fetchRankType(type: string) {
  if (dataStore.get(type)?.status === "success") return

  try {
    dataStore.set(type, {
      status: "pending",
    })
    dataStore.set(type, {
      status: "success",
      response: await BangXepHangType(type ? `${type}.html` : ""),
    })
  } catch (err) {
    dataStore.set(type, {
      status: "error",
      response: err,
    })
  }
}

const swiperRef = ref()
const activeIndex = ref(0)
const pagItemActiveRef = ref<HTMLDivElement>()

watch(
  activeIndex,
  (activeIndex) => {
    fetchRankType(types[activeIndex][1])
  },
  { immediate: true }
)

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

<style lang="scss" scoped>
.update-info-layer {
  background-image: linear-gradient(
    0deg,
    rgba(10, 12, 15, 0.8) 0%,
    rgba(10, 12, 15, 0.74) 4%,
    rgba(10, 12, 15, 0.59) 17%,
    rgba(10, 12, 15, 0.4) 34%,
    rgba(10, 12, 15, 0.21) 55%,
    rgba(10, 12, 15, 0.06) 78%,
    rgba(10, 12, 15, 0) 100%
  );
  background-color: transparent;
  min-height: 60px;
  position: absolute;
  padding: {
    left: 8px;
    right: 10px;
    bottom: 10px;
    top: 40px;
  }
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 2;

  font-size: 14px;
  font-weight: 500;
  @media screen and (max-width: 1680px) {
    font-size: 12px;
  }
  span {
    color: rgb(255, 255, 255);
    letter-spacing: 0px;
  }
  .star {
    position: absolute;
    right: 8px;
    // right: 10px;
    bottom: 10px;
  }
}
</style>
