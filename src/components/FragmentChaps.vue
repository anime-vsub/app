<template>
  <div class="py-1 px-4 text-subtitle1 flex items-center justify-between">
    {{ gridModeTabsSeasons ? t("chon-season") : t("chon-tap") }}

    <div>
      <q-btn
        dense
        round
        @click="
          () => {
            if (stateStorageStore.disableAutoRestoration >= 2)
              stateStorageStore.disableAutoRestoration = 0
            else stateStorageStore.disableAutoRestoration++
          }
        "
        :class="
          ['', 'text-orange-12', 'text-orange-14'][
            stateStorageStore.disableAutoRestoration
          ]
        "
      >
        <Icon
          :icon="
            [
              'iconoir:cloud-sync',
              'iconoir:cloud-upload',
              'iconoir:cloud-error'
            ][stateStorageStore.disableAutoRestoration]
          "
          width="20"
          height="20"
        />

        <q-tooltip
          anchor="bottom middle"
          self="top middle"
          class="text-14px max-w-150px"
          :delay="1000"
          >{{
            [
              t("vo-hieu-hoa-tam-thoi-khoi-phuc-tien-trinh-xem-truoc"),
              t("vo-hieu-hoa-ca-2-chieu-dong-bo-tien-trinh-xem"),
              t("kich-hoat-lai-khoi-phuc-tien-trinh-xem")
            ][stateStorageStore.disableAutoRestoration]
          }}</q-tooltip
        >
      </q-btn>
      <q-btn dense round @click="gridModeTabsSeasons = !gridModeTabsSeasons">
        <Icon
          :icon="
            gridModeTabsSeasons
              ? 'fluent:grid-kanban-20-regular'
              : 'fluent:apps-list-24-regular'
          "
          width="20"
          height="20"
        />
      </q-btn>
    </div>
  </div>

  <div v-if="!seasons" class="flex-1 flex items-center justify-center">
    <q-spinner color="main" size="3em" :thickness="3" />
  </div>
  <template v-else>
    <q-tabs
      v-model="seasonActive"
      class="min-w-0 w-full tabs-seasons"
      :class="{
        'grid-mode scrollbar-custom': gridModeTabsSeasons
      }"
      no-caps
      dense
      inline-label
      active-class="c--main"
      v-if="
        seasons.length > 1 || (seasons.length === 0 && seasons[0].name !== '')
      "
    >
      <q-tab
        v-for="item in seasons"
        :key="item.value"
        :name="item.value"
        :label="item.name"
        :ref="
          (el: QTab) =>
            void (item.value === seasonActive && (tabsRef = el as QTab))
        "
      />
    </q-tabs>

    <q-tab-panels
      v-model="seasonActive"
      animated
      keep-alive
      class="flex-1 w-full bg-transparent panels-seasons"
      v-show="!gridModeTabsSeasons"
    >
      <q-tab-panel
        v-for="({ value }, index) in seasons"
        :key="index"
        :name="value"
      >
        <div
          v-if="
            !(_tmp = _cacheDataSeasons.get(value)) || _tmp.status === 'pending'
          "
          class="absolute top-[50%] left-[50%] transform -translate-x-1/2 -translate-y-1/2"
        >
          <q-spinner style="color: #00be06" size="3em" :thickness="3" />
        </div>
        <div
          v-else-if="_tmp.status === 'error'"
          class="absolute top-[50%] left-[50%] text-center transform -translate-x-1/2 -translate-y-1/2"
        >
          {{ t("loi-khi-lay-du-lieu") }}
          <br />
          <q-btn
            dense
            no-caps
            rounded
            style="color: #00be06"
            @click="fetchSeason(value)"
            >{{ t("thu-lai") }}</q-btn
          >
        </div>

        <template v-else>
          <div v-if="_tmp.response.update" class="mb-2 text-gray-300">
            <MessageScheludeChap :update="_tmp.response.update" />
          </div>

          <ChapsGridQBtn
            grid
            :chaps="_tmp.response.chaps"
            :season="value"
            :find="(item) => value === currentSeason && item.id === currentChap"
            :progress-chaps="
              (progressWatchStore.get(value) as unknown as any)?.response
            "
            class-item="px-3 !py-[6px] mb-3"
          />
        </template>
      </q-tab-panel>
    </q-tab-panels>
  </template>
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"
import ChapsGridQBtn from "components/ChapsGridQBtn.vue"
import MessageScheludeChap from "components/feat/MessageScheludeChap.vue"
import { QBtn, QSpinner, QTab, QTabPanel, QTabPanels, QTabs } from "quasar"
import { scrollXIntoView, scrollYIntoView } from "src/helpers/scrollIntoView"
import type {
  ProgressWatchStore,
  Season
} from "src/pages/phim/_season.interface"
import { useStateStorageStore } from "src/stores/state-storage"
import { ref, watch, watchEffect } from "vue"
import { useI18n } from "vue-i18n"

import type {
  ResponseDataSeasonError,
  ResponseDataSeasonPending,
  ResponseDataSeasonSuccess
} from "../pages/phim/response-data-season"

const props = defineProps<{
  fetchSeason: (season: string) => Promise<void>
  seasons?: Season[] | undefined
  _cacheDataSeasons: Map<
    string,
    | ResponseDataSeasonPending
    | ResponseDataSeasonSuccess
    | ResponseDataSeasonError
  >
  currentSeason?: undefined | string
  currentChap?: string | undefined
  progressWatchStore: ProgressWatchStore
}>()
const { t } = useI18n()

const stateStorageStore = useStateStorageStore()

const seasonActive = ref<string>()
// sync data by active route
watch(
  () => props.currentSeason,
  (val) => (seasonActive.value = val),
  {
    immediate: true
  }
)

watch(seasonActive, (seasonActive) => {
  if (!seasonActive) return

  // download data season active
  void props.fetchSeason(seasonActive)
})

const gridModeTabsSeasons = ref(false)
watch(seasonActive, () => {
  gridModeTabsSeasons.value = false
})

// @scrollIntoView
const tabsRef = ref<QTab>()
watchEffect(() => {
  if (!tabsRef.value) return
  if (!props.currentSeason) return

  // eslint-disable-next-line no-unused-expressions
  gridModeTabsSeasons.value // watch value

  setTimeout(() => {
    console.log("scroll now")
    if (tabsRef.value?.$el) {
      if (gridModeTabsSeasons.value) scrollYIntoView(tabsRef.value.$el)
      else scrollXIntoView(tabsRef.value.$el)
    }
  }, 70)
})

let _tmp:
  | ResponseDataSeasonPending
  | ResponseDataSeasonSuccess
  | ResponseDataSeasonError
  | undefined
</script>

<style lang="scss" scoped>
@import "../pages/phim/tabs-seasons.scss";
</style>
