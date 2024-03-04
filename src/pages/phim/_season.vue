<template>
  <!-- skeleton first load -->
  <template v-if="!data">
    <div class="w-full overflow-hidden fixed top-0 left-0 z-200">
      <div
        class="absolute top-0 left-0 w-full h-full flex items-center justify-center pointer-events-none z-200"
      >
        <q-spinner size="60px" :thickness="3" />
      </div>
    </div>
  </template>
  <template v-else>
    <BrtPlayer
      v-if="configPlayer?.playTech !== 'trailer'"
      :sources="sources"
      :current-season="currentSeason"
      :name-current-season="currentMetaSeason?.name"
      :current-chap="currentChap"
      :name-current-chap="currentMetaChap?.name"
      :eng-name-current-chap="episodeOpEnd?.title"
      :next-chap="nextChap"
      :prev-chap="prevChap"
      :name="data?.name"
      :poster="currentDataSeason?.poster ?? data.poster"
      :seasons="seasons"
      :_cache-data-seasons="_cacheDataSeasons"
      :fetch-season="fetchSeason"
      :progressWatchStore="progressWatchStore"
      :intro="inoutroEpisode?.intro"
      :outro="inoutroEpisode?.outro"
      @cur-update="
        currentProgresWatch?.set($event.id, {
          cur: $event.cur,
          dur: $event.dur,
        })
      "
    />
    <div v-else class="w-full overflow-hidden fixed top-0 left-0 z-200">
      <q-img-custom
        no-spinner
        v-if="!sources?.[0]?.file"
        :ratio="16 / 9"
        src="~assets/ic_question_result_error.png"
        width="100"
        class="max-w-[100px]"
      />
      <q-video v-else :ratio="16 / 9" :src="sources![0]!.file" />
    </div>
  </template>

  <div
    v-if="!error && loading && !data"
    class="absolute w-full h-full overflow-hidden px-4 pt-6 text-[28px]"
  >
    <q-responsive :ratio="16 / 9" />

    <q-skeleton type="text" class="text-[35px]" width="80%" />
    <q-skeleton type="text" width="100px" class="mt-[-10px]" />

    <div class="flex flex-nowrap">
      <q-skeleton type="text" width="100px" class="mr-2" />
      <q-skeleton type="text" width="140px" class="mr-1" />
    </div>

    <div class="flex mt-1 flex-nowrap mt-[-10px]">
      <q-skeleton type="text" width="38px" class="mr-2" />
      <q-skeleton type="text" width="38px" class="mr-2" />
      <q-skeleton type="text" width="70px" class="mr-2" />
      <q-skeleton type="text" width="40px" />
    </div>

    <div class="flex flex-nowrap mt-[-10px]">
      <q-skeleton type="text" width="3em" class="mr-2" />
      <q-skeleton type="text" width="4em" class="mr-2" />
      <q-skeleton type="text" width="5em" class="mr-2" />
      <q-skeleton type="text" width="5em" />
    </div>

    <div class="mt-3">
      <q-skeleton type="rect" width="100%" height="48px" />
    </div>

    <SkeletonGridCard class="mt-3" :count="12" />
  </div>

  <div v-else-if="!error && data" class="mx-4">
    <q-responsive :ratio="16 / 9" />

    <div v-ripple @click="showDialogInforma = true" class="relative mt-6">
      <div class="relative flex items-center justify-between">
        <div class="flex-1 mt-4 mb-2">
          <h1
            class="line-clamp-2 text-weight-medium py-0 my-0 text-[18px] leading-normal"
          >
            {{ data.name }}
          </h1>
          <h5 class="text-gray-400 text-weight-normal">
            {{ t("formatview-data-views-luot-xem", [formatView(data.views)]) }}

            <span v-if="currentDataSeason?.update">
              &bull;

              <MessageScheludeChap :update="currentDataSeason.update" />
            </span>
          </h5>
        </div>

        <Icon
          icon="fluent:chevron-right-24-regular"
          width="18"
          height="18"
          class="text-gray-400 mb-4"
        />
      </div>
    </div>

    <div class="text-gray-400">
      {{ t("tac-gia") }}
      <template v-for="(item, index) in data.authors" :key="item.name">
        <router-link :to="item.path" class="text-[rgb(28,199,73)]">{{
          item.name
        }}</router-link
        ><template v-if="index < data.authors.length - 1">, </template>
      </template>
      <div class="divider"></div>
      {{ t("san-xuat-boi-_studio", [data.studio]) }}
    </div>

    <div class="text-[rgb(230,230,230)] mt-3">
      <Quality>{{ data.quality }}</Quality>
      <div class="divider"></div>
      {{ data.yearOf }}
      <div class="divider"></div>
      {{ t("cap-nhat-toi-tap-_duration", [data.duration]) }}
      <div class="divider"></div>
      <router-link
        v-for="item in data.contries"
        :key="item.name"
        :to="item.path"
        class="text-[rgb(28,199,73)]"
      >
        {{ item.name }}
      </router-link>
      <div class="divider"></div>

      <br />

      <div class="inline-flex items-center">
        <div class="text-[16px] text-weight-medium mr-1">
          {{ data.rate }}
        </div>
        <Star />
      </div>
      <div class="divider"></div>
      <span class="text-gray-400">
        {{ t("_rate-nguoi-danh-gia", [formatView(data.count_rate)]) }}
      </span>
      <div class="divider"></div>
      <!-- <span class="text-gray-400">
          {{ formatView(data.follows) }} người theo dõi
        </span> -->

      <router-link v-if="data.seasonOf" class="c--main" :to="data.seasonOf.path"
        >{{ data.seasonOf.name }}
      </router-link>
    </div>

    <div class="tags mt-1 break-words">
      <router-link
        v-for="item in data.genre"
        :key="item.name"
        :to="item.path"
        class="text-[rgb(28,199,73)]"
      >
        {{ t("tag-_val", [item.name.replace(/ /, "_")]) }}
      </router-link>
    </div>

    <div class="my-2">
      <q-btn
        stack
        no-caps
        class="mr-4 text-weight-normal"
        @click="toggleFollow"
      >
        <Icon
          v-if="followed"
          icon="material-symbols:bookmark-added-outline-rounded"
          width="28"
          height="28"
        />
        <Icon
          v-else
          icon="material-symbols:bookmark-add-outline-rounded"
          width="28"
          height="28"
        />
        <span class="text-[12px] mt-1">{{
          follows ? formatView(follows) : "Theo dõi"
        }}</span>
      </q-btn>
      <q-btn stack no-caps class="mr-4 text-weight-normal" @click="share">
        <Icon icon="fluent:share-ios-24-regular" width="28" height="28" />
        <span class="text-[12px] mt-1">{{ t("chia-se") }}</span>
      </q-btn>
      <q-btn
        stack
        no-caps
        class="mr-4 text-weight-normal"
        @click="clickShowPlaylist"
      >
        <Icon
          icon="fluent:add-square-multiple-16-regular"
          width="28"
          height="28"
        />
        <span class="text-[12px] mt-1">{{ t("luu") }}</span>
      </q-btn>
    </div>

    <div
      class="w-full py-2 relative"
      v-ripple
      @click="showDialogChapter = true"
    >
      <div class="flex items-center justify-between text-subtitle2 w-full">
        {{ t("Tap") }}

        <div class="flex no-wrap justify-end">
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
                  'iconoir:cloud-error',
                ][stateStorageStore.disableAutoRestoration]
              "
              width="20"
              height="20"
            />
          </q-btn>
          <span class="flex items-center text-gray-300 font-weight-normal">
            {{ t("tron-bo") }}
            <q-icon name="chevron_right" class="mr-[-8px]"></q-icon>
          </span>
        </div>
      </div>
    </div>

    <q-tab-panels
      v-model="seasonActive"
      animated
      keep-alive
      class="h-full bg-transparent overflow-y-visible whitespace-nowrap mb-3 mx-[-8px] panels-navigator"
    >
      <q-tab-panel
        v-for="{ value } in seasons"
        :key="value"
        :name="value"
        class="!h-[47px] overflow-y-visible py-0 !px-0"
      >
        <div
          v-if="_cacheDataSeasons.get(value)?.status === 'pending'"
          class="flex justify-center"
        >
          <q-spinner-infinity class="c--main" size="3em" :thickness="3" />
        </div>
        <div
          v-else-if="_cacheDataSeasons.get(value)?.status === 'error'"
          class="text-center"
        >
          {{ t("loi-khi-lay-du-lieu") }}
          <br />
          <q-btn
            dense
            no-caps
            style="color: #00be06"
            @click="fetchSeason(value)"
            rounded
            >{{ t("thu-lai") }}</q-btn
          >
        </div>
        <ChapsGridQBtn
          v-else
          :chaps="
            (
              _cacheDataSeasons.get(value) as
                | ResponseDataSeasonSuccess
                | undefined
            )?.response.chaps
          "
          :season="value"
          :find="(item) => value === currentSeason && item.id === currentChap"
          :progressChaps="
            (progressWatchStore.get(value) as unknown as any)?.response
          "
          scroll-x
        />
      </q-tab-panel>
    </q-tab-panels>

    <q-tabs
      v-model="seasonActive"
      no-caps
      dense
      inline-label
      active-class="c--main"
      indicator-color="transparent"
      v-if="
        seasons &&
        (seasons.length > 1 || (seasons.length === 0 && seasons[0].name !== ''))
      "
      class="mx-[-8px]"
    >
      <q-tab
        v-for="item in seasons"
        :key="item.value"
        :name="item.value"
        :label="item.name"
        class="bg-[#2a2a2a] mx-1 rounded-sm !min-h-0 py-[6px]"
        content-class="children:!font-normal children:!text-[13px] children:!min-h-0"
        :ref="
          (el: QTab) =>
            void (item.value === seasonActive && (tabsRef = el as QTab))
        "
      />
    </q-tabs>

    <div class="px-1 mx-[-8px]">
      <GridCard v-if="data" v-show="!loading" :items="data.toPut ?? []" />
    </div>

    <!-- comment embed -->
    <div class="mt-5 flex items-center justify-between flex-nowrap">
      <span class="text-subtitle1 text-[#eee]">{{ t("binh-luan") }}</span>
      <q-toggle
        v-model="settingsStore.ui.commentAnime"
        color="main"
        size="sm"
      />
    </div>
    <EmbedFbCmt
      v-if="settingsStore.ui.commentAnime"
      :href="`http://animevietsub.tv/phim/-${seasonId}/`"
      :lang="locale?.replace('-', '_')"
      class="bg-gray-400 rounded-xl mt-3 overflow-hidden"
    />
  </div>

  <ScreenError v-else :error="error" @click:retry="resetErrorAndRun()" />

  <!-- bottom sheet -->
  <q-dialog
    v-if="data"
    position="bottom"
    class="children:!px-0"
    full-width
    v-model="showDialogChapter"
  >
    <q-card
      style="height: calc(100vh - 100vw * 9 / 16)"
      class="!overflow-visible flex column flex-nowrap py-0 px-4"
    >
      <div class="flex items-center justify-between text-subtitle1 py-2">
        {{ t("season") }}
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
                  'iconoir:cloud-error',
                ][stateStorageStore.disableAutoRestoration]
              "
              width="20"
              height="20"
            />
          </q-btn>
          <q-btn dense flat round icon="close" v-close-popup />
        </div>
      </div>
      <div class="relative flex-1 min-h-0 mx-[-24px]">
        <q-tabs
          v-model="seasonActive"
          no-caps
          dense
          inline-label
          active-class="c--main"
          v-if="
            seasons &&
            (seasons.length > 1 ||
              (seasons.length === 0 && seasons[0].name !== ''))
          "
        >
          <q-tab
            v-for="item in seasons"
            :key="item.value"
            :name="item.value"
            :label="item.name"
            :ref="
              (el: QTab) =>
                void (
                  item.value === seasonActive && (tabsDialogRef = el as QTab)
                )
            "
          />
        </q-tabs>

        <q-tab-panels v-model="seasonActive" animated keep-alive class="h-full">
          <q-tab-panel
            v-for="({ value }, index) in seasons"
            :key="index"
            :name="value"
            class="flex justify-around place-items-center place-content-start"
          >
            <div
              v-if="_cacheDataSeasons.get(value)?.status === 'pending'"
              class="absolute top-[50%] left-[50%] transform -translate-x-1/2 -translate-y-1/2"
            >
              <q-spinner style="color: #00be06" size="3em" :thickness="3" />
            </div>
            <div
              v-else-if="_cacheDataSeasons.get(value)?.status === 'error'"
              class="absolute top-[50%] left-[50%] text-center transform -translate-x-1/2 -translate-y-1/2"
            >
              {{ t("loi-khi-lay-du-lieu") }}
              <br />
              <q-btn
                rounded
                dense
                no-caps
                style="color: #00be06"
                @click="fetchSeason(value)"
                >{{ t("thu-lai") }}</q-btn
              >
            </div>

            <ChapsGridQBtn
              v-else
              grid
              :chaps="
                (
                  _cacheDataSeasons.get(value) as
                    | ResponseDataSeasonSuccess
                    | undefined
                )?.response.chaps
              "
              :season="value"
              :find="
                (item) => value === currentSeason && item.id === currentChap
              "
              :progressChaps="
                (progressWatchStore.get(value) as unknown as any)?.response
              "
              class-item="px-4 py-[10px] mx-2 mb-3"
            />
          </q-tab-panel>
        </q-tab-panels>
      </div>
    </q-card>
  </q-dialog>

  <!-- dialog informa -->
  <q-dialog
    v-if="data"
    position="bottom"
    full-width
    class="children:!px-0"
    v-model="showDialogInforma"
  >
    <q-card
      style="height: calc(100vh - 100vw * 9 / 16)"
      class="!overflow-visible flex column flex-nowrap py-0"
    >
      <div class="flex items-center justify-between text-subtitle1 px-2 py-2">
        {{ t("chi-tiet") }}
        <q-btn dense flat round icon="close" v-close-popup />
      </div>
      <q-card-section
        class="relative flex-1 min-h-0 px-4 overflow-y-scroll text-[14px] text-[#9a9a9a] text-weight-normal"
      >
        <div>
          <div class="flex flex-nowrap">
            <q-img-custom
              :src="forceHttp2(currentDataSeason?.image ?? data.image!)"
              referrerpolicy="no-referrer"
              no-spinner
              :ratio="280 / 400"
              width="220px"
              class="rounded-lg"
            />

            <div class="pl-2 py-3 min-w-0">
              <div class="text-[16px] line-clamp-2 text-[#eee] leading-snug">
                {{ data.name }}
              </div>
              <div class="mt-4">
                {{ data.language }}
                <span class="mx-1">|</span>
                {{ data.contries[0]?.name ?? "unknown" }}
              </div>

              <div class="mt-2">
                {{ t("phat-hanh-nam-data-yearof", [data.yearOf]) }}
              </div>

              <div class="mt-2">
                {{ t("tap-data-duration-da-cap-nhat", [data.duration]) }}
              </div>
            </div>
          </div>

          <ul class="mt-8">
            <li>
              <span>{{ t("ten-khac") }} </span>

              <span class="text-[#eee] leading-relaxed">{{
                data.othername
              }}</span>
            </li>
            <li class="mt-3">
              <span>{{ t("loai") }} </span>

              <span class="text-[#eee]">
                <q-btn
                  flat
                  dense
                  no-caps
                  v-for="item in data.genre"
                  :key="item.name"
                  class="py-[5px] !min-h-0 px-2 rounded-sm bg-gray-700 mx-1 my-1 inline-block relative"
                  :to="item.path"
                >
                  {{ item.name }}
                </q-btn>
              </span>
            </li>
          </ul>

          <div class="mt-5 text-[#eee] text-[16px]">{{ t("gioi-thieu") }}</div>
          <p
            class="mt-3 leading-loose whitespace-pre-wrap"
            v-html="data.description"
          />

          <template v-if="data.trailer">
            <div class="mt-5 text-[#eee] text-[16px]">{{ t("trailer") }}</div>
            <q-video class="mt-3" :src="data.trailer!" :ratio="16 / 9" />
          </template>
        </div>
      </q-card-section>
    </q-card>
  </q-dialog>

  <!-- need component ScreenError checker error -->

  <AddToPlaylist
    v-model="showDialogAddToPlaylist"
    :exists="
      (id) =>
        currentSeason
          ? playlistStore.hasAnimeOfPlaylist(id, currentSeason)
          : false
    "
    @action:add="addAnimePlaylist"
    @action:del="removeAnimePlaylist"
    @after-create-playlist="addAnimePlaylist"
  />
