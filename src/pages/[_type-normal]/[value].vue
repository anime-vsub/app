<template>
  <div v-if="data?.filter" class="px-6">
    <div>
      <span class="text-subtitle1 text-white text-[14px] text-weight-medium"
        >Thể loại:
      </span>
      <q-btn
        v-for="item in data.filter.gener"
        :key="item.text"
        dense
        no-caps
        class="mx-1 text-[rgba(255,255,255,0.5)] font-weight-normal"
        :class="{
          'text-main font-weight-medium': genres.includes(item.value),
        }"
        @click="
          genres.includes(item.value)
            ? genres.splice(genres.indexOf(item.value) >>> 0)
            : genres.push(item.value)
        "
        outline
        flat
        :label="item.text"
      />
    </div>

    <div>
      <span class="text-subtitle1 text-white text-[14px] text-weight-medium"
        >Sắp xếp:
      </span>
      <q-btn
        v-for="item in data.filter.sorter"
        :key="item.value"
        dense
        no-caps
        class="mx-1 text-[rgba(255,255,255,0.5)] font-weight-normal"
        :class="{
          'text-main font-weight-medium': sorter === item.value,
        }"
        @click="sorter = item.value"
        outline
        flat
        :label="item.text"
      />
    </div>

    <div>
      <span class="text-subtitle1 text-white text-[14px] text-weight-medium"
        >Loại:
      </span>
      <q-btn
        v-for="item in data.filter.typer"
        :key="item.value"
        dense
        no-caps
        class="mx-1 text-[rgba(255,255,255,0.5)] font-weight-normal"
        :class="{
          'text-main font-weight-medium': typer === item.value,
        }"
        @click="typer = item.value"
        outline
        flat
        :label="item.text"
      />
    </div>

    <div>
      <span class="text-subtitle1 text-white text-[14px] text-weight-medium"
        >Mùa:
      </span>
      <q-btn
        v-for="item in data.filter.seaser"
        :key="item.value"
        dense
        no-caps
        class="mx-1 text-[rgba(255,255,255,0.5)] font-weight-normal"
        :class="{
          'text-main font-weight-medium': seaser === item.value,
        }"
        @click="seaser = item.value"
        outline
        flat
        :label="item.text"
      />
    </div>

    <div>
      <span class="text-subtitle1 text-white text-[14px] text-weight-medium"
        >Năm:
      </span>
      <q-btn
        v-for="item in data.filter.year"
        :key="item.value"
        dense
        no-caps
        class="mx-1 text-[rgba(255,255,255,0.5)] font-weight-normal"
        :class="{
          'text-main font-weight-medium': year === item.value,
        }"
        @click="year = item.value"
        outline
        flat
        :label="item.text"
      />
    </div>
  </div>

  <SkeletonGridCard v-if="loading" :count="12" />
  <template v-else-if="data">
    <ScreenNotFound v-if="data.items.length === 0" />

    <q-infinite-scroll
      v-else
      ref="infiniteScrollRef"
      @load="onLoad"
      :offset="250"
      class="px-4"
    >
      <GridCard :items="data.items" />

      <template v-slot:loading>
        <div class="row justify-center q-my-md">
          <q-spinner class="c--main" size="40px" />
        </div>
      </template>
    </q-infinite-scroll>
  </template>
  <ScreenError v-else @click:retry="run" />
</template>

<script lang="ts" setup>
import GridCard from "components/GridCard.vue"
import ScreenError from "components/ScreenError.vue"
import ScreenNotFound from "components/ScreenNotFound.vue"
import SkeletonGridCard from "components/SkeletonGridCard.vue"
import { QInfiniteScroll } from "quasar"
import { TypeNormalValue } from "src/apis/runs/[type_normal]/[value]"
import { computed, reactive, ref, watch } from "vue"
import { useRequest } from "vue-request"
import { useRoute } from "vue-router"

const route = useRoute()
const infiniteScrollRef = ref<QInfiniteScroll>()

const genres = reactive<string[]>([])
const seaser = ref<string | null>(null)
const sorter = ref<string | null>(null)
const typer = ref<string | null>(null)
const year = ref<string | null>(null)

const defaultsOptions = computed<{
  typer?: string
  gener?: string
  seaser?: string
  year?: string
}>(() => {
  // eslint-disable-next-line camelcase
  const { type_normal, value } = route.params as Record<string, string>

  // ":type_normal(quoc-gia|tag)/:value",
  // [":type_normal(season)/:value+"
  // eslint-disable-next-line camelcase
  switch (type_normal) {
    case "danh-sach":
      return {
        typer: value,
      }
    case "the-loai":
      return {
        gener: value,
      }
    case "season": {
      const [season, year] = value

      if (year) {
        return {
          seaser: season,
          year,
        }
      }

      return {
        seaser: season,
      }
    }
    default:
      return {}
  }
})

function fetchTypeNormalValue(page: number, onlyItems: boolean) {
  return TypeNormalValue(
    route.params.type_normal as string,
    route.params.value,
    page,
    onlyItems,
    {
      genres,
      seaser: seaser.value,
      sorter: sorter.value,
      typer: typer.value,
      year: year.value,
    },
    defaultsOptions.value
  )
}

const { data, run, loading } = useRequest(() => fetchTypeNormalValue(1, false))

const title = ref("")
const watcherData = watch(data, (data) => {
  if (!data) return

  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  title.value = route.params.type_normal === "danh-sach" &&
            route.params.value === "all"
              ? "Mục lục"
              : data.title!
  watcherData()
})



import { useHead } from "@vueuse/head"
useHead(computed(() => {

  if (!title.value) return {}



const description = title.value

  return {
    title : title.value,
    description,
    meta: [
      { property: "og:title", content: title.value },
      { property: "og:description", content: description },
      { property: "og:url", content: process.env.APP_URL + route.fullPath.slice(1) }
    ],
    link: [
      {
        rel: "canonical",
        href:  process.env.APP_URL + route.fullPath.slice(1)
      }
    ]
  }
}))

watch(
  [genres, seaser, sorter, typer, year, defaultsOptions],
  () => {
    run()
    infiniteScrollRef.value?.reset()
  },
  {
    deep: true,
  }
)

// eslint-disable-next-line functional/no-let
let nextPage = 2
async function onLoad(_index: number, done: (stop: boolean) => void) {
  const { curPage, maxPage, items } = await fetchTypeNormalValue(
    nextPage++,
    true
  )

  data.value = Object.assign(data.value ?? {}, {
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    items: [...data.value!.items, ...items],
    curPage,
    maxPage,
  })
  done(curPage === maxPage)
}
</script>
