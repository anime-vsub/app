<template>
  <!-- skeleton first load -->
  <div v-if="!error" class="row mx-4">
    <div
      class="col-9"
      :class="{
        'col-12': settingsStore.ui.modeMovie
      }"
    >
      <!-- <q-responsive
        v-if="!data"
        :ratio="841 / 483"
        class="max-h-[calc(100vh-169px)]"
      >
        <div class="flex items-center justify-center absolute w-full h-full">
          <q-spinner color="main" size="45px" />
        </div>
      </q-responsive> -->
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
        :poster="
          currentDataSeason?.poster
            ? forceHttp2(currentDataSeason.poster)
            : undefined
        "
        :seasons="seasons"
        :_cache-data-seasons="_cacheDataSeasons"
        :fetch-season="fetchSeason"
        :progressWatchStore="progressWatchStore"
        :intro="inoutroEpisode?.intro"
        :outro="inoutroEpisode?.outro"
        @cur-update="
          currentProgresWatch?.set($event.id, {
            cur: $event.cur,
            dur: $event.dur
          })
        "
      />
      <div
        v-else
        class="w-full overflow-hidden bg-[#000] focus-visible:outline-none select-none"
      >
        <q-img-custom
          no-spinner
          v-if="!sources?.[0]?.file"
          :ratio="841 / 483"
          class="max-h-[calc(100vh-169px)] max-w-[100px]"
          src="~assets/ic_question_result_error.png"
          width="100"
        />
        <q-video
          v-else
          :ratio="841 / 483"
          class="max-h-[calc(100vh-169px)]"
          :src="sources[0].file"
        />
      </div>
    </div>
    <div v-if="!settingsStore.ui.modeMovie" class="col-3 relative">
      <div class="absolute w-full h-full flex column flex-nowrap">
        <FragmentChaps
          :fetch-season="fetchSeason"
          :seasons="seasons"
          :_cache-data-seasons="_cacheDataSeasons"
          :current-season="currentSeason"
          :current-chap="currentChap"
          :progressWatchStore="progressWatchStore"
        />
      </div>
    </div>
  </div>

  <div
    v-if="!error && loading && !data"
    class="absolute w-full h-full overflow-hidden px-4 pt-6 text-[28px] row"
  >
    <div class="col-9 pr-4">
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
    </div>
    <div class="col-3">
      <div class="col-3">
        <div class="text-h6 mt-3 text-subtitle1">
          <q-skeleton type="text" width="60%" />
        </div>

        <SkeletonCardVertical v-for="item in 12" :key="item" class="mt-3" />
      </div>
    </div>
  </div>

  <div v-else-if="!error && data" class="mx-4 row">
    <div class="col-9 pr-4">
      <div class="flex-1 mt-4">
        <h1
          class="line-clamp-2 text-weight-medium py-0 my-0 text-[18px] leading-normal"
        >
          {{ data.name }}
        </h1>
      </div>

      <div class="flex justify-between">
        <div>
          <h5 class="text-gray-400 text-weight-normal leading-normal mb-2">
            {{ t("formatview-data-views-luot-xem", [formatView(data.views)]) }}
            <span v-if="currentDataSeason?.update">
              &bull;
              <MessageScheludeChap :update="currentDataSeason.update" />
            </span>
          </h5>

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
            <div class="divider" />
            <span class="text-main cursor-pointer">
              {{ t("toi-muon-danh-gia") }}
              <q-menu class="bg-dark-page">
                <q-card class="bg-transparent">
                  <q-card-section class="flex items-center text-gray-200">
                    <Star :label="pointRate" class="mr-2 text-[16px]" />
                    {{ t("voi") }}
                    {{ t("_rate-nguoi-danh-gia", [formatView(countRate)]) }}
                  </q-card-section>
                  <q-card-section class="pt-0">
                    <div class="text-gray-400">{{ t("danh-gia-cua-ban") }}</div>

                    <q-rating
                      v-model="myRate"
                      @update:model-value="sendRate"
                      no-reset
                      :readonly="rated"
                      class="mt-2"
                      size="2em"
                      color="grey"
                      max="10"
                      :color-selected="
                        [
                          'light-green-3',
                          'light-green-6',
                          'light-green-7',

                          'light-green-8',
                          'light-green-9',
                          'green',

                          'green-5',
                          'green-6',
                          'green-7',
                          'green-8'
                        ] as unknown as any
                      "
                    >
                      <template
                        v-for="(item, i) in ratesText"
                        :key="i"
                        v-slot:[`tip-${i+1}`]
                      >
                        <q-tooltip
                          class="bg-dark text-[14px] text-weight-medium"
                          >{{ item }}</q-tooltip
                        >
                      </template>
                    </q-rating>
                  </q-card-section>
                </q-card>
              </q-menu>
            </span>
          </div>
        </div>

        <div class="my-2">
          <q-btn
            no-caps
            rounded
            unelevated
            class="bg-[rgba(113,113,113,0.3)] mr-4 text-weight-normal"
            @click="toggleFollow"
          >
            <Icon
              v-if="followed"
              icon="material-symbols:bookmark-added-rounded"
              width="28"
              height="28"
            />
            <Icon
              v-else
              icon="material-symbols:bookmark-add-outline-rounded"
              width="28"
              height="28"
            />
            <span class="text-[14px] font-weight-normal ml-1">{{
              follows ? formatView(follows) : "Theo dõi"
            }}</span>
          </q-btn>
          <q-btn
            no-caps
            rounded
            unelevated
            class="bg-[rgba(113,113,113,0.3)] mr-4 text-weight-normal"
            @click="share"
          >
            <Icon icon="fluent:share-ios-24-regular" width="28" height="28" />
            <span class="text-[14px] font-weight-normal ml-1">{{
              t("chia-se")
            }}</span>
          </q-btn>
          <q-btn
            no-caps
            rounded
            unelevated
            class="bg-[rgba(113,113,113,0.3)] mr-4 text-weight-normal"
            @click="showDialogAddToPlaylist = true"
          >
            <Icon
              icon="fluent:add-square-multiple-16-regular"
              width="28"
              height="28"
            />
            <span class="text-[14px] font-weight-normal ml-1">{{
              t("luu")
            }}</span>
          </q-btn>
        </div>
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
            {{ pointRate }}
          </div>
          <Star />
        </div>
        <div class="divider"></div>
        <span class="text-gray-400">
          {{ t("_rate-nguoi-danh-gia", [formatView(countRate)]) }}
        </span>
        <div class="divider"></div>
        <!-- <span class="text-gray-400">
          {{ formatView(data.follows) }} người theo dõi
        </span> -->

        <router-link
          v-if="data.seasonOf"
          class="c--main"
          :to="data.seasonOf.path"
          >{{ data.seasonOf.name }}
        </router-link>
      </div>

      <div class="tags mt-1">
        <router-link
          v-for="item in data.genre"
          :key="item.name"
          :to="item.path"
          class="text-[rgb(28,199,73)]"
        >
          {{ t("tag-_val", [item.name.replace(/ /, "_")]) }}
        </router-link>
      </div>

      <div class="text-[#9a9a9a] mt-2">
        <span>{{ t("ten-khac") }}</span>

        <span class="text-[#eee] leading-relaxed">{{ data.othername }}</span>
      </div>

      <div class="mt-5 text-[#eee] text-[16px]">{{ t("gioi-thieu") }}</div>
      <div class="flex mt-3">
        <div>
          <q-img-custom
            v-if="data?.image"
            width="152px"
            class="rounded-xl"
            :src="forceHttp2(data.image)"
            referrerpolicy="no-referrer"
          />
        </div>
        <div class="flex-1 ml-4">
          <p
            class="leading-loose whitespace-pre-wrap text-[#9a9a9a]"
            v-html="data.description"
          />
        </div>
      </div>

      <!-- comment embed -->
      <FbComments
        v-if="semverGt(Http.version, '1.0.29')"
        :href="`http://animevietsub.tv/phim/-${seasonId}/`"
        :lang="locale?.replace('-', '_')"
      />
      <template v-else>
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
          :key="seasonId"
          :href="`http://animevietsub.tv/phim/-${seasonId}/`"
          :lang="locale?.replace('-', '_')"
          no_socket
          class="bg-gray-300 rounded-xl mt-3 overflow-hidden"
        />
      </template>
    </div>
    <div class="col-3">
      <q-responsive
        v-if="settingsStore.ui.modeMovie"
        :ratio="((841 / 483) * 3) / 9"
        class="mt-6 rounded-xl bg-[rgba(0,0,0,0.3)] shadow shadow-[rgba(0,0,0,0.4)] max-h-[calc(100vh-169px)]"
      >
        <div class="w-full h-full flex column flex-nowrap">
          <FragmentChaps
            :fetch-season="fetchSeason"
            :seasons="seasons"
            :_cache-data-seasons="_cacheDataSeasons"
            :current-season="currentSeason"
            :current-chap="currentChap"
            :progressWatchStore="progressWatchStore"
          />
        </div>
      </q-responsive>

      <div class="text-h6 mt-3 text-subtitle1">{{ t("de-xuat") }}</div>

      <CardVertical
        v-for="item in data?.toPut"
        :key="item.name"
        :data="item"
        class="mt-3"
        three-line
        show-star
      >
        <template v-slot:img-content>
          <BottomBlur class="update-info-layer">
            <span v-if="!item.chap">
              <template v-if="item.process">{{
                t("tap-_chap", [item.process])
              }}</template>
              <template v-else-if="item.quality">{{ t("movie") }}</template>
            </span>
            <span v-else-if="item.chap === 'Full_Season'">{{
              t("full-season")
            }}</span>
            <span v-else>{{ t("tap-_chap", [item.chap]) }}</span>
          </BottomBlur>
          <Quality v-if="item.quality" class="right-0 top-2 absolute">{{
            data.quality
          }}</Quality>
        </template>
      </CardVertical>
    </div>
  </div>

  <ScreenError v-else :error="error" @click:retry="resetErrorAndRun()" />

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
import { getAnalytics, logEvent } from "@firebase/analytics"
import { Icon } from "@iconify/vue"
import { computedAsync } from "@vueuse/core"
import { useHead } from "@vueuse/head"
import { Http } from "client-ext-animevsub-helper"
import AddToPlaylist from "components/AddToPlaylist.vue"
import BottomBlur from "components/BottomBlur.vue"
import BrtPlayer from "components/BrtPlayer.vue"
import CardVertical from "components/CardVertical.vue"
import FragmentChaps from "components/FragmentChaps.vue"
import QImgCustom from "components/QImgCustom"
import Quality from "components/Quality.vue"
import ScreenError from "components/ScreenError.vue"
import SkeletonCardVertical from "components/SkeletonCardVertical.vue"
import Star from "components/Star.vue"
import FbComments from "components/fb-comments/index.vue"
import MessageScheludeChap from "components/feat/MessageScheludeChap.vue"
import { EmbedFbCmt } from "embed-fbcmt-client/vue"
import { set } from "idb-keyval"
import {
  QBtn,
  QCard,
  QCardSection,
  QMenu,
  QRating,
  QResponsive,
  QSkeleton,
  QTooltip,
  QVideo,
  useQuasar
} from "quasar"
import semverGt from "semver/functions/gt"
import { AjaxLike, checkIsLike } from "src/apis/runs/ajax/like"
import { PlayerFB } from "src/apis/runs/ajax/player-fb"
import { PlayerLink } from "src/apis/runs/ajax/player-link"
import { AjaxRate } from "src/apis/runs/ajax/rate"
import { PhimId } from "src/apis/runs/phim/[id]"
import { PhimIdChap } from "src/apis/runs/phim/[id]/[chap]"
// import BottomSheet from "src/components/BottomSheet.vue"
import type { servers } from "src/constants"
import {
  API_OPEND,
  C_URL,
  TIMEOUT_GET_LAST_EP_VIEWING_IN_STORE,
  WARN
} from "src/constants"
import { forceHttp2 } from "src/logic/forceHttp2"
import { formatView } from "src/logic/formatView"
import { getDataIDB } from "src/logic/get-data-IDB"
import { getDataJson } from "src/logic/get-data-json"
import { getQualityByLabel } from "src/logic/get-quality-by-label"
import { getRealSeasonId } from "src/logic/getRealSeasonId"
import { parseChapName } from "src/logic/parseChapName"
import { unflat } from "src/logic/unflat"
import { useAuthStore } from "stores/auth"
import { useHistoryStore } from "stores/history"
import { usePlaylistStore } from "stores/playlist"
import { useSettingsStore } from "stores/settings"
import type { ShallowReactive, ShallowRef } from "vue"
import { useI18n } from "vue-i18n"
import { useRequest } from "vue-request"

