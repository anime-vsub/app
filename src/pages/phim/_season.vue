<template>
  <!-- skeleton first load -->
  <div class="row mx-4">
    <div class="col-9">
      <q-responsive
        v-if="!data"
        :ratio="841 / 483"
        class="max-h-[calc(100vh-169px)]"
      >
        <div class="flex items-center justify-center absolute w-full h-full">
          <q-spinner color="main" size="45px" />
        </div>
      </q-responsive>
      <BrtPlayer
        v-else-if="configPlayer?.playTech !== 'trailer'"
        :sources="sources"
        :current-season="currentSeason"
        :name-current-season="currentMetaSeason?.name"
        :current-chap="currentChap"
        :name-current-chap="currentMetaChap?.name"
        :next-chap="nextChap"
        :name="data.name"
        :poster="currentDataSeason?.poster ?? data.poster"
        :seasons="seasons"
        :_cache-data-seasons="_cacheDataSeasons"
        :fetch-season="fetchSeason"
        @cur-update="
          currentDataCache?.progressChaps?.set($event.id, {
            cur: $event.cur,
            dur: $event.dur,
          })
        "
      />
      <div v-else class="w-full overflow-hidden fixed top-0 left-0 z-200">
        <q-img
          no-spinner
          v-if="sources?.[0]?.url"
          :ratio="841 / 483"
          class="max-h-[calc(100vh-169px)] max-w-[100px]"
          src="~assets/ic_question_result_error.png"
          width="100"
        />
        <q-video
          v-else
          :ratio="841 / 483"
          class="max-h-[calc(100vh-169px)]"
          :src="sources![0]!.url"
        />
      </div>
    </div>
    <div class="col-3 relative">
      <div class="absolute w-full h-full flex column flex-nowrap">
        <div class="py-1 px-4 text-subtitle1 flex items-center justify-between">
          {{ gridModeTabsSeasons ? t("chon-season") : t("chon-tap") }}

          <q-btn
            dense
            round
            @click="gridModeTabsSeasons = !gridModeTabsSeasons"
          >
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

        <div v-if="loading" class="flex-1 flex items-center justify-center">
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
                  !(_tmp = _cacheDataSeasons.get(value)) ||
                  _tmp.status === 'pending'
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
                        ? "chủ nhật"
                        : `thứ ${_tmp.response.update[0]}`,
                      _tmp.response.update[0] > new Date().getDay() + 1
                        ? "tuần sau"
                        : "tuần này",
                    ])
                  }}
                </div>

                <ChapsGridQBtn
                  grid
                  :chaps="_tmp.response.chaps"
                  :season="value"
                  :find="
                    (item) => value === currentSeason && item.id === currentChap
                  "
                  :progress-chaps="_tmp.progressChaps"
                  class-item="px-3 !py-[6px] mb-3"
                />
              </template>
            </q-tab-panel>
          </q-tab-panels>
        </template>
      </div>
    </div>
  </div>

  <div
    v-if="loading || !data"
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

  <div v-else class="mx-4 row">
    <div class="col-9 pr-4">
      <div class="flex-1 mt-4 mb-2">
        <h1
          class="line-clamp-2 text-weight-medium py-0 my-0 text-[18px] leading-normal"
        >
          {{ data.name }}
        </h1>
      </div>

      <div class="flex justify-between">
        <div>
          <h5 class="text-gray-400 text-weight-normal">
            {{ t("formatview-data-views-luot-xem", [formatView(data.views)]) }}
            <span v-if="currentDataSeason?.update">
              &bull;
              {{
                t("tap-moi-chieu-vao-_time-_day", [
                  dayjs(
                    new Date(
                      `${currentDataSeason.update[1]}:${currentDataSeason.update[2]} 1/1/0`
                    )
                  ).format("HH:MM"),
                  currentDataSeason.update[0] === 0
                    ? "chủ nhật"
                    : `thứ ${currentDataSeason.update[0]}`,
                  currentDataSeason.update[0] > new Date().getDay() + 1
                    ? "tuần sau"
                    : "tuần này",
                ])
              }}
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
          <q-btn
            no-caps
            rounded
            unelevated
            class="bg-[rgba(113,113,113,0.3)] mr-4 text-weight-normal"
            @click="share"
          >
            <Icon icon="fluent:share-ios-24-regular" width="28" height="28" />
            <span class="text-[12px] mt-1">{{ t("chia-se") }}</span>
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
      <p
        class="mt-3 leading-loose whitespace-pre-wrap text-[#9a9a9a]"
        v-html="data.description"
      />
    </div>
    <div class="col-3">
      <div class="text-h6 mt-3 text-subtitle1">{{ t("de-xuat") }}</div>

      <CardVertical
        v-for="item in data?.toPut"
        :key="item.name"
        :data="item"
        class="mt-3"
        three-line
        show-star
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { collection, doc, getDocs, getFirestore } from "@firebase/firestore"
import { Icon } from "@iconify/vue"
import { useHead } from "@vueuse/head"
import { app } from "boot/firebase"
import BrtPlayer from "components/BrtPlayer.vue"
import CardVertical from "components/CardVertical.vue"
import ChapsGridQBtn from "components/ChapsGridQBtn.vue"
import Quality from "components/Quality.vue"
import SkeletonCardVertical from "components/SkeletonCardVertical.vue"
import Star from "components/Star.vue"
import {
  QBtn,
  QImg,
  QResponsive,
  QSkeleton,
  QSpinner,
  QTab,
  QTabPanel,
  QTabPanels,
  QTabs,
  QVideo,
  useQuasar,
} from "quasar"
import sha256 from "sha256"
import { AjaxLike, checkIsLile } from "src/apis/runs/ajax/like"
import { PhimId } from "src/apis/runs/phim/[id]"
import { PhimIdChap } from "src/apis/runs/phim/[id]/[chap]"
// import BottomSheet from "src/components/BottomSheet.vue"
import type { Source } from "src/components/sources"
import { C_URL, labelToQuality } from "src/constants"
import { scrollXIntoView, scrollYIntoView } from "src/helpers/scrollIntoView"
import dayjs from "src/logic/dayjs"
import { formatView } from "src/logic/formatView"
import { post } from "src/logic/http"
import { unflat } from "src/logic/unflat"
import { useAuthStore } from "stores/auth"
import { computed, reactive, ref, shallowRef, watch, watchEffect } from "vue"
import { useI18n } from "vue-i18n"
import { useRequest } from "vue-request"
import { RouterLink, useRoute, useRouter } from "vue-router"

