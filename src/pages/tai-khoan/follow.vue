<template>
  <q-header class="bg-dark-page">
    <q-toolbar class="relative">
      <q-btn flat dense round class="mr-2" @click.stop="router.back()">
        <Icon icon="fluent:chevron-left-24-regular" width="25" height="25" />
      </q-btn>
      <q-toolbar-title
        class="absolute left-[50%] top-[50%] transform translate-x-[-50%] translate-y-[-50%] text-[16px] max-w-[calc(100%-34px*2)] line-clamp-1">
        Anime đã theo dõi</q-toolbar-title>
    </q-toolbar>
  </q-header>

  <!-- main -->

  <div v-if="data?.items.length === 0" class="text-center py-20">
    <img
      src="~assets/img_tips_error_not_foud.png"
      width="186"
      height="174"
      class="mx-auto"
    />

    <div class="text-subtitle1 mt-1">Không tìm thấy gì cả.</div>
  </div>

  <template v-else>
    <SkeletonGridCard v-if="loading || !data" :count="12" />

    <q-infinite-scroll v-else @load="onLoad" :offset="250">
      <GridCard :items="data.items" />

      <template v-slot:loading>
        <div class="row justify-center q-my-md">
          <q-spinner class="c--main" size="40px" />
        </div>
      </template>
    </q-infinite-scroll>
  </template>
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"
import GridCard from "components/GridCard.vue"
import SkeletonGridCard from "components/SkeletonGridCard.vue"
import { TypeNormalValue } from "src/apis/runs/[type_normal]/[value]"
import { computed, reactive, ref, watch } from "vue"
import { useRequest } from "vue-request"
import { useRoute, useRouter } from "vue-router"
import { TuPhim } from "src/apis/runs/tu-phim"

const route = useRoute()

const { data, error, run, loading } = useRequest(
  () => TuPhim(1)
)

// eslint-disable-next-line functional/no-let
let nextPage = 2
async function onLoad(_index: number, done: (stop: boolean) => void) {
  const { curPage, maxPage, items } = await TuPhim(
    nextPage++
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

