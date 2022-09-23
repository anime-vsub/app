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
      {{ configPlayer }}
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

    <div
      v-if="currentDataSeason?.update"
      class="text-gray-300 px-2 text-center pt-1 text-weight-medium text-[14px]"
    >
      Tập mới cập nhật lúc
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
          : `Thứ ${currentDataSeason.update[0]}`
      }}
      {{ currentDataSeason.update[0] > new Date().getDay() ? "tuần sau" : "" }}
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
import dayjs from "dayjs"

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

const labelToQuality = {
  HD: "720p",
  SD: "480p",
}

onMounted(() => {
  watch(
    configPlayer,
    async (configPlayer) => {
      if (!configPlayer) return

      const sources = configPlayer.link.map((item) => {
        return {
          html: labelToQuality[item.label] ?? item.label,
          url: item.file.startsWith("http") ? item.file : `https:${item.file}`,
        }
      })

      const art = new Artplayer({
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        container: playerRef.value!,
        url: sources[0].url, // 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8',
        poster: data.value!.poster,
        id: "player",
        quality: sources,
        autoplay: true,

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

        volume: 1,
        fastForward: true,
        // autoOrientation: true,

        notice: false,

        isLive: false,
        muted: false,
        pip: false,
        autoSize: true,
        autoMini: true,
        screenshot: false,
        setting: true,
        loop: false,
        flip: true,
        playbackRate: false,
        aspectRatio: true,
        fullscreen: true,
        fullscreenWeb: false,
        miniProgressBar: false,
        mutex: true,
        backdrop: true,
        playsInline: true,
        autoPlayback: false,
        airplay: true,
        theme: "#23ade5",
        lock: true,
        lang: navigator.language.toLowerCase(),
        whitelist: ["*"],
        settings: [
          {
            html: "Select Quality",
            width: 150,
            tooltip: "1080P",
            selector: [
              {
                default: true,
                html: "1080P",
                url: "/assets/sample/video.mp4?id=1080",
              },
              {
                html: "720P",
                url: "/assets/sample/video.mp4?id=720",
              },
              {
                html: "360P",
                url: "/assets/sample/video.mp4?id=360",
              },
            ],
            onSelect: function (item, $dom, event) {
              console.info(item, $dom, event)
              art.switchQuality(item.url, item.html)

              // Change the tooltip
              return item.html
            },
          },
        ],
        contextFmenu: [],
        layers: [
          {
            name: "controller",
            style: {
              position: "absolute",
              top: 0,
              left: 0,
              width: "100%",
              height: "100%",
            },
            html: `

<div class="toolbar-top">
<button class="back">
<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-chevron-left" viewBox="0 0 16 16">
  <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
</svg>
</button>
<button class="settings">
<svg  xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-gear-fill" viewBox="0 0 16 16">
  <path d="M9.405 1.05c-.413-1.4-2.397-1.4-2.81 0l-.1.34a1.464 1.464 0 0 1-2.105.872l-.31-.17c-1.283-.698-2.686.705-1.987 1.987l.169.311c.446.82.023 1.841-.872 2.105l-.34.1c-1.4.413-1.4 2.397 0 2.81l.34.1a1.464 1.464 0 0 1 .872 2.105l-.17.31c-.698 1.283.705 2.686 1.987 1.987l.311-.169a1.464 1.464 0 0 1 2.105.872l.1.34c.413 1.4 2.397 1.4 2.81 0l.1-.34a1.464 1.464 0 0 1 2.105-.872l.31.17c1.283.698 2.686-.705 1.987-1.987l-.169-.311a1.464 1.464 0 0 1 .872-2.105l.34-.1c1.4-.413 1.4-2.397 0-2.81l-.34-.1a1.464 1.464 0 0 1-.872-2.105l.17-.31c.698-1.283-.705-2.686-1.987-1.987l-.311.169a1.464 1.464 0 0 1-2.105-.872l-.1-.34zM8 10.93a2.929 2.929 0 1 1 0-5.86 2.929 2.929 0 0 1 0 5.858z"/>
</svg>
</button>
</div>



<button class="play">
<svg fill="currentColor" xmlns="http://www.w3.org/2000/svg" height="45" width="45" viewBox="0 0 22 22">
  <path d="M17.982 9.275L8.06 3.27A2.013 2.013 0 0 0 5 4.994v12.011a2.017 2.017 0 0 0 3.06 1.725l9.922-6.005a2.017 2.017 0 0 0 0-3.45z"></path>
</svg>
</button>
<button class="pause" style="display: none"> 
<svg fill="currentColor" xmlns="http://www.w3.org/2000/svg" height="45" width="45" viewBox="0 0 22 22">
    <path d="M7 3a2 2 0 0 0-2 2v12a2 2 0 1 0 4 0V5a2 2 0 0 0-2-2zM15 3a2 2 0 0 0-2 2v12a2 2 0 1 0 4 0V5a2 2 0 0 0-2-2z"></path>
</svg>
</button>
`,
            mounted(layer) {
              this.on("video:loadstart", () => {
                layer.classList.add("loading")
              })
              this.on("video:loadeddata", () => {
                layer.classList.remove("loading")
              })
              this.on("play", () => {
                btnPlay.style.display = "none"
                btnPause.style.display = ""
              })
              this.on("pause", () => {
                btnPlay.style.display = ""
                btnPause.style.display = "none"
              })

              const btnPlay = layer.querySelector("button.play")
              const btnPause = layer.querySelector("button.pause")

              btnPlay.addEventListener("click", (event) => {
                event.stopPropagation()
                this.play()
              })
              btnPause.addEventListener("click", (event) => {
                event.stopPropagation()
                this.pause()
              })

              layer.querySelector(".settings").addEventListener("click", () => {
                console.log("setting")
              })
            },
          },
        ],
        icons: {
          loading: '<img src="https://artplayer.org/assets/img/ploading.gif">',
          state: "", //
          //      '<img width="150" heigth="150" src="https://artplayer.org/assets/img/state.svg">',
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

<style lang="scss">
.art-control-playAndPause,
.art-control-volume {
  display: none !important;
}

.art-layer-controller {
  opacity: 0;
  visibility: hidden;
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(0, 0, 0, 0.5);
  background-image: linear-gradient(to bottom, #0006, #0000);
  transition: all 0.2s ease-in-out;
}
.art-control-show .art-layer-controller {
  opacity: 1;
  visibility: visible;
}
.art-bottom {
  flex-direction: column-reverse !important;
}
.art-layer-controller button {
  background: none;
  outline: none;
  border: none;
  color: inherit;
  cursor: pointer;
}
.art-layer-controller .toolbar-top {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 2;
  padding: 8px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.art-layer-controller .toolbar-top svg {
  width: 1.2rem;
  height: 1.2rem;
}
.art-notice {
  display: none !important;
}
.art-layer-controller.loading {
  .play,
  .pause {
    display: none !important;
  }
}
</style>