</template>

<script lang="ts" setup>
import { Share } from "@capacitor/share"
import { Icon } from "@iconify/vue"
import { computedAsync } from "@vueuse/core"
import { useHead } from "@vueuse/head"
import AddToPlaylist from "components/AddToPlaylist.vue"
import BrtPlayer from "components/BrtPlayer.vue"
import ChapsGridQBtn from "components/ChapsGridQBtn.vue"
import GridCard from "components/GridCard.vue"
import QImgCustom from "components/QImgCustom"
import Quality from "components/Quality.vue"
import ScreenError from "components/ScreenError.vue"
import SkeletonGridCard from "components/SkeletonGridCard.vue"
import Star from "components/Star.vue"
import MessageScheludeChap from "components/feat/MessageScheludeChap.vue"
import { EmbedFbCmt } from "embed-fbcmt-client/vue"
import { get, set } from "idb-keyval"
import {
  QBtn,
  QCard,
  QCardSection,
  QDialog,
  QIcon,
  QResponsive,
  QSkeleton,
  QSpinner,
  QSpinnerInfinity,
  QTab,
  QTabPanel,
  QTabPanels,
  QTabs,
  QVideo,
  useQuasar,
} from "quasar"
import { AjaxLike, checkIsLile } from "src/apis/runs/ajax/like"
import { PlayerFB } from "src/apis/runs/ajax/player-fb"
import { PlayerLink } from "src/apis/runs/ajax/player-link"
import { PhimId } from "src/apis/runs/phim/[id]"
import { PhimIdChap } from "src/apis/runs/phim/[id]/[chap]"
// import BottomSheet from "src/components/BottomSheet.vue"
import { logEvent } from "src/boot/firebase"
import type { servers } from "src/constants"
import {
  API_OPEND,
  C_URL,
  isNative,
  TIMEOUT_GET_LAST_EP_VIEWING_IN_STORE,
  WARN,
} from "src/constants"
import { scrollXIntoView } from "src/helpers/scrollIntoView"
import { forceHttp2 } from "src/logic/forceHttp2"
import { formatView } from "src/logic/formatView"
import { getQualityByLabel } from "src/logic/get-quality-by-label"
import { getRealSeasonId } from "src/logic/getRealSeasonId"
import { parseChapName } from "src/logic/parseChapName"
import { unflat } from "src/logic/unflat"
import { useAuthStore } from "stores/auth"
import { useHistoryStore } from "stores/history"
import { usePlaylistStore } from "stores/playlist"
import { useSettingsStore } from "stores/settings"
import { useStateStorageStore } from "stores/state"
import type { Ref, ShallowReactive, ShallowRef } from "vue"
import {
  computed,
  getCurrentInstance,
  onBeforeUnmount,
  reactive,
  ref,
  shallowReactive,
  shallowRef,
  toRaw,
  watch,
  watchEffect,
} from "vue"
import { useI18n } from "vue-i18n"
import { useRequest } from "vue-request"
import { RouterLink, useRoute, useRouter } from "vue-router"

