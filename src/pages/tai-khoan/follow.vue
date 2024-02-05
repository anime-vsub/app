<template>
  <q-page-sticky
    position="top"
    class="bg-dark-page z-10 children:w-full children:py-2 children:!flex children:justify-between"
  >
    <div class="text-[18px] py-2 px-4">{{ t("anime-da-theo-doi") }}</div>

    <pagination.Pagination :max="data?.maxPage" class="mr-4" />
  </q-page-sticky>

  <!-- main -->

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
import { useHead } from "@vueuse/head"
import GridCard from "components/GridCard.vue"
import ScreenError from "components/ScreenError.vue"
import ScreenNotFound from "components/ScreenNotFound.vue"
import SkeletonGridCard from "components/SkeletonGridCard.vue"
import pagination from "components/pagination"
import type { QInfiniteScroll } from "quasar"
import { TuPhim } from "src/apis/runs/tu-phim"
import { usePage } from "src/composibles/page"
import { computed, ref } from "vue"
import { useI18n } from "vue-i18n"
import { useRequest } from "vue-request"

const { t } = useI18n()
const page = usePage()
useHead(
  computed(() => {
    const title = t("anime-dang-theo-doi")
    const description = title

    return {
      title,
      description,
      meta: [
        {
          property: "og:title",
          content: title
        },
        {
          property: "og:description",
          content: description
        },
        {
          property: "og:url"
        }
      ],
      link: [
        {
          rel: "canonical"
        }
      ]
    }
  })
)
const infiniteScrollRef = ref<QInfiniteScroll>()

const { data, loading, run, error } = useRequest(() => TuPhim(page.value), {
  refreshDeps: [page]
})

let nextPage = 2
async function onLoad(_index: number, done: (stop: boolean) => void) {
  const { curPage, maxPage, items } = await TuPhim(nextPage++)

  data.value = Object.assign(data.value ?? {}, {
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    items: [...data.value!.items, ...items],
    curPage,
    maxPage
  })
  done(curPage === maxPage)
}
</script>
