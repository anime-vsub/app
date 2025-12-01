<template>
  <header class="fixed w-full top-0 left-0 z-200 bg-dark-page">
    <q-toolbar class="relative">
      <q-btn flat dense round class="mr-2" @click.stop="router.back()">
        <Icon icon="fluent:chevron-left-24-regular" width="25" height="25" />
      </q-btn>
      <q-toolbar-title
        class="absolute left-[50%] top-[50%] transform translate-x-[-50%] translate-y-[-50%] text-[16px] max-w-[calc(100%-34px*2)] line-clamp-1"
      >
        {{ t("anime-da-theo-doi") }}</q-toolbar-title
      >
    </q-toolbar>
  </header>

  <!-- main -->

  <SkeletonGridCard v-if="loading" :count="12" class="pt-[47px] px-4" />
  <template v-else-if="data">
    <ScreenNotFound v-if="data.items.length === 0" class="pt-[47px]" />

    <q-pull-to-refresh v-else @refresh="refresh" class="pt-[47px]">
      <q-infinite-scroll @load="onLoad" :offset="250" ref="infiniteScrollRef">
        <GridCard :items="data.items" />

        <template v-slot:loading>
          <div class="row justify-center q-my-md">
            <q-spinner class="c--main" size="40px" />
          </div>
        </template>
      </q-infinite-scroll>
    </q-pull-to-refresh>
  </template>
  <ScreenError v-else @click:retry="run" class="pt-[47px]" :error="error" />
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"
import { useHead } from "@vueuse/head"
import GridCard from "components/GridCard.vue"
import ScreenError from "components/ScreenError.vue"
import ScreenNotFound from "components/ScreenNotFound.vue"
import SkeletonGridCard from "components/SkeletonGridCard.vue"
import { QInfiniteScroll } from "quasar"
import { TuPhim } from "src/apis/runs/tu-phim"
import { isNative } from "src/constants"
import { computed, ref } from "vue"
import { useI18n } from "vue-i18n"
import { useRequest } from "vue-request"
import { useRouter } from "vue-router"

const router = useRouter()
const { t } = useI18n()
if (!isNative)
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
            content: title,
          },
          {
            property: "og:description",
            content: description,
          },
          {
            property: "og:url",
          },
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

const { data, loading, run, refreshAsync, error } = useRequest(() => TuPhim(1))
const refresh = (done: () => void) =>
  refreshAsync()
    .then(() => infiniteScrollRef.value?.reset())
    // eslint-disable-next-line promise/no-callback-in-promise
    .then(done)

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