import type { ProgressWatchStore, Season } from "./_season.interface"
import type {
  ResponseDataSeasonError,
  ResponseDataSeasonPending,
  ResponseDataSeasonSuccess
} from "./response-data-season"
// ================ follow ================
// =======================================================
// import SwipableBottom from "components/SwipableBottom.vue"

// ============================================

const __ONLINE__ = import.meta.env.DEV ? Symbol("__ONLINE__") : Symbol("")

const route = useRoute()
const router = useRouter()
const instance = getCurrentInstance()
const { t, locale } = useI18n()
const authStore = useAuthStore()
const playlistStore = usePlaylistStore()
const historyStore = useHistoryStore()
const settingsStore = useSettingsStore()

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

    let result: Ref<Awaited<ReturnType<typeof PhimId>>>

    await Promise.any([
      new Promise<Awaited<ReturnType<typeof PhimId>>>((resolve, reject) => {
        const data = getDataJson<Awaited<ReturnType<typeof PhimId>>>(
          "anime_info",
          id
        )
        if (data) {
          resolve(data)
          return
        }

        getDataIDB<string>(`data-${id}`)
          .then((json) => JSON.parse(json))
          .then(resolve)
          .catch(reject)
      }).then((text) => {
        if (!text) throw new Error("not_found_on_idb")
        console.log("[fs]: use cache from fs %s", id)
        // eslint-disable-next-line promise/always-return
        if (!result) result = ref(text)
      }),
      PhimId(id)
        .then(async (data) => {
          let changed = !result // true if result is undefined
          const watcher =
            result &&
            watch(
              result,
              () => {
                watcher()
                changed = true
              },
              { deep: true }
            )
          if (result) Object.assign(result.value, data)
          else result = ref(data)
          watcher?.()

          Object.assign(result.value, { [__ONLINE__]: true })

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
            console.log("[data main]: No update data in IndexedDB")
          }
        })
        .catch((err) => {
          if (result) return

          error.value = err as Error
          console.error(err)

          throw err
        })
    ])

    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    return result!.value
  },
  {
    refreshDeps: [realIdCurrentSeason],
    refreshDepsAction() {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      delete (data.value as unknown as any)?.[__ONLINE__]
      // data.value = undefined
      run()
    }
  }
)
function resetErrorAndRun() {
  error.value = undefined
  run()
}