import type {
  ResponseDataSeasonError,
  ResponseDataSeasonPending,
  ResponseDataSeasonSuccess,
} from "./response-data-season"

// ================ follow ================
// =======================================================
// import SwipableBottom from "components/SwipableBottom.vue"

// ============================================

const route = useRoute()
const router = useRouter()
const { t } = useI18n()
const authStore = useAuthStore()

const currentSeason = computed(() => route.params.season as string)
const currentMetaSeason = computed(() => {
  return seasons.value?.find((item) => item.value === currentSeason.value)
})
const realIdCurrentSeason = computed(() => {
  if (!currentSeason.value) return

  const lastIndexDolar = currentSeason.value.lastIndexOf("$")

  if (lastIndexDolar === -1) return currentSeason.value

  return currentSeason.value.slice(0, lastIndexDolar)
})

const { data, run, error, loading } = useRequest(
  () => {
    // const { }
    return realIdCurrentSeason.value
      ? PhimId(realIdCurrentSeason.value)
      : Promise.reject()
  },
  {
    refreshDeps: [realIdCurrentSeason],
    refreshDepsAction() {
      run()
    },
  }
)
watch(error, (error) => {
  if (error)
    router.push({
      name: "not_found",
      params: {
        pathMatch: route.path,
      },
      query: {
        message: error.message,
        cause: error.cause + "",
      },
    })
})

const seasons = ref<
  {
    name: string
    value: string
  }[]
>()
watch(
  data,
  () => {
    if (!data.value) {
      seasons.value = undefined

      return
    }

    console.log("data refreshed")

    const season = data.value.season ?? []
    console.log("raw season: ", season)
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
  }
)

const _cacheDataSeasons = reactive<
  Map<
    string,
    | ResponseDataSeasonPending
    | ResponseDataSeasonSuccess
    | ResponseDataSeasonError
  >
>(new Map())
// eslint-disable-next-line camelcase
watch([_cacheDataSeasons, () => authStore.user_data], ([cache, user_data]) => {
  // eslint-disable-next-line camelcase
  if (!user_data) return

  // help me
  cache.forEach(async (item, season) => {
    if (item.status === "error") return

    if (item.progressChaps || item.progressChaps === null) return

    item.progressChaps = null // set is fetching
    item.progressChaps = await getProgressChaps(season)
  })
})

