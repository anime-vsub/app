<template>
  <div class="py-1 px-4 text-subtitle1 flex items-center justify-between">
    {{ gridModeTabsSeasons ? t("chon-season") : t("chon-tap") }}

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

  <div v-if="!seasons" class="flex-1 flex items-center justify-center">
    <q-spinner color="main" size="3em" :thickness="3" />
  </div>

  <template v-else>
    <q-tabs
      v-model="seasonActive"
      class="min-w-0 w-full tabs-seasons"
      :class="{
        'grid-mode scrollbar-custom': gridModeTabsSeasons,
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
        :ref="(el: QTab) => item.value === seasonActive && (tabsRef = el as QTab)"
      />
    </q-tabs>

    <q-tab-panels
      v-model="seasonActive"
      animated
      keep-alive
      class="flex-1 w-full bg-transparent panels-seasons"
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
            {{
              t("tap-moi-chieu-vao-_time-_day", [
                dayjs(
                  new Date(
                    `${_tmp.response.update[1]}:${_tmp.response.update[2]} 1/1/0`
                  )
                ).format("HH:MM"),
                _tmp.response.update[0] === 0
                  ? t("chu-nhat")
                  : t("thu-_day", [_tmp.response.update[0]]),
                _tmp.response.update[0] > new Date().getDay() + 1
                  ? t("tuan-sau")
                  : t("tuan-nay"),
              ])
            }}
          </div>

          <ChapsGridQBtn
            grid
            :chaps="_tmp.response.chaps"
            :season="value"
            :find="(item) => value === currentSeason && item.id === currentChap"
            :progress-chaps="_tmp.progressChaps"
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
import { QBtn, QSpinner, QTab, QTabPanel, QTabPanels, QTabs } from "quasar"
import { scrollXIntoView, scrollYIntoView } from "src/helpers/scrollIntoView"
import dayjs from "src/logic/dayjs"
import { ref, watch, watchEffect } from "vue"
import { useI18n } from "vue-i18n"

import type {
  ResponseDataSeasonError,
  ResponseDataSeasonPending,
  ResponseDataSeasonSuccess,
} from "../pages/phim/response-data-season"

const props = defineProps<{
  fetchSeason: (season: string) => Promise<void>
  seasons:
    | {
        name: string
        value: string
      }[]
    | undefined
  _cacheDataSeasons: Map<
    string,
    | ResponseDataSeasonPending
    | ResponseDataSeasonSuccess
    | ResponseDataSeasonError
  >
  currentSeason: undefined | string
  currentChap: string | undefined
}>()
const { t } = useI18n()

const seasonActive = ref<string>()
// sync data by active route

const gridModeTabsSeasons = ref(false)
watch(seasonActive, () => {
  gridModeTabsSeasons.value = false
})

watch(seasonActive, (seasonActive) => {
  if (!seasonActive) return

  // download data season active
  props.fetchSeason(seasonActive)
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

// eslint-disable-next-line functional/no-let
let _tmp:
  | ResponseDataSeasonPending
  | ResponseDataSeasonSuccess
  | ResponseDataSeasonError
  | undefined
</script>

<style lang="scss" scoped>
@import "../tabs-seasons.scss";
</style>