import type {
  ProgressWatchStore,
  ResponseDataSeasonError,
  ResponseDataSeasonPending,
  ResponseDataSeasonSuccess,
  Season,
} from "./_season.interface"

// ================ follow ================
// =======================================================
// import SwipableBottom from "components/SwipableBottom.vue"

// ============================================

const route = useRoute()
const router = useRouter()
const instance = getCurrentInstance()
const historyStore = useHistoryStore()
const settingsStore = useSettingsStore()
const playlistStore = usePlaylistStore()
const stateStorageStore = useStateStorageStore()
const authStore = useAuthStore()
const $q = useQuasar()

const { t, locale } = useI18n()

const currentSeason = computed(() => route.params.season as string)
const currentMetaSeason = computed(() => {
  return seasons.value?.find((item) => item.value === currentSeason.value)
})
const realIdCurrentSeason = computed(() => {
  if (!currentSeason.value) return

  return getRealSeasonId(currentSeason.value)
})

const { data, run, error, loading } = useRequest(
  async () => {
    // const { }
    const id = realIdCurrentSeason.value

    if (!id) return Promise.reject()

    // eslint-disable-next-line functional/no-let
    let result: Ref<Awaited<ReturnType<typeof PhimId>>>

    await Promise.any([
      get(`data-${id}`).then((text: string) => {
        // eslint-disable-next-line functional/no-throw-statement
        if (!text) throw new Error("not_found")
        console.log("[fs]: use cache from fs %s", id)
        // eslint-disable-next-line promise/always-return
        if (!result) result = ref(JSON.parse(text))
      }),
      PhimId(realIdCurrentSeason.value)
        .then(async (data) => {
          // eslint-disable-next-line functional/no-let
          let changed = !result // true if result is undefined
          const watcher =
            result &&
            watch(
              result,
              () => {
                watcher()
                changed = true
              },
              { deep: true },
            )
          if (result) Object.assign(result.value, data)
          else result = ref(data)
          watcher?.()

          Object.assign(result.value, { __ONLINE__: true })

          // eslint-disable-next-line promise/always-return
          if (changed) {
            set(`data-${id}`, JSON.stringify(data))
              // eslint-disable-next-line promise/no-nesting
              .then(() => {
                return console.log("[fs]: save cache to fs %s", id)
              })
              // eslint-disable-next-line promise/no-nesting, @typescript-eslint/no-empty-function
              .catch(() => {})
          } else if (import.meta.env.DEV) {
            console.log("No update data in IndexedDB")
          }
        })
        .catch((err) => {
          error.value = err as Error
          console.error(err)
          // eslint-disable-next-line functional/no-throw-statement
          throw err
        }),
    ])

    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    return result!.value
  },
  {
    refreshDeps: [realIdCurrentSeason],
    refreshDepsAction() {
      // data.value = undefined
      if (!realIdCurrentSeason.value) return
      run()
    },
  },
)
function resetErrorAndRun() {
  error.value = undefined
  run()
}

