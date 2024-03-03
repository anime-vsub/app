<template>
  <q-page-sticky
    position="top"
    class="bg-dark-page z-10 children:w-full children:py-2 children:!flex children:justify-between"
  >
    <div class="text-[18px] py-2 px-4">
      <template v-if="title">
        {{ title }}

        <span v-if="textFilter" class="text-grey text-[14px]"
          ><span class="mx-1">&bull;</span>{{ textFilter }}</span
        >
      </template>
      <q-skeleton
        v-else
        type="rect"
        width="180px"
        height="16px"
        class="rounded-lg"
      />
    </div>

    <pagination.Pagination :max="data?.maxPage" class="mr-4" />
  </q-page-sticky>

  <div class="pt-[48px]">
    <div v-if="data?.filter" class="px-4 mt-2">
      <div v-if="!defaultsOptions.gener">
        <span class="text-subtitle1 text-white text-[14px] text-weight-medium"
          >{{ t("the-loai") }}
        </span>
        <q-btn
          v-for="item in data.filter.gener"
          :key="item.text"
          dense
          no-caps
          class="mx-1 text-[rgba(255,255,255,0.5)] font-weight-normal"
          :class="{
            '!text-main font-weight-medium': allGenres.includes(item.value)
          }"
          :value="item.value"
          @click="
            allGenres.includes(item.value)
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
          >{{ t("sap-xep") }}
        </span>
        <q-btn
          v-for="item in data.filter.sorter"
          :key="item.value"
          dense
          no-caps
          class="mx-1 text-[rgba(255,255,255,0.5)] font-weight-normal"
          :class="{
            '!text-main font-weight-medium': sorter === item.value
          }"
          @click="sorter = item.value"
          outline
          flat
          :label="item.text"
        />
      </div>

      <div>
        <span class="text-subtitle1 text-white text-[14px] text-weight-medium"
          >{{ t("loai") }}
        </span>
        <q-btn
          v-for="item in data.filter.typer"
          :key="item.value"
          dense
          no-caps
          class="mx-1 text-[rgba(255,255,255,0.5)] font-weight-normal"
          :class="{
            '!text-main font-weight-medium': typer === item.value
          }"
          @click="typer = item.value"
          outline
          flat
          :label="item.text"
        />
      </div>

      <div>
        <span class="text-subtitle1 text-white text-[14px] text-weight-medium"
          >{{ t("mua") }}
        </span>
        <q-btn
          v-for="item in data.filter.seaser"
          :key="item.value"
          dense
          no-caps
          class="mx-1 text-[rgba(255,255,255,0.5)] font-weight-normal"
          :class="{
            '!text-main font-weight-medium': seaser === item.value
          }"
          @click="seaser = item.value"
          outline
          flat
          :label="item.text"
        />
      </div>

      <div>
        <span class="text-subtitle1 text-white text-[14px] text-weight-medium"
          >{{ t("nam") }}
        </span>
        <q-btn
          v-for="item in data.filter.year"
          :key="item.value"
          dense
          no-caps
          class="mx-1 text-[rgba(255,255,255,0.5)] font-weight-normal"
          :class="{
            '!text-main font-weight-medium': year === item.value
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

      <pagination.InfiniteScroll v-else ref="infiniteScrollRef" @load="onLoad">
        <GridCard :items="data.items" />
      </pagination.InfiniteScroll>
    </template>
    <ScreenError v-else @click:retry="run" :error="error" />
  </div>
</template>

<script lang="ts" setup>
import { useHead } from "@vueuse/head"
import GridCard from "components/GridCard.vue"
import ScreenError from "components/ScreenError.vue"
import ScreenNotFound from "components/ScreenNotFound.vue"
import SkeletonGridCard from "components/SkeletonGridCard.vue"
import pagination from "components/pagination"
import type { QInfiniteScroll } from "quasar"
import { TypeNormalValue } from "src/apis/runs/[type_normal]/[value]"
import { usePage } from "src/composibles/page"
import { computed, reactive, ref, watch } from "vue"
import { useI18n } from "vue-i18n"
import { useRequest } from "vue-request"
import { useRoute } from "vue-router"