const seasons = shallowRef<Season[]>()
const _cacheDataSeasons = reactive<
  Map<
    string,
    | ResponseDataSeasonPending
    | ResponseDataSeasonSuccess
    | ResponseDataSeasonError
  >
>(new Map())
const progressWatchStore = reactive<ProgressWatchStore>(new Map())

watch(
  data,
  () => {
    if (!data.value) {
      seasons.value = undefined
      _cacheDataSeasons.clear()
      progressWatchStore.clear()
    }
  },
  { immediate: true }
)
watch(
  () => {
    if (!data.value) return false
    return data.value.season
  },
  (season) => {
    if (season === false) return
    // check season on tasks
    if (
      seasons.value?.some((item) => item.value === realIdCurrentSeason.value)
    ) {
      console.log("exists on cache by data previous season")
      return
    }

    console.log("data refreshed")
    if (!season) season = []
    console.log("raw season: ", season)
    if (season.length > 0) {
      seasons.value = season.map((item) => {
        return {
          name: item.name,
          value: router.resolve(item.path).params.season as string
        }
      })
      return
    }

    seasons.value = [
      {
        name: "",
        value: currentSeason.value
      }
    ]
  },
  {
    immediate: true
  }
)

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
        status: "pending"
      })
      try {
        console.log("%c fetch progress view", "color: blue")
        Object.assign(item, {
          status: "success",
          response: await getProgressChaps(season)
        })
      } catch (err) {
        Object.assign(item, {
          status: "error",
          error: err as Error
        })
      }
    })
  }
)

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
    status: "pending"
  })
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const currentDataSeason = _cacheDataSeasons.get(season)!
  try {
    console.log("fetch chaps on season")

    const realIdSeason = getRealSeasonId(season)

    const response = shallowRef<Awaited<ReturnType<typeof PhimIdChap>>>()

    let promiseLoadIndexedb: Promise<
      Awaited<ReturnType<typeof PhimIdChap>> | undefined
    > = Promise.resolve(undefined)
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
          console.info("cache wrong")

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
                      err
                    )
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
      (promiseLoadIndexedb = new Promise<
        Awaited<ReturnType<typeof PhimIdChap>>
      >((resolve, reject) => {
        const data = getDataJson<Awaited<ReturnType<typeof PhimIdChap>>>(
          "anime_list",
          realIdSeason
        )
        if (data) {
          resolve(data)
          return
        }

        getDataIDB<string>(`season_data ${realIdSeason}`)
          .then((json) => JSON.parse(json))
          .then(resolve)
          .catch(reject)
      }).then((json) => {
        if (!json) throw new Error("not_found")
        console.log("[fs]: use cache %s", realIdSeason)
        if (!response.value) response.value = json

        return json
      }))
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
          name: t("trailer")
        }
      ]
    } else if (response.value && response.value.chaps.length > 50) {
      console.log("large chap. spliting...")

      // eslint-disable-next-line no-inner-declarations
      function watchHandler() {
        if (!seasons.value || !response.value) return

        let indexMetaSeason = seasons.value.findIndex(
          (item) => item.value === season
        )

        if (indexMetaSeason === -1)
          indexMetaSeason = seasons.value.findIndex(
            (item) => item.value === realIdSeason
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
              ssSibs: seasonsSplited
            }
          }
          if (dataOnCache) {
            Object.assign(dataOnCache, newData)
          } else {
            _cacheDataSeasons.set(value, newData)
          }

          seasonsSplited.push({
            name,
            value
          })
        })
        const newSeasons = [
          ...seasons.value.slice(0, indexMetaSeason),
          ...seasonsSplited,
          ...seasons.value.slice(indexMetaSeason + 1)
        ]
        console.log("current seasons: ", seasons.value)
        seasons.value = newSeasons
        console.log("new seasons: ", newSeasons)
        console.log("set seasons: ", seasons.value)

        return responseOnlineStore.has(response)
      }

      let watcherResponse: (() => void) | undefined = watch(response, () => {
        const doneAll = watchHandler()
        if (doneAll) {
          watcherResponse?.()
          watcherResponse = undefined
        }
      })

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
        { immediate: true }
      )

      onBeforeUnmount(() => {
        watcherSeasons?.()
        watcherResponse?.()
      }, instance)
      return
    }

    Object.assign(currentDataSeason, {
      status: "success",
      response
    })

    console.log(_cacheDataSeasons)
  } catch (err) {
    console.warn(err)
    // error.value = err as Error
    Object.assign(currentDataSeason, {
      status: "error",
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      response: err as any
    })
  }
}

