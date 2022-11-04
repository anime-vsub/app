<template>
  <q-page-sticky position="top" class="children:w-full bg-dark-page z-10">
    <div class="text-[16px] py-2 px-4">{{ t("anime-da-theo-doi") }}</div>
  </q-page-sticky>

  <!-- main -->

  <div class="pt-[32px]">
    <SkeletonGridCard v-if="loading" :count="12" />
    <template v-else-if="data">
      <ScreenNotFound v-if="data.items.length === 0" />

      <q-infinite-scroll
        v-else
        @load="onLoad"
        :offset="250"
        ref="infiniteScrollRef"
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
  </div>
</template>

<script lang="ts" setup>
import { useHead } from "@vueuse/head"
import GridCard from "components/GridCard.vue"
import ScreenError from "components/ScreenError.vue"
import ScreenNotFound from "components/ScreenNotFound.vue"
import SkeletonGridCard from "components/SkeletonGridCard.vue"
import { QInfiniteScroll } from "quasar"
import { TuPhim } from "src/apis/runs/tu-phim"
import { computed, ref } from "vue"
import { useI18n } from "vue-i18n"
import { useRequest } from "vue-request"

const { t } = useI18n()
useHead(
  computed(() => {
    const title = t("anime-dang-theo-doi")
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
const infiniteScrollRef = ref<QInfiniteScroll>()

const { data, loading, run } = useRequest(() => TuPhim(1))

// eslint-disable-next-line functional/no-let
let nextPage = 2
async function onLoad(_index: number, done: (stop: boolean) => void) {
  const { curPage, maxPage, items } = await TuPhim(nextPage++)

  data.value = Object.assign(data.value ?? {}, {
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    items: [...data.value!.items, ...items],
    curPage,
    maxPage,
  })
  done(curPage === maxPage)
}
</script>