const { t } = useI18n()
const route = useRoute()
const infiniteScrollRef = ref<QInfiniteScroll>()
const page = usePage()

let inited = false
const defaultsOptions = computed<{
  typer?: string
  gener?: string[]
  seaser?: string
  year?: string
}>(() => {
  // eslint-disable-next-line camelcase
  const { type_normal, value } = route.params
  // ":type_normal(quoc-gia|tag)/:value",
  // [":type_normal(season)/:value+"
  // eslint-disable-next-line camelcase
  switch (type_normal) {
    case "danh-sach":
      return {
        typer: value as string
      }
    case "the-loai": {
      if (inited)
        return {
          gener: data.value?.filter?.gener
            .filter((item) => item.checked)
            .map((item) => item.value) ?? [value as string]
        }

      return {
        gener: [value as string]
      }
    }
    case "season": {
      const [season, year] =
        typeof value === "string" ? value.replace(/\/*$/, "").split("/") : value

      if (year) {
        return {
          seaser: season,
          year
        }
      }

      return {
        seaser: season
      }
    }
    default:
      return {}
  }
})

const genres = reactive<string[]>([])
const seaser = ref<string | null>(null)
const sorter = ref<string | null>(null)
const typer = ref<string | null>(null)
const year = ref<string | null>(null)

const allGenres = computed(() => {
  return [...(defaultsOptions.value.gener ?? []), ...genres]
})

watch(
  defaultsOptions,
  (options) => {
    seaser.value ??= options.seaser ?? null
    typer.value ??= options.typer ?? null
    year.value ??= options.year ?? null
  },
  { immediate: true }
)

const { data, run, loading, error } = useRequest(
  () => fetchTypeNormalValue(page.value, false),
  {
    refreshDeps: [page]
  }
)

function fetchTypeNormalValue(page: number, onlyItems: boolean) {
  const r = TypeNormalValue(
    route.params.type_normal as string,
    typeof route.params.value === "string"
      ? route.params.value.replace(/\/*$/, "")
      : route.params.value,
    page,
    onlyItems,
    {
      genres: allGenres.value,
      seaser: seaser.value,
      sorter: sorter.value,
      typer: typer.value,
      year: year.value
    },
    defaultsOptions.value
  )
  inited = true

  return r
}

const title = ref("")
const watcherData = watch(data, (data) => {
  if (!data) return

  title.value =
    route.params.type_normal === "danh-sach" && route.params.value === "all"
      ? t("muc-luc")
      : // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        data.title!
  watcherData()
})
useHead(
  computed(() => {
    if (!title.value) return {}

    const description = title.value

    return {
      title: title.value,
      description,
      meta: [
        { property: "og:title", content: title.value },
        { property: "og:description", content: description },
        {
          property: "og:url",
          content: process.env.APP_URL + route.fullPath.slice(1)
        }
      ],
      link: [
        {
          rel: "canonical",
          href: process.env.APP_URL + route.fullPath.slice(1)
        }
      ]
    }
  })
)

const textFilter = computed(() => {
  return (
    data.value &&
    data.value.filter &&
    [
      data.value.filter.gener
        .filter((item) => allGenres.value.includes(item.value))
        .map((item) => item.text)
        .join(", "),
      data.value.filter.seaser.find((item) => item.value === seaser.value)
        ?.text,
      data.value.filter.sorter.find((item) => item.value === sorter.value)
        ?.text,
      data.value.filter.typer.find((item) => item.value === typer.value)?.text,
      data.value.filter.year.find((item) => item.value === year.value)?.text
    ]
      .filter(Boolean)
      .join(" • ")
  )
})

watch(
  [allGenres, seaser, sorter, typer, year],
  () => {
    console.log("refresh by watcher options")
    run()
    infiniteScrollRef.value?.reset()
  },
  { deep: true }
)
watch(defaultsOptions, (newVal, oldVal) => {
  if (JSON.stringify(newVal) !== JSON.stringify(oldVal)) {
    console.log("refresh by watcher options")

    run()
    infiniteScrollRef.value?.reset()
  }
})

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
    maxPage
  })
  done(curPage === maxPage)
}
</script>