const seasons = shallowRef<Season[]>()
watch(
  data,
  () => {
    if (!data.value) {
      seasons.value = undefined

      return
    }

    console.log("data refreshed")

    const season = data.value.season ?? []

    if (season.length > 0) {
      seasons.value = season.map((item) => {
        return {
          name: item.name,
          value: router.resolve(item.path).params.season as string,
        }
      })
      return
    }

    seasons.value = [
      {
        name: "",
        value: currentSeason.value,
      },
    ]
  },
  {
    immediate: true,
  },
)

const _cacheDataSeasons = reactive<
  Map<
    string,
    | ResponseDataSeasonPending
    | ResponseDataSeasonSuccess
    | ResponseDataSeasonError
  >
>(new Map())
const progressWatchStore = reactive<ProgressWatchStore>(new Map())

const responseOnlineStore = new WeakSet<
  ShallowRef<Awaited<ReturnType<typeof PhimIdChap>> | undefined>
>()
async function fetchSeason(season: string) {
  // if (!seasons.value) {
  //   console.warn("seasons not ready")
  //   return
  // }

  if (!progressWatchStore.has(season))
    progressWatchStore.set(season, { status: "queue" })
  else console.log(">> progress %s exists", season)

  if (_cacheDataSeasons.get(season)?.status === "success") {
    console.info("use data from cache not fetch")
    return
  }

  _cacheDataSeasons.set(season, {
    status: "pending",
  })
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const currentDataSeason = _cacheDataSeasons.get(season)!
  try {
    console.log("fetch chaps on season")

    const realIdSeason = getRealSeasonId(season)

    const response = shallowRef<Awaited<ReturnType<typeof PhimIdChap>>>()
    // eslint-disable-next-line functional/no-let
    let promiseLoadIndexedb: Promise<string | undefined> =
      Promise.resolve(undefined)
    await Promise.any([
      PhimIdChap(realIdSeason).then((data) => {
        // mergeListEp(response.value, data)
        const json = JSON.stringify(data)
        // eslint-disable-next-line promise/always-return
        if (
          !response.value ||
          response.value.chaps.length !== data.chaps.length ||
          json !== JSON.stringify(toRaw(response.value))
        ) {
          // eslint-disable-next-line promise/catch-or-return
          promiseLoadIndexedb.finally((jsonCache?: string) => {
            if (json !== jsonCache) {
              const task = set(`season_data ${realIdSeason}`, json)
              if (import.meta.env.DEV)
                task
                  // eslint-disable-next-line promise/no-nesting, promise/always-return
                  .then(() => {
                    console.log("[fs]: save cache season %s", realIdSeason)
                  })
                  // eslint-disable-next-line promise/no-nesting
                  .catch((err) =>
                    console.warn(
                      "[fs]: failure save cache season %s",
                      realIdSeason,
                      err,
                    ),
                  )
            } else if (import.meta.env.DEV) {
              console.log("[data season]: No update response in IndexedDB")
            }
          })
          console.log("[online]: use data from internet")
          response.value = data
          console.log("data from internet is ", data)
        }
        responseOnlineStore.add(response)
      }),
      (promiseLoadIndexedb = get(`season_data ${realIdSeason}`).then(
        (json?: string) => {
          // eslint-disable-next-line functional/no-throw-statement
          if (!json) throw new Error("not_found")
          console.log("[fs]: use cache %s", realIdSeason)
          if (!response.value) response.value = JSON.parse(json)

          return json
        },
      )),
    ])

    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    if (response.value!.chaps.length === 0) {
      console.warn("chaps not found")
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      response.value!.chaps = [
        {
          id: "0",
          play: "1",

          hash:
            data.value?.trailer ?? "https://www.youtube.com/embed/qUmMH_TGLS8",
          name: t("trailer"),
        },
      ]
    } else if (response.value && response.value.chaps.length > 50) {
      console.log("large chap. spliting...")

      // eslint-disable-next-line no-inner-declarations
      function watchHandler() {
        if (!seasons.value || !response.value) return

        // eslint-disable-next-line functional/no-let
        let indexMetaSeason = seasons.value.findIndex(
          (item) => item.value === season,
        )

        if (indexMetaSeason === -1)
          indexMetaSeason = seasons.value.findIndex(
            (item) => item.value === realIdSeason,
          )

        console.log("index %s = %i", season, indexMetaSeason)

        const nameSeason = seasons.value[indexMetaSeason].name

        const seasonsSplited: Season[] = []
        const { chaps } = response.value
        unflat(chaps, 50).forEach((chapsSplited, index) => {
          const value = index === 0 ? realIdSeason : `${realIdSeason}$${index}`
          const name = `${nameSeason} (${chapsSplited[0].name} - ${
            chapsSplited[chapsSplited.length - 1].name
          })`

          console.log("set %s by %s", value, chapsSplited[0].id)

          const dataOnCache = _cacheDataSeasons.get(value)
          const newData: ResponseDataSeasonSuccess = {
            status: "success",
            response: {
              // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
              ...response.value!,
              chaps: chapsSplited,
              ssSibs: seasonsSplited,
            },
          }
          if (dataOnCache) {
            Object.assign(dataOnCache, newData)
          } else {
            _cacheDataSeasons.set(value, newData)
          }

          seasonsSplited.push({
            name,
            value,
          })
        })
        const newSeasons = [
          ...seasons.value.slice(0, indexMetaSeason),
          ...seasonsSplited,
          ...seasons.value.slice(indexMetaSeason + 1),
        ]
        console.log("current seasons: ", seasons.value)
        seasons.value = newSeasons
        console.log("new seasons: ", newSeasons)
        console.log("set seasons: ", seasons.value)

        return responseOnlineStore.has(response)
      }

      // eslint-disable-next-line functional/no-let
      let watcherResponse: (() => void) | undefined = watch(response, () => {
        const doneAll = watchHandler()
        if (doneAll) {
          watcherResponse?.()
          watcherResponse = undefined
        }
      })
      // eslint-disable-next-line functional/no-let
      let watcherSeasons: (() => void) | undefined
      watcherSeasons = watch(
        () => typeof seasons.value !== "undefined",
        (seasonsExists) => {
          if (!seasonsExists) return

          const doneAll = watchHandler()
          if (doneAll) {
            watcherResponse?.()
            watcherResponse = undefined
          }

          if (watcherSeasons) watcherSeasons()
          else {
            // eslint-disable-next-line promise/catch-or-return
            Promise.resolve().then(() => {
              // eslint-disable-next-line promise/always-return
              watcherSeasons?.()
              watcherSeasons = undefined
            })
          }
        },
        { immediate: true },
      )

      onBeforeUnmount(() => {
        watcherSeasons?.()
        watcherResponse?.()
      }, instance)
      return
    }

    Object.assign(currentDataSeason, {
      status: "success",
      response,
    })

    console.log(_cacheDataSeasons)
  } catch (err) {
    console.warn(err)
    error.value = err as Error
    Object.assign(currentDataSeason, {
      status: "error",
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      response: err as any,
    })
  }
}

