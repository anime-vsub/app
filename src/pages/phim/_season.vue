<template>
  <q-page v-if="loading || !data" class="fit flex items-center justify-between">
    <q-spinner style="color: #00be06" size="3em" :thickness="3" />
  </q-page>

  <q-page v-else-if="error">
    {{ error }}
  </q-page>

  <q-page v-else>
    <q-responsive :ratio="16 / 9">
      <div
        class="rounded-borders bg-primary text-white flex flex-center"
        ref="playerRef"
      >
        {{ data.poster }}
        Ratio 16:9
      </div>
    </q-responsive>

    <div class="px-2 pt-4">
      <h1 class="line-clamp-2 text-weight-medium py-0 my-0 text-[18px]">
        {{ data.name }}
      </h1>
      <h5 class="text-gray-400 text-weight-normal">
        {{ formatView(data.views) }} lượt xem
        <span
          class="inline-block w-1 h-1 rounded bg-[currentColor] mb-1 mx-1"
        />
        <router-link
          v-if="data.seasonOf"
          class="c--main"
          :to="data.seasonOf.path"
          >{{ data.seasonOf.name }}</router-link
        >
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
          >{{ item.name }}</router-link
        >
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
        <span class="text-gray-400">
          {{ formatView(data.follows) }} người theo dõi
        </span>
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
    </div>

    <div v-if="currentDataSeason?.update" class="text-gray-300 px-2">
      Tập mới cập nhật lúc
      {{ currentDataSeason?.update }}
    </div>

    <q-btn
      flat
      no-caps
      class="w-full !px-2 mt-4"
      @click="openBottomSheetChap = true"
    >
      <div class="flex items-center justify-between text-subtitle2 w-full">
        <template v-if="currentMetaSeason?.name !== '[[DEFAULT]]'">{{
          currentMetaSeason?.name
        }}</template>
        Tập

        <span class="flex items-center text-gray-300 font-weight-normal">
          Trọn bộ <q-icon name="chevron_right" class="mr-[-8px]"></q-icon>
        </span>
      </div>
    </q-btn>
    <OverScrollX>
      <router-link
        v-for="item in currentDataSeason?.chaps"
        :key="item.id"
        class="btn-chap"
        :class="{
          active: item.id === currentChap,
        }"
        :to="{
          name: 'phim_[season]_[chap]',
          params: {
            season: currentSeason,
            chap: item.id,
          },
        }"
      >
        {{ item.name }}
      </router-link>
    </OverScrollX>

    <!-- bottom sheet -->
    <BottomSheet
      class="h-[calc(100%-100vmin/16*9)]"
      :open="openBottomSheetChap"
    >
      <div class="flex items-center justify-between text-subtitle1 px-2 py-2">
        Season
        <q-btn
          dense
          flat
          round
          icon="close"
          @click="openBottomSheetChap = false"
        />
      </div>
      <div class="relative h-[100%]">
        <OverScrollX>
          <button
            v-for="(item, index) in allSeasons"
            :key="item.value"
            class="chap-name"
            :class="{
              active: item.value === seasonActive,
            }"
            @click="switchToTabSeason(index)"
            :ref="(el) => (tabsBtnSeasonRefs[index] = el as unknown as HTMLButtonElement)"
          >
            {{ item.name }}
          </button>
          <div class="tabs-season-line" :style="lineSeasonStyle" />
        </OverScrollX>

        <q-tab-panels v-model="seasonActive" animated keep-alive class="h-full">
          <q-tab-panel
            v-for="{ value } in allSeasons"
            :key="value"
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
            <router-link
              v-else
              v-for="item in currentDataSeason?.chaps"
              :key="item.id"
              class="btn-chap mt-1 light"
              :class="{
                active: currentSeason === value && item.id === currentChap,
              }"
              :to="{
                name: 'phim_[season]_[chap]',
                params: {
                  season: value,
                  chap: item.id,
                },
              }"
            >
              {{ item.name }}
            </router-link>
          </q-tab-panel>
        </q-tab-panels>
      </div>
    </BottomSheet>

    <!--
      trailer
      toPut
      followed
    -->
  </q-page>
</template>

<script lang="ts" setup>
import Artplayer from "artplayer"
import OverScrollX from "components/OverScrollX.vue"
import Quality from "components/Quality.vue"
import Star from "components/Star.vue"
import { PhimId } from "src/apis/phim/[id]"
import { PhimIdChap } from "src/apis/phim/[id]/[chap]"
import BottomSheet from "src/components/BottomSheet.vue"
import { formatView } from "src/logic/formatView"
import { Hls } from "src/logic/hls"
import { post } from "src/logic/http"
import {
  computed,
  onMounted,
  reactive,
  ref,
  shallowRef,
  watch,
  watchEffect,
} from "vue"
import { useRequest } from "vue-request"
import { useRoute, useRouter } from "vue-router"

const tabsBtnSeasonRefs = reactive<HTMLButtonElement[]>([])

const route = useRoute()
const router = useRouter()

const currentSeason = computed(() => route.params.season as string)
const allSeasons = computed(() => {
  const season = data.value?.season ?? []

  if (season.length > 0) {
    return season.map((item) => {
      return {
        ...item,
        value: router.resolve(item.path).params.season as string,
      }
    })
  }

  return [
    {
      path: `/phim/${currentSeason.value}/`,
      name: "[[DEFAULT]]",
      value: currentSeason.value,
    },
  ]
})
const currentMetaSeason = computed(() => {
  return allSeasons.value?.find((item) => item.value === currentSeason.value)
})

