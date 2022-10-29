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
        :progress-chaps="progressChaps"
        @cur-update="
          progressChaps.set($event.id, {
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
      <div class="absolute w-full h-full flex column">
        <div class="py-1 px-4 text-subtitle1 flex items-center justify-between">
          Chọn tập

          <q-btn
            dense
            round
            @click="gridModeTabsSeasons = !gridModeTabsSeasons"
          >
            <Icon icon="fluent:apps-list-24-regular" width="20" height="20" />
          </q-btn>
        </div>

        <q-tabs
          v-model="seasonActive"
          class="min-w-0 w-full tabs-seasons relative"
          :class="{
            'grid-mode': gridModeTabsSeasons,
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
            :ref="(el: QTab) => item.value === seasonActive && (tabsDialogRef = el as QTab)"
          />
        </q-tabs>

        <q-tab-panels
          v-model="seasonActive"
          animated
          keep-alive
          class="flex-1 w-full bg-transparent"
        >
          <q-tab-panel
            v-for="({ value }, index) in seasons"
            :key="index"
            :name="value"
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
              Lỗi khi lấy dữ liệu
              <br />
              <q-btn
                dense
                no-caps
                style="color: #00be06"
                @click="fetchSeason(value)"
                >Thử lại</q-btn
              >
            </div>

            <ChapsGridQBtn
              v-else
              grid
              :chaps="(_cacheDataSeasons.get(value) as ResponseDataSeasonSuccess | undefined)?.response.chaps"
              :season="value"
              :find="
                (item) => value === currentSeason && item.id === currentChap
              "
              :progress-chaps="progressChaps"
              class-item="px-3 py-[6px] mx-[-8px] mb-3"
            />
          </q-tab-panel>
        </q-tab-panels>
      </div>
    </div>
  </div>

  <div
    v-if="loading || !data"
    class="absolute w-full h-full overflow-hidden px-4 pt-6 text-[28px]"
  >
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

  <div v-else class="mx-4 row">
    <div class="col-9">
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
            {{ formatView(data.views) }} lượt xem

            <span v-if="currentDataSeason?.update">
              &bull; Tập mới chiếu vào
              {{
                dayjs(
                  new Date(
                    `${currentDataSeason.update[1]}:${currentDataSeason.update[2]} 1/1/0`
                  )
                ).format("HH:MM")
              }}
              {{
                currentDataSeason.update[0] === 7
                  ? "Chủ nhật"
                  : `thứ ${currentDataSeason.update[0]}`
              }}
              {{
                currentDataSeason.update[0] > new Date().getDay()
                  ? "tuần sau"
                  : ""
              }}
            </span>
          </h5>

          <div class="text-gray-400">
            Tác giả
            <template v-for="(item, index) in data.authors" :key="item.name">
              <router-link :to="item.path" class="text-[rgb(28,199,73)]">{{
                item.name
              }}</router-link
              ><template v-if="index < data.authors.length - 1">, </template>
            </template>
            <div class="divider"></div>
            sản xuất bởi {{ data.studio }}
          </div>
        </div>

        <div class="my-2">
          <q-btn no-caps class="mr-4 text-weight-normal" @click="toggleFollow">
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
          <q-btn no-caps class="mr-4 text-weight-normal" @click="share">
            <Icon icon="fluent:share-ios-24-regular" width="28" height="28" />
            <span class="text-[12px] mt-1">Chia sẻ</span>
          </q-btn>
        </div>
      </div>

      <div class="text-[rgb(230,230,230)] mt-3">
        <Quality>{{ data.quality }}</Quality>
        <div class="divider"></div>
        {{ data.yearOf }}
        <div class="divider"></div>
        Cập nhật tới tập {{ data.duration }}
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
          {{ formatView(data.count_rate) }} người đánh giá
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
          #{{ item.name.replace(/ /, "_") }}
        </router-link>
      </div>

      <div class="text-[#9a9a9a] mt-2">
        <span>Tên khác: </span>

        <span class="text-[#eee] leading-relaxed">{{ data.othername }}</span>
      </div>

      <div class="mt-5 text-[#eee] text-[16px]">Giới thiệu</div>
      <p
        class="mt-3 leading-loose whitespace-pre-wrap text-[#9a9a9a]"
        v-html="data.description"
      />
    </div>
    <div class="col-3">
      <div class="text-h6 mt-3 text-subtitle1">Đề xuất</div>

      <CardVertical
        v-for="item in data?.toPut"
        :key="item.name"
        :data="item"
        class="mt-3"
      />
    </div>
  </div>

  <!--
      followed
    -->
</template>

<script lang="ts" setup>
import { Share } from "@capacitor/share"
import { collection, doc, getDocs, getFirestore } from "@firebase/firestore"
import { Icon } from "@iconify/vue"
import { app } from "boot/firebase"
import BrtPlayer from "components/BrtPlayer.vue"
import CardVertical from "components/CardVertical.vue"
import ChapsGridQBtn from "components/ChapsGridQBtn.vue"
import Quality from "components/Quality.vue"
import SkeletonGridCard from "components/SkeletonGridCard.vue"
import Star from "components/Star.vue"
import { QTab, useQuasar } from "quasar"
import sha256 from "sha256"
import { AjaxLike, checkIsLile } from "src/apis/runs/ajax/like"
import { PhimId } from "src/apis/runs/phim/[id]"
import { PhimIdChap } from "src/apis/runs/phim/[id]/[chap]"
// import BottomSheet from "src/components/BottomSheet.vue"
import type { Source } from "src/components/sources"
import { C_URL, labelToQuality } from "src/constants"
import { scrollXIntoView } from "src/helpers/scrollXIntoView"
import dayjs from "src/logic/dayjs"
import { formatView } from "src/logic/formatView"
import { post } from "src/logic/http"
import { unflat } from "src/logic/unflat"
import { useAuthStore } from "stores/auth"
import {
  computed,
  reactive,
  ref,
  shallowReactive,
  shallowRef,
  watch,
  watchEffect,
} from "vue"
import { useRequest } from "vue-request"
import { useRoute, useRouter } from "vue-router"
// ================ follow ================
// =======================================================
// import SwipableBottom from "components/SwipableBottom.vue"

// ============================================

const route = useRoute()
const router = useRouter()

const currentSeason = computed(() => route.params.season as string)
const currentMetaSeason = computed(() => {
  return seasons.value?.find((item) => item.value === currentSeason.value)
})
const realIdCurrentSeason = computed(() => {
  if (!currentSeason.value) return

  const lastIndexDolar = currentSeason.value.lastIndexOf("$")

  console.log(currentSeason.value.slice(0, lastIndexDolar))

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

    if (season.length > 0) {
      seasons.value = season.map((item) => {
        return {
          name: item.name,
          value: router.resolve(item.path).params.season as string,
        }
      })
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

interface ResponseDataSeasonPending {
  status: "pending"
}
interface ResponseDataSeasonSuccess {
  status: "success"
  response: Awaited<ReturnType<typeof PhimIdChap>>
}
interface ResponseDataSeasonError {
  status: "error"
  response: {
    status: number
  }
}
const _cacheDataSeasons = reactive<
  Map<
    string,
    | ResponseDataSeasonPending
    | ResponseDataSeasonSuccess
    | ResponseDataSeasonError
  >
>(new Map())

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
          name: "Trailer",
        },
      ]
    } else if (response.chaps.length > 50) {
      console.log("large chap. spliting...")
      const { chaps } = response

      const indexMetaSeason =
        seasons.value.findIndex((item) => item.value === season) >>> 0
      const nameSeason = seasons.value[indexMetaSeason].name

      seasons.value.splice(
        indexMetaSeason,
        1,
        ...unflat(chaps, 50).map((chaps, index) => {
          const value = index === 0 ? realIdSeason : `${realIdSeason}$${index}`
          const name = `${nameSeason} (${chaps[0].name} - ${
            chaps[chaps.length - 1].name
          })`

          console.log("set %s by %s", value, chaps[0].id)

          _cacheDataSeasons.set(value, {
            status: "success",
            response: {
              ...response,
              chaps,
            },
          })

          return {
            name,
            value,
          }
        })
      )

      console.log(seasons.value)
      return
    }

    _cacheDataSeasons.set(season, {
      status: "success",
      response,
    })
  } catch (err) {
    console.warn(err)
    _cacheDataSeasons.set(season, {
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


import { useHead } from "@vueuse/head"
useHead(computed(() => {

  if (!data.value || !currentMetaChap.value) return {}


const title = `Tập ${currentMetaChap.value.name} ${data.value.name} (${data.value.othername})`
const description = data.value.description

  return {
    title,
    description,
    meta: [
      { property: "og:title", content: title },
      { property: "og:description", content: description },
      { property: "og:image", content: data.value.poster },
      { property: "og:url", content: `${process.env.APP_URL}phim/${realIdCurrentSeason.value}` }
    ],
    link: [
      {
        rel: "canonical",
        href:  `${process.env.APP_URL}phim/${realIdCurrentSeason.value}`
      }
    ]
  }
}))

const nextChap = computed<
  | {
      season: string
      chap?: string
    }
  | undefined
  // eslint-disable-next-line vue/return-in-computed-property
>(() => {
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
      season: currentSeason.value,
      chap: currentDataSeason.value.chaps[indexCurrentChap + 1].id,
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
      season: seasons.value[indexSeason + 1].value,
    }
  }

  console.info("[[===THE END===]]")
})

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
        ).data
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

const authStore = useAuthStore()
const progressChaps = shallowReactive<
  Map<
    string,
    {
      cur: number
      dur: number
    }
  >
>(new Map())

watch(
  [currentSeason, () => authStore.user_data],
  // eslint-disable-next-line camelcase
  async ([currentSeason, user_data]) => {
    // eslint-disable-next-line camelcase
    if (!user_data || !currentSeason) {
      return
    }

    const db = getFirestore(app)

    // eslint-disable-next-line camelcase
    const userRef = doc(db, "users", sha256(user_data.email + user_data.name))
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    const seasonRef = doc(userRef, "history", currentSeason!)
    const chapRef = collection(seasonRef, "chaps")

    progressChaps.clear()
    const { docs } = await getDocs(chapRef)

    docs.forEach((item) => {
      const { cur, dur } = item.data()
      progressChaps.set(item.id, {
        cur,
        dur,
      })
    })
  },
  {
    immediate: true,
  }
)

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
      message: "Hãy đăng nhập trước để theo dõi",
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
      ? "Đã thêm vào danh sách theo dõi"
      : "Đã xóa khỏi danh sách theo dõi",
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
    dialogTitle: `Chia sẻ ${data.value.name}`,
  })
}
// ================ status ================

const gridModeTabsSeasons = ref(false)
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

.tabs-seasons {
  &.grid-mode:deep(.q-tabs__content) {
    @apply flex-wrap absolute top-0 left-0 w-full z-9999 bg-[var(--q-dark-page)];
  }
}
</style>

<style lang="scss">
.q-panel {
  @apply scrollbar-custom;
}
</style>