const seasonActive = ref<string>()
// sync data by active route
watch(currentSeason, (val) => (seasonActive.value = val), {
  immediate: true,
})

watch(seasonActive, (seasonActive) => {
  if (!seasonActive) return

  // download data season active
  fetchSeason(seasonActive)
})

const currentDataSeason = computed(() => {
  const inCache = _cacheDataSeasons.get(currentSeason.value)

  if (inCache?.status === "success") return inCache.response

  return undefined
})
const currentProgresWatch = computed(() => {
  const inCache = progressWatchStore.get(currentSeason.value)

  if (inCache?.status === "success") return inCache.response

  return undefined
})

// eslint-disable-next-line functional/no-let
let watcherChangeIdFirstEp: (() => void) | null = null
onBeforeUnmount(() => watcherChangeIdFirstEp?.())
/** @type - currentChap is episode id */
const currentChap = ref<string>()
watchEffect(async (onCleanup): Promise<void> => {
  watcherChangeIdFirstEp?.()
  if (!currentSeason.value) return
  if (route.params.chap) {
    currentChap.value = route.params.chap as string
    return
  }
  // if this does not exist make sure the status has not finished loading, this function call also useless

  // if not login -> return first episode in season
  if (!authStore.uid) {
    currentChap.value = currentDataSeason.value?.chaps[0].id
    return
  }
  currentChap.value = undefined
  const episodeId = await Promise.race([
    // if logged -> get last episode viewing in season
    historyStore
      .getLastEpOfSeason(currentSeason.value)
      .catch((err) => {
        console.warn(err)
        return null
      })
      .then((res) => {
        console.log("usage last ep of season", res)
        return res
      }),
    new Promise<null | undefined>((resolve) => {
      const timeout = setTimeout(
        () => resolve(null),
        TIMEOUT_GET_LAST_EP_VIEWING_IN_STORE,
      )

      onCleanup(() => {
        resolve(undefined)
        clearTimeout(timeout)
      })
    }),
  ])

  if (episodeId !== undefined) {
    if (episodeId) {
      // eslint-disable-next-line functional/no-let
      let watcher: () => void
      // eslint-disable-next-line prefer-const
      watcher = watchEffect(() => {
        if (
          currentDataSeason.value?.chaps.some((item) => item.id === episodeId)
        ) {
          currentChap.value = episodeId
          if (typeof watcher === "undefined") setTimeout(watcher)
          else watcher()
        }
      })
    } else {
      watcherChangeIdFirstEp = watch(
        () => currentDataSeason.value?.chaps[0].id,
        (idFirstEp) => {
          currentChap.value = idFirstEp
        },
        { immediate: true },
      )
    }
  }
})
const currentMetaChap = computed(() => {
  if (!currentChap.value) return
  return currentDataSeason.value?.chaps.find(
    (item) => item.id === currentChap.value,
  )
})

