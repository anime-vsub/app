<template>
  <q-page-sticky position="top" class="children:w-full bg-dark-page z-10">
    <div class="text-[16px] py-2 px-4">
      Tìm kiếm:
      <span class="text-weight-medium">{{ route.params.keyword }}</span>
    </div>
  </q-page-sticky>

  <div class="pt-[32px]">
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
  </div>
</template>

<script lang="ts" setup>
import GridCard from "components/GridCard.vue"
import ScreenError from "components/ScreenError.vue"
import ScreenNotFound from "components/ScreenNotFound.vue"
import SkeletonGridCard from "components/SkeletonGridCard.vue"
import { QInfiniteScroll } from "quasar"
import { TypeNormalValue } from "src/apis/runs/[type_normal]/[value]"
import { ref } from "vue"
import { useRequest } from "vue-request"
import { useRoute } from "vue-router"

const route = useRoute()
const infiniteScrollRef = ref()

const { data, loading, run } = useRequest(
  () => TypeNormalValue("tim-kiem", route.params.keyword, 1, true),
  {
    refreshDeps: [() => route.params.keyword],
    refreshDepsAction() {
      run()
      infiniteScrollRef.value?.reset()
    },
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
    maxPage,
  })
  done(curPage === maxPage)
}
</script>
