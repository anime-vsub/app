<template>
  <header class="fixed z-1000 bg-dark-page top-0 w-full left-0">
    <q-toolbar class="relative">
      <q-btn
        flat
        dense
        round
        class="mr-2"
        v-show="keyword || searching"
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
          <q-item-section
            avatar
            class="min-w-0"
            @click.stop.prevent="onClickItemPreLoad(item, true)"
          >
            <Icon icon="fluent:arrow-up-left-24-regular" />
          </q-item-section>
        </q-item>
      </q-list>
    </div>

    <q-toolbar v-if="!keyword && !searching">
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
    <q-toolbar v-else-if="!searching && query">
      <div class="text-subtitle2 text-weight-regular mx-2">
        <span class="text-grey">Kết quả tìm kiếm cho: </span>{{ query }}
      </div>
    </q-toolbar>
  </header>

  <div v-if="!keyword" class="absolute top-0 h-[100%] w-full pt-[100px]">
    <!-- swiper -->

    <Swiper
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
        <ScreenLoading
          v-if="
            !(_dataInStoreTmp = dataStore.get(type)) ||
            _dataInStoreTmp.status === 'pending'
          "
        />
        <q-pull-to-refresh
          v-else-if="_dataInStoreTmp.status === 'success'"
          @refresh="refreshRank($event, type)"
        >
          <CardVertical
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
        </q-pull-to-refresh>
        <ScreenError v-else @click:retry="runSearch" :error="errorSearch" />
      </swiper-slide>
    </Swiper>
  </div>
  <template v-else>
    <ScreenLoading v-if="loadingSearch" class="absolute pt-[100px]" />
    <template v-else-if="resultSearch">
      <ScreenNotFound
        v-if="resultSearch.items.length === 0"
        class="absolute pt-[100px]"
      />

      <q-pull-to-refresh v-else @refresh="refresh">
        <q-infinite-scroll @load="moreSearch" :offset="250" class="pt-[100px]">
          <CardVertical
            v-for="item in resultSearch.items"
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
              <q-spinner class="c--main" size="40px" />
            </div>
          </template>
        </q-infinite-scroll>
      </q-pull-to-refresh>
    </template>
    <ScreenError
      v-else
      @click:retry="run"
      class="absolute pt-[100px]"
      :error="error"
    />
  </template>
</template>

<script lang="ts" setup>
import "swiper/css"
import { Icon } from "@iconify/vue"
import { useHead } from "@vueuse/head"
import BottomBlur from "components/BottomBlur.vue"
import CardVertical from "components/CardVertical.vue"
import ScreenError from "components/ScreenError.vue"
import ScreenLoading from "components/ScreenLoading.vue"
import ScreenNotFound from "components/ScreenNotFound.vue"
import {
  debounce,
  QBtn,
  QInfiniteScroll,
  QItem,
  QItemLabel,
  QItemSection,
  QList,
  QPullToRefresh,
  QSpinner,
  QToolbar,
} from "quasar"
import { TypeNormalValue } from "src/apis/runs/[type_normal]/[value]"
import { AjaxItem } from "src/apis/runs/ajax/item"
import { PreSearch } from "src/apis/runs/pre-search"
import { logEvent } from "src/boot/firebase"
import { useAliveScrollBehavior } from "src/composibles/useAliveScrollBehavior"
import { isNative } from "src/constants"
import ranks from "src/logic/ranks"
import { useHistorySearchStore } from "stores/history-search"
import type { Swiper as TSwiper } from "swiper"
import { Swiper, SwiperSlide } from "swiper/vue"
import { computed, ref, shallowReactive, watch, watchEffect } from "vue"
import { useI18n } from "vue-i18n"
import { useRequest } from "vue-request"
import { useRoute, useRouter } from "vue-router"

// Import Swiper Vue.js components
useAliveScrollBehavior()
// ================= unknown ===============

const types = [
  ["Tuần", "top-bo-week"],
  ["Ngày", "hot-viewed-today"],
  ["Tháng", "hot-top-voted"],
  ["Movie", "top-le-week"],
]

const router = useRouter()
const route = useRoute()
const { t } = useI18n()

const searching = ref(false)
const keyword = ref("")
const query = ref("")

const historySearchStore = useHistorySearchStore()


if (!isNative)
useHead(
  computed(() => {
    const title = t("tim-kiem-_keyword", [route.params.keyword])
    const description = title

    return {
      title,
      description,
      meta: [
        { property: "og:title", content: title },
        { property: "og:description", content: description },
        { property: "og:url" },
      ],
      link: [
        {
          rel: "canonical",
        },
      ],
    }
  })
)

const { data, run, error } = useRequest(
  async () => [
    ...[...new Set(historySearchStore.items)].filter((item) =>
      item.includes(query.value)
    ),
    ...(await PreSearch(query.value)),
  ],
  {
    manual: true,
  }
)
watch(
  query,
  debounce(() => {
    run()

    logEvent("search",{
        search_term: query.value,
    })
  }, 100)
)

watchEffect(() => {
  document.body.style.overflow = searching.value ? "hidden" : ""
})

function onEnter(event: Event) {
  // save to history search
  historySearchStore.items = [
    ...new Set([...historySearchStore.items, query.value]),
  ]

  keyword.value = query.value

  logEvent("search",{
      search_term: query.value,
  })

  searching.value = false
  ;(event.target as HTMLInputElement)?.blur()
}

function onBack() {
  searching.value = false

  if (keyword.value) keyword.value = ""
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
  refreshAsync: refreshAsyncSearch,
  error: errorSearch,
} = useRequest(() => TypeNormalValue("tim-kiem", keyword.value, 1, true))
// eslint-disable-next-line promise/no-callback-in-promise
const refresh = (done: () => void) => refreshAsyncSearch().then(done)
watch(keyword, runSearch, {
  immediate: true,
})

async function moreSearch(page: number, done: (noMore?: boolean) => void) {
  if (
    resultSearch.value &&
    resultSearch.value.curPage === resultSearch.value.maxPage
  )
    return done(true)

  const newData = await TypeNormalValue(
    "tim-kiem",
    keyword.value,
    page + 1,
    true
  )

  resultSearch.value = {
    ...newData,
    items: [...(resultSearch.value?.items ?? []), ...newData.items],
  }

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
async function refreshRank(done: () => void, type: string) {
  dataStore.set(type, {
    status: "success",
    response: await AjaxItem(type as "top-bo-week" | "top-le-week"),
  })

  done()
}

const swiperRef = ref()
const activeIndex = ref(0)

watch(
  activeIndex,
  (activeIndex) => {
    fetchRankType(types[activeIndex][1])
  },
  { immediate: true }
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