watch(
  currentSeason,
  (_, __, onCleanup) => {
    // replace router if last episode viewing exists
    const watcherRestoreLastEp = watchEffect(() => {
      const episodeIdFirst = currentDataSeason.value?.chaps[0].id

      if (
        !route.params.chap &&
        currentChap.value &&
        currentChap.value !== episodeIdFirst &&
        currentMetaChap.value
      ) {
        const correctChapName = parseChapName(currentMetaChap.value.name)

        if (import.meta.env.DEV)
          console.log("%c Redirect to suspend path", "color: green")
        router.replace({
          path: `/phim/${route.params.season}/${correctChapName}-${currentChap.value}`,
          query: route.query,
          hash: route.hash,
        })
        watcherRestoreLastEp()
      }
    })
    onCleanup(watcherRestoreLastEp)
  },
  { immediate: true },
)
watchEffect(() => {
  // currentChap != undefined because is load done from firestore and ready show but in chaps not found (!currentMetaChap.value)
  if (!currentDataSeason.value) return
  if (!currentChap.value) return

  if (!currentMetaChap.value) {
    const epId = currentChap.value

    // search on all season siblings (season splited with `$`)
    const seasonAccuracy = currentDataSeason.value.ssSibs?.find((season) => {
      const cache = _cacheDataSeasons.get(season.value)

      if (cache?.status !== "success") return false

      if (cache.response.chaps.some((item) => item.id === epId)) {
        return true
      }

      return false
    })

    if (seasonAccuracy) {
      if (import.meta.env.DEV)
        console.log("Redirect to season %s", seasonAccuracy.value)
      router.replace({
        name: "watch-anime",
        params: {
          ...route.params,
          season: seasonAccuracy.value,
        },
        query: route.query,
        hash: route.hash,
      })
    } else {
      if (import.meta.env.DEV) console.warn("Redirect to not_found")
      if (data.value && "__ONLINE__" in data.value)
        router.replace({
          name: "not_found",
          params: {
            catchAll: route.path.split("/").slice(1),
          },
          query: route.query,
          hash: route.hash,
        })
    }
  }
})
// TOOD: check chapName in url is chapName
watchEffect(() => {
  const metaEp = currentMetaChap.value
  if (!metaEp) return

  const epId = metaEp.id
  if (route.params.chap !== epId) return
  const correctChapName = parseChapName(metaEp.name)
  const urlChapName = route.params.chapName

  if (urlChapName) {
    // check is valid if not valid redirect
    if (correctChapName === urlChapName) return

    console.warn(
      `Redirect chapName wrong current: "${urlChapName}" not equal real: ${correctChapName}.\nAuto edit url to chapName correct`,
    )
    router.replace({
      path: `/phim/${route.params.season}/${correctChapName}-${epId}`,
      query: route.query,
      hash: route.hash,
    })
  } else {
    // old type url /phim/:season/:chap
    // replace
    console.info("Redirect this url old type redirect to new type url")
    router.replace({
      path: `/phim/${route.params.season}/${correctChapName}-${epId}`,
      query: route.query,
      hash: route.hash,
    })
  }
})
if (!isNative)
  useHead(
    computed(() => {
      if (!data.value) return {}

      const title = currentMetaChap.value
        ? t("tap-_chap-_name-_othername", [
            currentMetaChap.value.name,
            data.value.name,
            data.value.othername,
          ])
        : t("_name-_othername", [data.value.name, data.value.othername])
      const description = data.value.description

      return {
        title,
        description,
        meta: [
          { property: "og:title", content: title },
          { property: "og:description", content: description },
          {
            property: "og:image",
            content: currentDataSeason.value?.poster ?? data.value.poster,
          },
          {
            property: "og:url",
            content: `${process.env.APP_URL}phim/${realIdCurrentSeason.value}`,
          },
        ],
        link: [
          {
            rel: "canonical",
            href: `${process.env.APP_URL}phim/${realIdCurrentSeason.value}`,
          },
        ],
      }
    }),
  )

interface SiblingChap {
  season: Exclude<typeof seasons.value, undefined>[0]
  chap?: Exclude<typeof currentDataSeason.value, undefined>["chaps"][0]
}
// eslint-disable-next-line vue/return-in-computed-property
const nextChap = computed((): SiblingChap | undefined => {
  if (!currentDataSeason.value) return
  // get index currentChap
  const indexCurrentChap = !currentMetaChap.value
    ? -1
    : currentDataSeason.value.chaps.indexOf(currentMetaChap.value)
  if (indexCurrentChap === -1) {
    console.warn("current index not found %i", indexCurrentChap)
    return
  }

  const isLastChapOfSeason =
    indexCurrentChap === currentDataSeason.value.chaps.length - 1
  if (!isLastChapOfSeason) {
    if (!currentMetaSeason.value) return
    return {
      season: currentMetaSeason.value,
      chap: currentDataSeason.value.chaps[indexCurrentChap + 1],
    }
  }

  if (!seasons.value) return
  // if current last chap of season
  // check season of last
  const indexSeason = !currentMetaSeason.value
    ? -1
    : seasons.value.indexOf(currentMetaSeason.value)
  if (indexSeason === -1) {
    console.warn("current index not found %i", indexSeason)
    return
  }

  const isLastSeason = indexSeason === seasons.value.length - 1
  if (!isLastSeason) {
    // first chap of next season
    return {
      season: seasons.value[indexSeason + 1],
    }
  }

  console.info("[[===THE END===]]")
})
// eslint-disable-next-line vue/return-in-computed-property
const prevChap = computed((): SiblingChap | undefined => {
  if (!currentDataSeason.value) return
  // get index currentChap
  const indexCurrentChap = !currentMetaChap.value
    ? -1
    : currentDataSeason.value.chaps.indexOf(currentMetaChap.value)
  if (indexCurrentChap === -1) {
    console.warn("current index not found %i", indexCurrentChap)
    return
  }

  const isFirstChapOfSeason = indexCurrentChap === 0
  if (!isFirstChapOfSeason) {
    if (!currentMetaSeason.value) return
    return {
      season: currentMetaSeason.value,
      chap: currentDataSeason.value.chaps[indexCurrentChap - 1],
    }
  }

  if (!seasons.value) return
  // if current last chap of season
  // check season of last
  const indexSeason = !currentMetaSeason.value
    ? -1
    : seasons.value.indexOf(currentMetaSeason.value)
  if (indexSeason === -1) {
    console.warn("current index not found %i", indexSeason)
    return
  }

  const isFirstSeason = indexSeason === 0
  if (!isFirstSeason) {
    // first chap of next season
    return {
      season: seasons.value[indexSeason - 1],
    }
  }

  console.info("[[===THE END===]]")
})

