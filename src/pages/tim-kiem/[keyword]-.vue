<template>
  <q-header class="bg-dark-page">
    <q-toolbar class="relative">
      <q-btn
        flat
        dense
        round
        class="mr-2"
        v-show="route.params.keyword || searching"
        @click="onBack"
      >
        <Icon icon="fluent:chevron-left-24-regular" width="25" height="25" />
      </q-btn>
      <div class="relative w-full">
        <input
          class="w-full bg-[#2a2a2a] placeholder-[#818181] h-[39px] rounded-[30px] focus-visible:outline-none pl-6"
          placeholder="Tìm kiếm"
          v-model="query"
          @keyup="query = ($event.target as HTMLInputElement).value"
          @focus="searching = true"
          @keypress.enter="onEnter"
        />
        <button
          class="text-[#aaa] absolute right-6 top-0 h-full flex items-center"
          v-show="query"
          @click="query = ''"
        >
          <Icon icon="ep:close-bold" />
        </button>
      </div>
    </q-toolbar>

    <div
      v-if="searching"
      class="fixed h-full w-full overflow-y-auto px-2 bg-dark-page"
    >
      <q-list dense>
        <q-item
          v-for="item in data"
          :key="typeof item === 'object' ? 'B' + item.name : item"
          clickable
          v-ripple
          @click="onClickItemPreLoad(item)"
        >
          <q-item-section avatar class="min-w-0">
            <Icon
              v-if="typeof item === 'object'"
              icon="fluent:search-24-regular"
            />
            <Icon v-else icon="fluent:history-24-regular" />
          </q-item-section>
          <q-item-section>
            <q-item-label class="flex items-center">
              <div class="max-w-full line-clamp-1">
                {{ typeof item === "object" ? item.name : item }}
              </div>
              <div v-if="typeof item === 'object'" class="text-grey pl-2">
                - {{ item.status.replace(/vietsub/i, "") }}
              </div>
            </q-item-label>
          </q-item-section>
          <q-item-section avatar class="min-w-0">
            <Icon
              icon="fluent:arrow-up-left-24-regular"
              @click.stop="onClickItemPreLoad(item, true)"
            />
          </q-item-section>
        </q-item>
      </q-list>
    </div>

    <q-toolbar v-if="!route.params.keyword && !searching">
      <div class="flex items-center mx-3">
        <div>
          <div class="title">Thịnh hành</div>
          <span
            class="ml-3 mr-1 h-[1rem] w-[1px] bg-gray-500 inline-block top-0 transform translate-x-[-50%]"
          />
        </div>
        <div class="overflow-x-auto text-[14px] text-grey">
          <div
            v-for="([name, value], index) in types"
            :key="value"
            class="inline-block px-2 py-2"
            :class="{
              'text-white text-weight-medium': activeIndex === index,
            }"
            @click="swiperRef?.slideTo(index)"
          >
            {{ name }}
          </div>
        </div>
      </div>
    </q-toolbar>
    <q-toolbar v-else>
      <div class="text-subtitle2 text-weight-regular mx-2">
        <span class="text-grey">Kết quả tìm kiếm cho: </span>{{ query }}
      </div>
    </q-toolbar>
  </q-header>

  <div v-if="!route.params.keyword" class="absolute top-0 h-[100%] w-full">
    <!-- swiper -->

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
            description: '',
            process: item.process.replace('Tập ', ''),
          }"
          class="mt-4 mx-3"
        >
          <template v-slot:img-content>
            <BottomBlur />
            <img v-if="index < 10" :src="ranks[index]" class="h-[1.5rem]" />
          </template>
        </CardVertical>
        <div v-else class="h-full flex items-center">
          <div class="text-center w-full">
            <img src="~assets/ic_22_cry.png" width="240" class="mx-auto" />
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
  </div>
  <template v-else>
    <div v-if="loadingSearch" class="absolute h-full w-full flex items-center">
      <LaodingAnim />
    </div>
    <template v-else-if="resultSearch">
      <div v-if="resultSearch.items.length === 0" class="text-center py-20">
        <img
          src="~assets/img_tips_error_not_foud.png"
          width="186"
          height="174"
          class="mx-auto"
        />

        <div class="text-subtitle1 mt-1">Không tìm thấy gì cả.</div>
      </div>

      <q-infinite-scroll v-else @load="moreSearch" :offset="250">
        <CardVertical
          v-for="(item, index) in resultSearch.items"
          :key="item.name"
          :data="{
            ...item,
            description: '',
            process: item.process.replace('Tập ', ''),
          }"
          class="mt-4 mx-3"
        >
          <template v-slot:img-content>
            <BottomBlur />
          </template>
        </CardVertical>

        <template v-slot:loading>
          <div class="row justify-center q-my-md">
            <q-spinner-dots color="primary" size="40px" />
          </div>
        </template>
      </q-infinite-scroll>
    </template>
    <div v-else class="absolute h-full w-full flex items-center">
      <div class="text-center w-full">
        <img src="~assets/ic_22_cry.png" width="240" class="mx-auto" />
        <br />
        <q-btn
          dense
          no-caps
          outline
          class="px-2"
          @click="runSearch"
          style="color: #00be06"
          >Thử lại</q-btn
        >
      </div>
    </div>
  </template>