const currentDataCache = computed(() => {
  const inCache = _cacheDataSeasons.get(currentSeason.value)

  if (inCache?.status === "success") return inCache

  return undefined
})
const currentDataSeason = computed(() => currentDataCache.value?.response)
const currentProgresWatch = computed(() => {
  const inCache = progressWatchStore.get(currentSeason.value)

  if (inCache?.status === "success") return inCache.response

  return undefined
})

let watcherChangeIdFirstEp: (() => void) | null = null
onBeforeUnmount(() => watcherChangeIdFirstEp?.())
/** @type - currentChap is episode id */
const currentChap = ref<string>()
watchEffect(async (onCleanup): Promise<void> => {
  watcherChangeIdFirstEp?.()
  if (route.params.chap) {
    currentChap.value = route.params.chap as string
    return
  }
  // if this does not exist make sure the status has not finished loading, this function call also useless

  // if not login -> return first episode in season
  if (!authStore.uid || !settingsStore.restoreLastEp) {
    currentChap.value = currentDataSeason.value?.chaps[0].id
    return
  }
  currentChap.value = undefined
  const episodeId = await Promise.any([
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
        TIMEOUT_GET_LAST_EP_VIEWING_IN_STORE
      )

      onCleanup(() => {
        resolve(undefined)
        clearTimeout(timeout)
      })
    })
  ])

  if (episodeId !== undefined) {
    if (episodeId) {
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
        { immediate: true }
      )
    }
  }
})
const currentMetaChap = computed(() => {
  if (!currentChap.value) return
  return currentDataSeason.value?.chaps.find(
    (item) => item.id === currentChap.value
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
          hash: route.hash
        })
        watcherRestoreLastEp()
      }
    })
    onCleanup(watcherRestoreLastEp)
  },
  { immediate: true }
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
          season: seasonAccuracy.value
        },
        query: route.query,
        hash: route.hash
      })
    } else {
      if (import.meta.env.DEV) console.warn("Redirect to not_found")
      if (data.value && __ONLINE__ in data.value)
        router.replace({
          name: "not_found",
          params: {
            catchAll: route.path.split("/").slice(1)
          },
          query: route.query,
          hash: route.hash
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
      `Redirect chapName wrong current: "${urlChapName}" not equal real: ${correctChapName}.\nAuto edit url to chapName correct`
    )
    router.replace({
      path: `/phim/${route.params.season}/${correctChapName}-${epId}`,
      query: route.query,
      hash: route.hash
    })
  } else {
    // old type url /phim/:season/:chap
    // replace
    console.info("Redirect this url old type redirect to new type url")
    router.replace({
      path: `/phim/${route.params.season}/${correctChapName}-${epId}`,
      query: route.query,
      hash: route.hash
    })
  }
})
useHead(
  computed(() => {
    if (!data.value) return {}

    const title = currentMetaChap.value
      ? t("tap-_chap-_name-_othername", [
          currentMetaChap.value.name,
          data.value.name,
          data.value.othername
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
          content:
            currentDataSeason.value?.image ??
            currentDataSeason.value?.poster ??
            data.value.poster
        },
        {
          property: "og:url",
          content: `${process.env.APP_URL}phim/${realIdCurrentSeason.value}`
        }
      ],
      link: [
        {
          rel: "canonical",
          href: `${process.env.APP_URL}phim/${realIdCurrentSeason.value}`
        }
      ]
    }
  })
)