async function fetchSeason(season: string) {
  if (!seasons.value) {
    console.warn("seasons not ready")
    return
  }
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

    const lastIndexDolar = season.lastIndexOf("$")
    const realIdSeason =
      lastIndexDolar === -1 ? season : season.slice(0, lastIndexDolar)

    const response = await PhimIdChap(realIdSeason)

    if (response.chaps.length === 0) {
      console.warn("chaps not found")
      response.chaps = [
        {
          id: "#youtube",
          play: "1",

          hash:
            data.value?.trailer ?? "https://www.youtube.com/embed/qUmMH_TGLS8",
          name: t("trailer"),
        },
      ]
    } else if (response.chaps.length > 50) {
      console.log("large chap. spliting...")
      const { chaps } = response

      // eslint-disable-next-line functional/no-let
      let indexMetaSeason = seasons.value.findIndex(
        (item) => item.value === season
      )

      if (indexMetaSeason === -1)
        indexMetaSeason = seasons.value.findIndex(
          (item) => item.value === realIdSeason
        )

      console.log("index %s = %i", season, indexMetaSeason)

      const nameSeason = seasons.value[indexMetaSeason].name

      const newSeasons = [
        ...seasons.value.slice(0, indexMetaSeason),
        ...unflat(chaps, 50).map((chaps, index) => {
          const value = index === 0 ? realIdSeason : `${realIdSeason}$${index}`
          const name = `${nameSeason} (${chaps[0].name} - ${
            chaps[chaps.length - 1].name
          })`

          console.log("set %s by %s", value, chaps[0].id)

          const dataOnCache = _cacheDataSeasons.get(value)
          const newData: ResponseDataSeasonSuccess = {
            status: "success",
            response: {
              ...response,
              chaps,
            },
          }
          if (dataOnCache) {
            Object.assign(dataOnCache, newData)
          } else {
            _cacheDataSeasons.set(value, newData)
          }

          return {
            name,
            value,
          }
        }),
        ...seasons.value.slice(indexMetaSeason + 1),
      ]
      console.log("current seasons: ", seasons.value)
      seasons.value = newSeasons
      console.log("new seasons: ", newSeasons)
      console.log("set seasons: ", seasons.value)
      return
    }

    Object.assign(currentDataSeason, {
      status: "success",
      response,
    })

    console.log(_cacheDataSeasons)
  } catch (err) {
    console.warn(err)
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

const currentDataCache = computed(() => {
  const inCache = _cacheDataSeasons.get(currentSeason.value)

  if (inCache?.status === "success") return inCache

  return undefined
})
const currentDataSeason = computed(() => currentDataCache.value?.response)
const currentChap = computed(() => {
  if (route.params.chap) return route.params.chap as string

  // get first chap in season

  return currentDataSeason.value?.chaps[0].id
})
const currentMetaChap = computed(() => {
  return currentDataSeason.value?.chaps.find(
    (item) => item.id === currentChap.value
  )
})
useHead(
  computed(() => {
    if (!data.value || !currentMetaChap.value) return {}

    const title = t("tap-_chap-_name-_othername", [
      currentMetaChap.value.name,
      data.value.name,
      data.value.othername,
    ])
    const description = data.value.description

    return {
      title,
      description,
      meta: [
        { property: "og:title", content: title },
        { property: "og:description", content: description },
        { property: "og:image", content: data.value.poster },
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
  })
)

interface SiblingChap {
  season: Exclude<typeof seasons.value, undefined>[0]
  chap?: Exclude<typeof currentDataSeason.value, undefined>["chaps"][0]
}
const nextChap = computed(
  // eslint-disable-next-line vue/return-in-computed-property
  (): SiblingChap | undefined => {
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
      return {
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        season: currentMetaSeason.value!,
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
  }
)
// const prevChap = computed<
//   | {
//       season: typeof seasons.value[0]
//       chap?: typeof currentDataSeason.value.chaps[0]
//     }
//   | undefined
//   // eslint-disable-next-line vue/return-in-computed-property
// >(() => {
//   if (!currentDataSeason.value) return
//   // get index currentChap
//   const indexCurrentChap = !currentMetaChap.value
//     ? -1
//     : currentDataSeason.value.chaps.indexOf(currentMetaChap.value)
//   if (indexCurrentChap === -1) {
//     console.warn("current index not found %i", indexCurrentChap)
//     return
//   }

//   const isFirstChapOfSeason =
//     indexCurrentChap === 0
//   if (!isFirstChapOfSeason) {
//     return {
//       season: currentMetaSeason.value,
//       chap: currentDataSeason.value.chaps[indexCurrentChap - 1]
//     }
//   }

//   if (!seasons.value) return
//   // if current last chap of season
//   // check season of last
//   const indexSeason = !currentMetaSeason.value
//     ? -1
//     : seasons.value.indexOf(currentMetaSeason.value)
//   if (indexSeason === -1) {
//     console.warn("current index not found %i", indexSeason)
//     return
//   }

//   const isFirstSeason = indexSeason === 0
//   if (!isLastSeason) {
//     // first chap of next season
//     return {
//       season: seasons.value[indexSeason - 1]
//     }
//   }

//   console.info("[[===THE END===]]")
// })

const configPlayer = shallowRef<{
  link: {
    file: string
    label: string
    preload: string
    type: "hls" | "youtube"
  }[]
  playTech: "api" | "trailer"
}>()
watch(
  currentMetaChap,
  async (currentMetaChap) => {
    if (!currentMetaChap) return

    configPlayer.value = undefined

    if (currentMetaChap.id === "#youtube") {
      configPlayer.value = {
        link: [
          {
            file: currentMetaChap.hash,
            label: "HD",
            preload: "auto",
            type: "youtube",
          },
        ],
        playTech: "trailer",
      }

      return
    }

    try {
      configPlayer.value = JSON.parse(
        (
          await post("/ajax/player?v=2019a", {
            link: currentMetaChap.hash,
            play: currentMetaChap.play,
            id: currentMetaChap.id,
            backuplinks: "1",
          })
        ).data as string
      )
    } catch (err) {
      console.log({
        err,
      })
    }
  },
  {
    immediate: true,
  }
)
const sources = computed<Source[] | undefined>(() =>
  configPlayer.value?.link.map((item): Source => {
    return {
      html: labelToQuality[item.label] ?? item.label,
      url: item.file.startsWith("http") ? item.file : `https:${item.file}`,
      type: item.type as
        | "aac"
        | "f4a"
        | "mp4"
        | "f4v"
        | "hls"
        | "m3u"
        | "m4v"
        | "mov"
        | "mp3"
        | "mpeg"
        | "oga"
        | "ogg"
        | "ogv"
        | "vorbis"
        | "webm"
        | "youtube",
    }
  })
)

async function getProgressChaps(
  currentSeason: string
): Promise<Map<string, { cur: number; dur: number }> | null> {
  // eslint-disable-next-line camelcase
  const { user_data } = authStore
  // eslint-disable-next-line camelcase
  if (!user_data) {
    return null
  }

  const db = getFirestore(app)

  // eslint-disable-next-line camelcase
  const userRef = doc(db, "users", sha256(user_data.email + user_data.name))
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const seasonRef = doc(userRef, "history", currentSeason!)
  const chapRef = collection(seasonRef, "chaps")

  const { docs } = await getDocs(chapRef)

  const progressChaps = new Map()
  docs.forEach((item) => {
    const { cur, dur } = item.data()
    progressChaps.set(item.id, {
      cur,
      dur,
    })
  })

  return progressChaps
}

// @scrollIntoView
const tabsRef = ref<QTab>()
watchEffect(() => {
  if (!tabsRef.value) return
  if (!currentSeason.value) return

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
const followed = ref(false)
const follows = ref(0)

const $q = useQuasar()

const seasonId = computed(
  () => (route.params.season as string | undefined)?.match(/\d+$/)?.[0]
)
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
  }
)

watch(
  data,
  (data) => {
    follows.value = data?.follows ?? 0
  },
  {
    immediate: true,
  }
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
  navigator.share?.({
    title: t("xem-_name-season-_season", [
      data.value.name,
      currentMetaSeason.value.name,
    ]),
    text: t("xem-_name-tap-_chap", [
      data.value.name,
      currentMetaChap.value.name,
    ]),
    url: C_URL + route.path,
  })
}
// ================ status ================

const gridModeTabsSeasons = ref(false)
watch(seasonActive, () => {
  gridModeTabsSeasons.value = false
})

// eslint-disable-next-line functional/no-let
let _tmp:
  | ResponseDataSeasonPending
  | ResponseDataSeasonSuccess
  | ResponseDataSeasonError
  | undefined
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
  transition: width 0.22s ease, left 0.22s ease;
  transform: translateX(-50%);
  z-index: 12;
}

@import "./tabs-seasons.scss";
</style>