const configPlayer = shallowRef<Awaited<ReturnType<typeof PlayerLink>>>()
watch(
  currentMetaChap,
  (currentMetaChap, _, onCleanup) => {
    if (!currentMetaChap) return

    if (currentMetaChap.id === "0") {
      configPlayer.value = {
        link: [
          {
            file: currentMetaChap.hash,
            label: "HD",
            qualityCode: getQualityByLabel("HD"),
            preload: "auto",
            type: "youtube",
          },
        ],
        playTech: "trailer",
      }

      return
    }

    configPlayer.value = undefined

    // eslint-disable-next-line functional/no-let
    let typeCurrentConfig: keyof typeof servers | null = null
    // eslint-disable-next-line functional/no-let
    let loadedServerFB = false
    // setup watcher it
    const watcher = watch(
      () => settingsStore.player.server,
      async (server) => {
        loadedServerFB = false
        try {
          if (server === "DU") {
            if (typeCurrentConfig !== "DU")
              PlayerLink(currentMetaChap)
                .then((conf) => {
                  // eslint-disable-next-line promise/always-return
                  if (settingsStore.player.server === "DU") {
                    configPlayer.value = conf
                    typeCurrentConfig = "DU"
                  }
                })
                .catch((err) => {
                  error.value = err
                })
          }
          if (server === "FB") {
            // PlayerFB は常に PlayerLink よりも遅いため、DU を使用して高速プリロード戦術を使用する必要があります。
            if (typeCurrentConfig !== "DU")
              PlayerLink(currentMetaChap)
                .then((conf) => {
                  // eslint-disable-next-line promise/always-return
                  if (!loadedServerFB && settingsStore.player.server === "DU") {
                    configPlayer.value = conf
                    typeCurrentConfig = "DU"
                  }
                })
                .catch((err) => {
                  error.value = err
                })
            PlayerFB(currentMetaChap.id)
              .then((conf) => {
                // eslint-disable-next-line promise/always-return
                if (settingsStore.player.server === "FB") {
                  configPlayer.value = conf
                  typeCurrentConfig = "FB"
                }
                loadedServerFB = true
              })
              .catch((err) => {
                error.value = err
              })
          }
        } catch (err) {
          $q.notify({
            position: "bottom-right",
            message: (err as Error).message,
          })
          console.error(err)
        }
      },
      { immediate: true },
    )
    onCleanup(watcher)
  },
  {
    immediate: true,
  },
)
const sources = computed(() => configPlayer.value?.link)

watch(
  [progressWatchStore, () => authStore.user_data],
  // eslint-disable-next-line camelcase
  ([progressWatchStore, user_data]) => {
    // eslint-disable-next-line camelcase
    if (!user_data) return

    // help me
    progressWatchStore.forEach(async (item, season) => {
      if (item.status && item.status !== "error" && item.status !== "queue")
        return // "pending" or "success"

      Object.assign(item, {
        status: "pending",
      })
      try {
        console.log("%c fetch progress view", "color: blue")
        Object.assign(item, {
          status: "success",
          response: await getProgressChaps(season),
        })
      } catch (err) {
        Object.assign(item, {
          status: "error",
          error: err as Error,
        })
      }
    })
  },
)

async function getProgressChaps(
  currentSeason: string,
): Promise<Map<string, { cur: number; dur: number }> | null> {
  if (!authStore.uid) return null

  const progressChaps = new Map()

  try {
    const docs = await historyStore.getProgressChaps(currentSeason)
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    docs.forEach((item: any) => {
      const { cur, dur } = item.data()
      progressChaps.set(item.id, {
        cur,
        dur,
      })
    })
  } catch (err) {
    $q.notify({
      position: "bottom-right",
      message: (err as Error).message,
    })
  }

  return progressChaps
}

// @scrollIntoView
const tabsRef = ref<QTab>()
watchEffect(() => {
  if (!tabsRef.value) return
  if (!currentSeason.value) return

  setTimeout(() => {
    console.log("scroll now")
    if (tabsRef.value?.$el) scrollXIntoView(tabsRef.value.$el)
  }, 70)
})
const tabsDialogRef = ref<QTab>()
watchEffect(() => {
  if (!tabsDialogRef.value) return
  if (!currentSeason.value) return

  setTimeout(() => {
    console.log("scroll now")
    if (tabsDialogRef.value?.$el) scrollXIntoView(tabsDialogRef.value.$el)
  }, 70)
})
const followed = ref(false)
const follows = ref(0)

const seasonId = computed(() => realIdCurrentSeason.value?.match(/\d+$/)?.[0])
watch(
  seasonId,
  async (seasonId) => {
    if (!authStore.isLogged) {
      console.warn("can't get is like because not login")
      return
    }

    if (seasonId) {
      followed.value = await checkIsLile(seasonId)
    } else {
      followed.value = false
      follows.value = 0
    }
  },
  {
    immediate: true,
  },
)
// Analytics
watch(
  [seasonId, currentMetaSeason, currentMetaChap, () => data.value?.name],
  async ([seasonId, currentMetaSeason, currentMetaChap, name]) => {
    if (!currentMetaSeason) return
    if (!currentMetaChap) return
    if (!name) return

    logEvent("watching", {
      value: `${name} - ${currentMetaSeason.name} Tập ${currentMetaChap.name}(${seasonId}/${currentMetaChap.id})`,
    })
  },
  { immediate: true },
)

watch(
  data,
  (data) => {
    follows.value = data?.follows ?? 0
  },
  {
    immediate: true,
  },
)

async function toggleFollow() {
  if (!authStore.isLogged) {
    $q.notify({
      position: "bottom-right",
      message: t("hay-dang-nhap-truoc-de-theo-doi"),
    })
    return
  }

  followed.value = !followed.value
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  await AjaxLike(seasonId.value!, followed.value)
  if (followed.value) follows.value++
  else follows.value--
  $q.notify({
    position: "bottom-right",
    message: followed.value
      ? t("da-them-vao-danh-sach-theo-doi")
      : t("da-xoa-khoi-danh-sach-theo-doi"),
  })
}

function share() {
  if (!data.value || !currentMetaSeason.value || !currentMetaChap.value) {
    console.warn("data not ready")

    return
  }
  Share.share({
    title: `Xem ${data.value.name} series ${currentMetaSeason.value.name}`,
    text: `Xem ${data.value.name} tập ${currentMetaChap.value.name}`,
    url: C_URL + route.path,
    dialogTitle: t("chia-se") + " " + data.value.name,
  })
}
// ================ status ================
const showDialogChapter = ref(false)
const showDialogInforma = ref(false)