interface SiblingChap {
  season: Exclude<typeof seasons.value, undefined>[0]
  chap?: Exclude<typeof currentDataSeason.value, undefined>["chaps"][0]
}

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
      chap: currentDataSeason.value.chaps[indexCurrentChap + 1]
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
      season: seasons.value[indexSeason + 1]
    }
  }

  console.info("[[===THE END===]]")
})

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
      chap: currentDataSeason.value.chaps[indexCurrentChap - 1]
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
      season: seasons.value[indexSeason - 1]
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
            type: "youtube"
          }
        ],
        playTech: "trailer"
      }

      return
    }

    configPlayer.value = undefined

    let typeCurrentConfig: keyof typeof servers | null = null

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
                  void $q.notify({
                    message: `Play load error: ${err}`,
                    position: "bottom-right",
                    timeout: 0
                  })
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
                  void $q.notify({
                    message: `Play load error: ${err}`,
                    position: "bottom-right",
                    timeout: 0
                  })
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
                void $q.notify({
                  message: `Play load error: ${err}`,
                  position: "bottom-right",
                  timeout: 0
                })
              })
          }
        } catch (err) {
          $q.notify({
            position: "bottom-right",
            message: (err as Error).message
          })
          console.error(err)
        }
      },
      { immediate: true }
    )
    onCleanup(watcher)
  },
  {
    immediate: true
  }
)
const sources = computed(() => configPlayer.value?.link)