</template>

<script lang="ts" setup>
import "swiper/css"
import { Icon } from "@iconify/vue"
// eslint-disable-next-line import/order
import BottomBlur from "components/BottomBlur.vue"
import "dayjs/locale/vi"

import CardVertical from "components/CardVertical.vue"
import LaodingAnim from "components/LaodingAnim.vue"
import dayjs from "dayjs"
import relativeTime from "dayjs/plugin/relativeTime"
import { debounce } from "quasar"
import { TypeNormalValue } from "src/apis/runs/[type_normal]/[value]"
import { AjaxItem } from "src/apis/runs/ajax/item"
import { PreSearch } from "src/apis/runs/pre-search"
import { useLocalStorage } from "src/composibles/useLocalStorage"
import ranks from "src/logic/ranks"
import type { Swiper as TSwiper } from "swiper"
import { Swiper, SwiperSlide } from "swiper/vue"
import { ref, shallowReactive, watch, watchEffect } from "vue"
import { useRequest } from "vue-request"
import { useRoute, useRouter } from "vue-router"

// ================= unknown ===============


dayjs.extend(relativeTime)

const types = [
  ["Tuần", "top-bo-week"],
  ["Ngày", "hot-viewed-today"],
  ["Tháng", "hot-top-voted"],
  ["Movie", "top-le-week"],
]

const route = useRoute()
const router = useRouter()

const searching = ref(false)
const query = ref((route.params.keyword ?? "") + "")

const historySearch = useLocalStorage<string[]>("history_search", [])

const debounceRun = debounce(() => run(), 100)

const { data, error, run } = useRequest(
  async () => [
    ...[...new Set([...historySearch.value, query.value])].filter((item) =>
      item.includes(query.value)
    ),
    ...(await PreSearch(query.value)),
  ],
  {
    refreshDeps: [query],
    manual: true,
    refreshDepsAction() {
      debounceRun()
    },
  }
)
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

watchEffect(() => {
  document.body.style.overflow = searching.value ? "hidden" : ""
})

function onEnter() {
  // save to history search
  historySearch.value = [...new Set([...historySearch.value, query.value])]
  if (route.params.keyword)
    router.replace(`/tim-kiem/${encodeURIComponent(query.value)}`)
  else router.push(`/tim-kiem/${encodeURIComponent(query.value)}`)

  searching.value = false
}
function onBack() {
  searching.value = false

  if (route.params.keyword) router.back()
}

async function onClickItemPreLoad(
  item: string | Awaited<ReturnType<typeof PreSearch>>[0],
  forceFilled?: boolean
) {
  if (typeof item === "string") {
    query.value = item

    return
  }

  if (forceFilled) {
    query.value = item.name

    return
  }

  // go
  await router.push(item.path)

  searching.value = false
}

const {
  loading: loadingSearch,
  data: resultSearch,
  run: runSearch,
} = useRequest(
  () => TypeNormalValue("tim-kiem", route.params.keyword, 1, true),
  {
    cacheKey: () => `tim-kiem/${route.params.keyword}`,
    cacheTime: 5 * 60 * 1000, // 5 minutes
  }
)
watch(() => route.params.keyword, runSearch, { immediate: true })

async function moreSearch(page: number, done: (noMore: boolean) => void) {
  if (
    resultSearch.value &&
    resultSearch.value.curPage === resultSearch.value.maxPage
  )
    return done(true)

  resultSearch.value = await TypeNormalValue(
    "tim-kiem",
    route.params.keyword,
    page + 1,
    true
  )

  done()
}

// =========== load top anime ============

const dataStore = shallowReactive<
  Map<
    string,
    | {
        status: "pending" | "error"
        response?: unknown
      }
    | {
        status: "success"
        response: Awaited<ReturnType<typeof AjaxItem>>
      }
  >
>(new Map())
// eslint-disable-next-line functional/no-let
let _dataInStoreTmp: ReturnType<typeof dataStore.get>

async function fetchRankType(type: string) {
  if (dataStore.get(type)?.status === "success") return

  try {
    dataStore.set(type, {
      status: "pending",
    })
    dataStore.set(type, {
      status: "success",
      response: await AjaxItem(type as "top-bo-week" | "top-le-week"),
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

watch(
  activeIndex,
  (activeIndex) => {
    fetchRankType(types[activeIndex][1])
  },
  { immediate: !route.params.keyword }
)

function onSwiper(swiper: TSwiper) {
  swiperRef.value = swiper
  activeIndex.value = swiper.activeIndex
}
function onSlideChange(swiper: TSwiper) {
  activeIndex.value = swiper.activeIndex
}
</script>

<style lang="scss" scoped>
.title {
  background-image: linear-gradient(0deg, #fbd786, #f7797d 70%);
  -webkit-background-clip: text;
  color: transparent;
  display: inline-block;
  font-weight: 900;
  font-style: italic;
  text-transform: uppercase;
  font-style: 16px;
}
</style>