// =========== playlist ===========
const showDialogAddToPlaylist = ref(false)
function clickShowPlaylist() {
  if (!authStore.isLogged) {
    $q.notify({
      position: "bottom-right",
      message: "Hãy đăng nhập trước để sử dụng danh sách phát",
    })
    return
  }

  showDialogAddToPlaylist.value = true
}
async function addAnimePlaylist(idPlaylist: string) {
  const { value: metaSeason } = currentMetaSeason
  if (!metaSeason) return
  if (!currentDataSeason.value || !data.value) return
  if (!currentSeason.value) return
  if (!currentChap.value) return
  if (!currentMetaChap.value) return
  try {
    await playlistStore.addAnimeToPlaylist(idPlaylist, currentSeason.value, {
      name: data.value.name,
      poster: currentDataSeason.value?.poster ?? data.value.poster,
      nameSeason: metaSeason.name,
      chap: currentChap.value,
      nameChap: currentMetaChap.value.name,
    })
    $q.notify({
      position: "bottom-right",
      message: t("da-theo-vao-danh-sach-phat"),
    })
  } catch (err) {
    $q.notify({
      position: "bottom-right",
      message: (err as Error).message,
    })
  }
}
async function removeAnimePlaylist(idPlaylist: string) {
  if (!currentSeason.value) return
  try {
    await playlistStore.deleteAnimeFromPlaylist(idPlaylist, currentSeason.value)
    $q.notify({
      position: "bottom-right",
      message: t("da-xoa-khoi-danh-sach-phat"),
    })
  } catch (err) {
    $q.notify({
      position: "bottom-right",
      message: (err as Error).message,
    })
  }
}

// ================ skip op/end ================
interface ListEpisodes {
  poster: string
  progress: {
    current: string
    total: string
  }
  name: string
  jName?: string
  id: string
  list: {
    id: string
    order: string
    name: string
    title?: string
  }[]
}
// eslint-disable-next-line functional/no-let
let episodesOpEndInited = false
const episodesOpEnd = computedAsync<ShallowReactive<ListEpisodes> | null>(
  async (onCleanup) => {
    if (episodesOpEndInited) episodesOpEnd.value = null
    else episodesOpEndInited = true

    const name = data.value?.name
    const othername = data.value?.othername

    if (!name && !othername) return null

    const realId = realIdCurrentSeason.value

    const controller = new AbortController()
    onCleanup(() => controller.abort())

    let results: ShallowReactive<ListEpisodes>
    await Promise.any([
      fetch(`${API_OPEND}/list-episodes?name=${name + " " + othername}`, {
        signal: controller.signal,
      })
        .then((res) => res.json())
        .then((data) => {
          if (data.progress.current === data.progress.total) {
            // ok backup data now
            void set(`episodes_opend:${realId}`, JSON.stringify(data))
          }

          // eslint-disable-next-line promise/always-return
          if (results) {
            if (JSON.stringify(toRaw(results)) !== JSON.stringify(data))
              Object.assign(results, data)
          } else results = shallowReactive(data)
        }),
      getDataIDB<string>(`episodes_opend:${realId}`).then((text) => {
        if (!text) throw new Error("not_found_on_idb")

        const data = JSON.parse(text)

        // eslint-disable-next-line promise/always-return
        if (results) {
          if (JSON.stringify(toRaw(results)) !== text)
            Object.assign(results, data)
        } else results = shallowReactive(data)
      }),
    ])

    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    return results!
  },
  null,
  {
    onError: WARN,
  },
)
const episodeOpEnd = computed(() => {
  // find episode on episodesOpEnd
  if (!episodesOpEnd.value) return

  const epName = currentMetaChap.value?.name.trim().replace(/^\w+0+/, "")

  if (!epName) return

  const { list } = episodesOpEnd.value

  const epFloat = parseFloat(epName)
  const episode =
    list.find((item) => {
      if (item.name === epName) return true

      return parseFloat(item.name) === epFloat
    }) ??
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    list[currentDataSeason.value?.chaps.indexOf(currentMetaChap.value!) ?? -1]

  return episode
  // currentMetaChap.name // format is 01...
})

interface InOutroEpisode {
  sources: string
  tracks: {
    file: string
    label: string
    kind: "captions"
    default?: true
  }[]
  encrypted: boolean
  intro: {
    start: number
    end: number
  }
  outro: {
    start: number
    end: number
  }
  server: number
}
// eslint-disable-next-line functional/no-let
let inoutroEpisodeInited = false
const inoutroEpisode = computedAsync<ShallowReactive<InOutroEpisode> | null>(
  async () => {
    if (!episodeOpEnd.value) return null

    if (inoutroEpisodeInited) inoutroEpisode.value = null
    else inoutroEpisodeInited = true

    const { id } = episodeOpEnd.value

    let results: ShallowReactive<InOutroEpisode>
    await Promise.any([
      fetch(`${API_OPEND}/episode-skip/${id}`)
        .then((res) => res.json() as Promise<InOutroEpisode>)
        .then((data) => {
          void set(`inoutro:${id}`, data)

          // eslint-disable-next-line promise/always-return
          if (results) {
            if (JSON.stringify(toRaw(results)) !== JSON.stringify(data))
              Object.assign(results, data)
          } else results = shallowReactive(data)
        }),
      getDataIDB<string>(`inoutro:${id}`).then((text) => {
        if (!text) throw new Error("not_found_on_idb")

        const data = JSON.parse(text)

        // eslint-disable-next-line promise/always-return
        if (results) {
          if (JSON.stringify(toRaw(results)) !== text)
            Object.assign(results, data)
        } else results = shallowReactive(data)
      }),
    ])

    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    return results!
  },
  null,
  { onError: WARN },
)
</script>

<style lang="scss" scoped>
.divider {
  width: 1px;
  height: 10px;
  border: none;
  background: rgb(45, 47, 52);
  color: rgb(230, 230, 230);
  margin: 0px 8px;
  display: inline-block;

  @media screen and (max-width: 1023px) {
    margin: 0px 6px;
  }
}

.tags {
  > * {
    @apply mr-3;
  }

  @media (max-width: 767px) {
    font-size: 13px;

    > * {
      @apply mr-1 mt-1;
    }
  }
}

.chap-name {
  // height: 20px;
  text-align: center;
  font-size: 14px;
  color: rgb(130, 131, 135);
  // transition: all 1s ease;
  font-weight: 500;
  @apply mx-2 py-1;

  &.active {
    color: rgb(0, 190, 6);

    &:after {
      width: 80%;
    }
  }
}

.tabs-season-line {
  @apply absolute bottom-0;
  height: 2px;
  border-radius: 1px;
  bottom: 0px;
  margin-top: 8px;
  background: rgb(0, 190, 6);
  display: block;
  transition:
    width 0.22s ease,
    left 0.22s ease;
  transform: translateX(-50%);
  z-index: 12;
}
</style>

<style lang="scss" scoped>
.panels-navigator :deep(.q-panel) {
  overflow-y: hidden;
  @apply scrollbar-hide;
}
</style>