async function getProgressChaps(
  currentSeason: string
): Promise<Map<string, { cur: number; dur: number }> | null> {
  if (!authStore.uid) return null

  const progressChaps = new Map()

  try {
    const docs = await historyStore.getProgressChaps(currentSeason)
    docs.forEach((item) => {
      const { cur, dur } = item.data()
      progressChaps.set(item.id, {
        cur,
        dur
      })
    })
  } catch (err) {
    $q.notify({
      position: "bottom-right",
      message: (err as Error).message
    })
  }

  return progressChaps
}

const followed = ref(false)
const follows = ref(0)

const $q = useQuasar()

const seasonId = computed(() => realIdCurrentSeason.value?.match(/\d+$/)?.[0])
watch(
  seasonId,
  async (seasonId) => {
    if (!authStore.isLogged) {
      console.warn("can't get is like because not login")
      return
    }

    if (seasonId) {
      followed.value = await checkIsLike(seasonId)
    } else {
      followed.value = false
      follows.value = 0
    }
  },
  {
    immediate: true
  }
)

watch(
  data,
  (data) => {
    follows.value = data?.follows ?? 0
  },
  {
    immediate: true
  }
)

async function toggleFollow() {
  if (!authStore.isLogged) {
    $q.notify({
      position: "bottom-right",
      message: t("hay-dang-nhap-truoc-de-theo-doi")
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
      : t("da-xoa-khoi-danh-sach-theo-doi")
  })
}

function share() {
  if (!data.value || !currentMetaSeason.value || !currentMetaChap.value) {
    console.warn("data not ready")

    return
  }
  navigator.share?.({
    title: t("xem-_name-season-_season", [
      data.value.name,
      currentMetaSeason.value.name
    ]),
    text: t("xem-_name-tap-_chap", [
      data.value.name,
      currentMetaChap.value.name
    ]),
    url: C_URL + route.path
  })
}

// =========== playlist ===========
const showDialogAddToPlaylist = ref(false)

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
      nameChap: currentMetaChap.value.name
    })
    $q.notify({
      position: "bottom-right",
      message: t("da-theo-vao-danh-sach-phat")
    })
  } catch (err) {
    $q.notify({
      position: "bottom-right",
      message: (err as Error).message
    })
  }
}
async function removeAnimePlaylist(idPlaylist: string) {
  if (!currentSeason.value) return

  try {
    await playlistStore.deleteAnimeFromPlaylist(idPlaylist, currentSeason.value)
    $q.notify({
      position: "bottom-right",
      message: t("da-xoa-khoi-danh-sach-phat")
    })
  } catch (err) {
    $q.notify({
      position: "bottom-right",
      message: (err as Error).message
    })
  }
}

