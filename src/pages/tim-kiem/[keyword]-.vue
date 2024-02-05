<template>
  <q-page-sticky
    position="top"
    class="bg-dark-page z-10 children:w-full children:py-2 children:!flex children:justify-between"
  >
    <div class="text-[18px] py-2 px-4">
      <span class="text-gray-400 mr-1">{{ t("tim-kiem") }}: </span>
      <span class="font-bold truncate">{{ route.params.keyword }}</span>
      <span v-if="data" class="text-gray-300 text-[14px]">
        <span class="mx-3">&bull;</span
        >{{ t("_maxPage-trang", [data?.maxPage]) }} kết quả
      </span>
    </div>
    <pagination.Pagination :max="data?.maxPage" class="mr-4" />
  </q-page-sticky>

  <div class="pt-[48px]">
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
import { getAnalytics, logEvent } from "@firebase/analytics"
import { useHead } from "@vueuse/head"
import GridCard from "components/GridCard.vue"
import ScreenError from "components/ScreenError.vue"
import ScreenNotFound from "components/ScreenNotFound.vue"
import SkeletonGridCard from "components/SkeletonGridCard.vue"
import pagination from "components/pagination"
import { TypeNormalValue } from "src/apis/runs/[type_normal]/[value]"
import { usePage } from "src/composibles/page"
import { computed, ref, watch } from "vue"
import { useI18n } from "vue-i18n"
import { useRequest } from "vue-request"
import { useRoute } from "vue-router"

const { t } = useI18n()
const route = useRoute()
const infiniteScrollRef = ref()
const page = usePage()
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
        { property: "og:url" }
      ],
      link: [
        {
          rel: "canonical"
        }
      ]
    }
  })
)
// analytics
const analytics = getAnalytics()
watch(
  () => route.params.keyword,
  (keyword) =>
    keyword &&
    logEvent(analytics, "search", { search_term: keyword as string }),
  { immediate: true }
)

const { data, loading, run, error } = useRequest(
  () => TypeNormalValue("tim-kiem", route.params.keyword, page.value, true),
  {
    refreshDeps: [() => route.params.keyword, page],
    refreshDepsAction() {
      run()
      infiniteScrollRef.value?.reset()
    }
  }
)

async function onLoad(_index: number, done: (stop: boolean) => void) {
  const { curPage, maxPage, items } = await TypeNormalValue(
    "tim-kiem",
    route.params.keyword,
    (data.value?.curPage ?? 0) + 1,
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