const { data, loading, error } = useRequest(
  () => {
    return PhimId(`/phim/${currentSeason.value}/`)
  },
  {
    refreshDeps: [currentSeason],
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

const seasonActive = ref<string>()
// sync data by active route
watch(currentSeason, (val) => (seasonActive.value = val))

watch(
  seasonActive,
  (seasonActive) => {
    if (!seasonActive) return

    // download data season active
    fetchSeason(seasonActive)
  },
  {
    immediate: true,
  }
)

watchEffect(() => {
  const { value } = allSeasons
  if (value)
    switchToTabSeason(
      value.findIndex((item) => item?.value === currentSeason.value)
    )
})

const currentDataSeason = computed(() => {
  const inCache = _cacheDataSeasons.get(currentSeason.value)

  if (inCache?.status === "success") return inCache.response

  return undefined
})
const currentChap = computed(() => {
  if (route.params.chap) return route.params.chap

  // get first chap in season

  return currentDataSeason.value?.chaps[0].id
})
const currentStream = computed(() => {
  return currentDataSeason.value?.chaps.find(
    (item) => item.id === currentChap.value
  )
})
const configPlayer = shallowRef<{
  link: {
    file: string
    label: string
    preload: string
    type: "hls"
  }[]
  playTech: "api"
}>()
watch(
  currentStream,
  async (currentStream) => {
    if (!currentStream) return

    try {
      configPlayer.value = JSON.parse(
        (
          await post("/ajax/player?v=2019a", {
            link: currentStream.hash,
            play: currentStream.play,
            id: currentStream.id,
            backuplinks: "1",
          })
        ).data
      )
    } catch (err) {
      console.log({ err })
    }
  },
  { immediate: true }
)

const playerRef = ref<HTMLDivElement>()

onMounted(() => {
  watch(
    configPlayer,
    async (configPlayer) => {
      if (!configPlayer) return

      const { file } = configPlayer.link[0]

      const art = new Artplayer({
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        container: playerRef.value!,
        autoplay: true,
        id: "player",
        url: file.startsWith("http") ? file : "https:" + file, // 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8',
        customType: {
          m3u8: function (video, url) {
            if (Hls.isSupported()) {
              const hls = new Hls({
                // progressive: true,
                // debug: true
              })
              // customLoader(hls.config)
              hls.loadSource(url)
              hls.attachMedia(video)
            } else {
              const canPlay = video.canPlayType("application/vnd.apple.mpegurl")
              if (canPlay === "probably" || canPlay === "maybe") {
                video.src = url
              } else {
                art.notice.show = "不支持播放格式：m3u8"
              }
            }
          },
        },
        poster: data.value!.poster,
        volume: 1,
        isLive: false,
        muted: false,
        pip: true,
        autoSize: true,
        autoMini: true,
        screenshot: true,
        setting: true,
        loop: true,
        flip: true,
        playbackRate: true,
        aspectRatio: true,
        fullscreen: true,
        fullscreenWeb: true,
        subtitleOffset: true,
        miniProgressBar: true,
        mutex: true,
        backdrop: true,
        playsInline: true,
        autoPlayback: true,
        airplay: true,
        theme: "#23ade5",
        lang: navigator.language.toLowerCase(),
        whitelist: ["*"],
        settings: [],
        contextmenu: [],
        layers: [],
        quality: [],
        icons: {
          loading: '<img src="https://artplayer.org/assets/img/ploading.gif">',
          state:
            '<img width="150" heigth="150" src="https://artplayer.org/assets/img/state.svg">',
          indicator:
            '<img width="16" heigth="16" src="https://artplayer.org/assets/img/indicator.svg">',
        },
      })
    },
    { immediate: true }
  )
})
async function fetchSeason(season: string) {
  if (_cacheDataSeasons.get(season)?.status === "success") return

  _cacheDataSeasons.set(season, {
    status: "pending",
  })
  try {
    _cacheDataSeasons.set(season, {
      status: "success",
      response: await PhimIdChap(`/phim/${season}/xem-phim.html`),
    })
  } catch (err) {
    _cacheDataSeasons.set(season, {
      status: "error",
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      response: err as any,
    })
  }
}
// ===== setup =====

const lineSeasonStyle = reactive<{
  left: string
  width: string
}>({
  left: "0px",
  width: "0px",
})

function switchToTabSeason(index: number) {
  if (!allSeasons.value?.[index]) return

  seasonActive.value = allSeasons.value[index].value

  const itemBtn = tabsBtnSeasonRefs[index]
  if (!itemBtn) return

  const left = itemBtn.offsetLeft
  const width = itemBtn.offsetWidth

  lineSeasonStyle.left = left + width / 2 + "px"

  lineSeasonStyle.width = width * 0.8 + "px"
}

const openBottomSheetChap = ref(false)
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
</style>

<style lang="scss" scoped>
.btn-chap {
  display: inline-block;
  @apply rounded-[2px] text-4 border border-[rgb(35,37,43)] text-[14px] px-2 mr-2 py-2;
  white-space: nowrap;
  text-align: center;
  border-radius: 10px;

  &.active {
    color: rgb(28, 199, 73);
    border-color: rgb(28, 199, 73) !important;
  }

  &.light {
    border-color: rgb(61, 63, 70);
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
</style>