// =================== rate ======================
const countRate = ref(0)
const pointRate = ref(0)
watch(data, (data) => {
  if (!data) {
    countRate.value = 0
    pointRate.value = 0
    return
  }

  countRate.value = data.count_rate
  pointRate.value = data.rate
})
const myRate = ref(0)
const rated = ref(false)
const ratesText = computed(() => [
  t("phim-chan"),
  t("phim-hoi-chan"),
  t("kem"),
  t("hoi-kem"),
  t("tam-duoc"),
  t("duoc"),
  t("co-ve-hay"),
  t("hay"),
  t("tuyet"),
  t("hoan-hao")
])
watch(currentSeason, () => {
  myRate.value = 0
  rated.value = false
})
async function sendRate() {
  if (rated.value) return

  try {
    if (myRate.value < 5)
      await new Promise<void>((resolve, reject) => {
        $q.dialog({
          title: t("ban-chac-muon-danh-gia-_star-sao-cho-season-nay-chu", [
            myRate.value
          ]),
          message: t(
            "ban-chi-co-the-danh-gia-mot-lan-cho-moi-season-anime-va-khong-the-sua-lai-sau-khi-danh-gia-hay-chac-chan-rang-ban-cam-thay--b-_text--b",
            [ratesText.value[myRate.value - 1]]
          ),
          html: true,
          focus: "cancel",
          ok: { rounded: true, flat: true },
          cancel: { rounded: true, flat: true }
        })
          .onOk(() => {
            resolve()
          })
          .onCancel(() => {
            reject()
          })
          .onDismiss(() => {
            reject()
          })
      })

    rated.value = true

    try {
      // eslint-disable-next-line camelcase
      const { success, count_rate, rate } = await AjaxRate(
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        seasonId.value!,
        myRate.value
      )

      if (success) {
        $q.notify({
          position: "bottom-right",
          message: t("danh-gia-da-duoc-gui")
        })
        // eslint-disable-next-line camelcase
        countRate.value = count_rate
        pointRate.value = rate

        return
      }

      $q.notify({
        position: "bottom-right",
        message: t("ban-da-danh-gia-anime-nay-truoc-day")
      })
      myRate.value = rate
    } catch (err) {
      $q.notify({
        position: "bottom-right",
        message: (err as Error).message
      })
      rated.value = false
    }
  } catch {
    myRate.value = 0
    rated.value = false
  }
}

// ================= analytics ===================
const analytics = getAnalytics()
watch(
  [seasonId, currentMetaSeason, currentMetaChap, () => data.value?.name],
  async ([seasonId, currentMetaSeason, currentMetaChap, name]) => {
    if (!currentMetaSeason) return
    if (!currentMetaChap) return
    if (!name) return
    logEvent(analytics, "watching", {
      value: `${name} - ${currentMetaSeason.name} Tập ${currentMetaChap.name}(${seasonId}/${currentMetaChap.id})`
    })
  },
  { immediate: true }
)

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
      fetch(
        `${API_OPEND}/list-episodes?${[
          name,
          ...(othername?.split(",").map((name) => name.trim()) ?? [])
        ]
          .map((item) => `name=${item}`)
          .join("&")}`,
        {
          signal: controller.signal
        }
      )
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
      })
    ])

    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    return results!
  },
  null,
  {
    onError: WARN
  }
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
          void set(`inoutro:${id}`, JSON.stringify(data))

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
      })
    ])

    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    return results!
  },
  null,
  { onError: WARN }
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
    @apply mr-3 inline-block;
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
