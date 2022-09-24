<template>
  <q-page v-if="loading || !data" class="fit flex items-center justify-between">
    <q-spinner style="color: #00be06" size="3em" :thickness="3" />
  </q-page>

  <q-page v-else-if="error">
    {{ error }}
  </q-page>

  <q-page v-else>
    <q-responsive :ratio="16 / 9" class="">
      <transition name="fade__ease-in-out">
        <div
          class="art-layer-controller z-67"
          v-show="artControlShow"
          @click="artControlShow = false"
        >
          <div class="toolbar-top">
            <button class="back">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="16"
                height="16"
                fill="currentColor"
                class="bi bi-chevron-left"
                viewBox="0 0 16 16"
              >
                <path
                  fill-rule="evenodd"
                  d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"
                />
              </svg>
            </button>
            <button class="settings">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="16"
                height="16"
                fill="currentColor"
                class="bi bi-gear-fill"
                viewBox="0 0 16 16"
              >
                <path
                  d="M9.405 1.05c-.413-1.4-2.397-1.4-2.81 0l-.1.34a1.464 1.464 0 0 1-2.105.872l-.31-.17c-1.283-.698-2.686.705-1.987 1.987l.169.311c.446.82.023 1.841-.872 2.105l-.34.1c-1.4.413-1.4 2.397 0 2.81l.34.1a1.464 1.464 0 0 1 .872 2.105l-.17.31c-.698 1.283.705 2.686 1.987 1.987l.311-.169a1.464 1.464 0 0 1 2.105.872l.1.34c.413 1.4 2.397 1.4 2.81 0l.1-.34a1.464 1.464 0 0 1 2.105-.872l.31.17c1.283.698 2.686-.705 1.987-1.987l-.169-.311a1.464 1.464 0 0 1 .872-2.105l.34-.1c1.4-.413 1.4-2.397 0-2.81l-.34-.1a1.464 1.464 0 0 1-.872-2.105l.17-.31c.698-1.283-.705-2.686-1.987-1.987l-.311.169a1.464 1.464 0 0 1-2.105-.872l-.1-.34zM8 10.93a2.929 2.929 0 1 1 0-5.86 2.929 2.929 0 0 1 0 5.858z"
                />
              </svg>
            </button>
          </div>

          <button @click.prevent.stop="artPlaying = !artPlaying">
            <svg
              v-if="!artPlaying"
              fill="currentColor"
              xmlns="http://www.w3.org/2000/svg"
              height="45"
              width="45"
              viewBox="0 0 22 22"
            >
              <path
                d="M17.982 9.275L8.06 3.27A2.013 2.013 0 0 0 5 4.994v12.011a2.017 2.017 0 0 0 3.06 1.725l9.922-6.005a2.017 2.017 0 0 0 0-3.45z"
              ></path>
            </svg>
            <svg
              v-else
              fill="currentColor"
              xmlns="http://www.w3.org/2000/svg"
              height="45"
              width="45"
              viewBox="0 0 22 22"
            >
              <path
                d="M7 3a2 2 0 0 0-2 2v12a2 2 0 1 0 4 0V5a2 2 0 0 0-2-2zM15 3a2 2 0 0 0-2 2v12a2 2 0 1 0 4 0V5a2 2 0 0 0-2-2z"
              ></path>
            </svg>
          </button>

          <div class="toolbar-bottom">
            <div class="art-control-progress" data-index="10">
              <div class="art-control-progress-inner" ref="progressInnerRef" @click="onIndicatorMove">
                <div
                  class="art-progress-loaded"
                  :style="{
                    width: `${percentageResourceLoaded * 100}%`,
                  }"
                />
                <div
                  class="art-progress-played"
                  :style="{
                    width: `${(currentTime / duration) * 100}%`,
                  }"
                >
                  <div
                    class="absolute w-[20px] h-[20px] right-[-10px] top-[calc(100%-10px)]"
                    @touchmove="onIndicatorMove"
                  >
                    <img
                      width="16"
                      heigth="16"
                      src="src/assets/img/indicator.svg"
                    />
                  </div>
                </div>
              </div>
            </div>

            <div class="art-controls">
              <div class="art-controls-left">
                <div
                  class="art-control art-control-time art-control-onlyText"
                  data-index="30"
                  style="cursor: auto"
                >
                  {{ currentTime }} / {{ duration }}
                </div>
              </div>
              <div class="art-controls-center"></div>
              <div class="art-controls-right">
                <div
                  class="art-control art-control-fullscreen hint--rounded hint--top"
                  data-index="70"
                  aria-label="Fullscreen"
                >
                  <i class="art-icon art-icon-fullscreenOn">
                    <svg
                      class="icon"
                      width="22"
                      height="22"
                      viewBox="0 0 1024 1024"
                      version="1.1"
                      xmlns="http://www.w3.org/2000/svg"
                    >
                      <path
                        fill="#ffffff"
                        d="M625.777778 256h142.222222V398.222222h113.777778V142.222222H625.777778v113.777778zM256 398.222222V256H398.222222v-113.777778H142.222222V398.222222h113.777778zM768 625.777778v142.222222H625.777778v113.777778h256V625.777778h-113.777778zM398.222222 768H256V625.777778h-113.777778v256H398.222222v-113.777778z"
                      />
                    </svg>
                  </i>
                  <i class="art-icon art-icon-fullscreenOff">
                    <svg
                      class="icon"
                      width="22"
                      height="22"
                      viewBox="0 0 1024 1024"
                      version="1.1"
                      xmlns="http://www.w3.org/2000/svg"
                    >
                      <path
                        fill="#ffffff"
                        d="M768 298.666667h170.666667v85.333333h-256V128h85.333333v170.666667zM341.333333 384H85.333333V298.666667h170.666667V128h85.333333v256z m426.666667 341.333333v170.666667h-85.333333v-256h256v85.333333h-170.666667zM341.333333 640v256H256v-170.666667H85.333333v-85.333333h256z"
                      />
                    </svg>
                  </i>
                </div>
              </div>
            </div>
          </div>
        </div>
      </transition>
      <div
        class="rounded-borders text-white flex flex-center"
        ref="playerRef"
        @click="artControlShow = true"
        :class="{
          'control-show': artControlShow,
        }"
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
import dayjs from "dayjs"
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
const sources = computed(() =>
  configPlayer.value?.link.map((item) => {
    return {
      html: labelToQuality[item.label] ?? item.label,
      url: item.file.startsWith("http") ? item.file : `https:${item.file}`,
    }
  })
)

// ===================== player =========================

const playerRef = ref<HTMLDivElement>()

const labelToQuality: Record<string, string> = {
  HD: "720p",
  SD: "480p",
}

const art = shallowRef<Artplayer | null>(null)
const handlersArtMounted = new Set<(art: Artplayer) => void>()
const watcherArt = watch(art, (art) => {
  if (!art) return
  watcherArt()
  handlersArtMounted.forEach((handler) => handler(art))
  handlersArtMounted.clear()
})
function onArtMounted(handler: (art: Artplayer) => void) {
  if (art.value) handler(art.value)
  else handlersArtMounted.add(handler)
}

// value control set
const artControlShow = ref(true)
const artPlaying = ref(true)

// value control get
const duration = ref<number>(0)
const currentTime = ref<number>(0)
const percentageResourceLoaded = ref<number>(0)

// bind value control get
onArtMounted((art) => {
  art.on("video:durationchange", () => {
    duration.value = art.duration
  })
  art.on("video:timeupdate", () => {
    currentTime.value = art.currentTime
  })

  art.on("video:progress", (event: Event) => {
    const target = event.target as HTMLVideoElement
    // eslint-disable-next-line functional/no-let
    let range = 0
    const bf = target.buffered
    const time = target.currentTime

    while (!(bf.start(range) <= time && time <= bf.end(range))) {
      range += 1
    }
    // const loadStartPercentage = bf.start(range) / target.duration
    const loadEndPercentage = bf.end(range) / target.duration
    // const loadPercentage = loadEndPercentage - loadStartPercentage

    percentageResourceLoaded.value = loadEndPercentage
  })
})

// bind value control set
onArtMounted((art) => {
  watch(artPlaying, (val) => {
    if (val) {
      art.play()
    } else {
      art.pause()
    }
  })
  // eslint-disable-next-line functional/no-let
  let activeTime = Date.now()
  watch(artControlShow, (val) => {
    if (val) {
      activeTime = Date.now()
    }
  })
  art.on("video:timeupdate", () => {
    if (artControlShow.value && Date.now() - activeTime >= 2e3) {
      artControlShow.value = false
    }
  })
})

onMounted(() => {
  watch(
    sources,
    async (sources) => {
      if (!sources) return

      if (art.value) {
        art.value.url = sources[0].url
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        art.value.poster = data.value!.poster!
      } else {
        art.value = new Artplayer({
          // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
          container: playerRef.value!,
          url: sources[0].url, // 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8',
          // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
          poster: data.value!.poster,
          id: "player",
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
                const canPlay = video.canPlayType(
                  "application/vnd.apple.mpegurl"
                )
                if (canPlay === "probably" || canPlay === "maybe") {
                  video.src = url
                }
              }
            },
          },

          volume: 1,
          fastForward: true,
          // autoOrientation: true,

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
          layers: [],
          icons: {
            loading:
              '<img src="https://artplayer.org/assets/img/ploading.gif">',
            state: "", //
            //      '<img width="150" heigth="150" src="https://artplayer.org/assets/img/state.svg">',
            indicator: "",
          },
        })

        Object.defineProperty(art.value.controls, "show", {
          get() {
            return artControlShow.value
          },
          // eslint-disable-next-line @typescript-eslint/no-unused-vars
          set(_val) {
            // empty
          },
        })
      }
    },
    { immediate: true }
  )
})

const progressInnerRef  = ref<HTMLDivElement>()
function onIndicatorMove(event: TouchEvent | MouseEvent) {
  const {clientX} = event.changedTouches?.[0] ?? event.touches?.[0] ?? event 
  
  const maxX =progressInnerRef.value.offsetWidth

  art.value!.currentTime = art.value.duration * clientX/maxX
}
// =======================================================
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
.art-layer-controller {
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
  button {
    background: none;
    outline: none;
    border: none;
    color: inherit;
    cursor: pointer;
  }
  .toolbar-top {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    z-index: 2;
    padding: 8px 16px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    svg {
      width: 1.2rem;
      height: 1.2rem;
    }
  }
  .toolbar-bottom {
    padding: 50px 7px 0;
    @apply h-[100px] z-60;
    background-image: linear-gradient(to top, #000, #0006, #0000);
    background-position: bottom;
    background-repeat: repeat-x;
    flex-direction: column-reverse;
    justify-content: space-between;
    display: flex;
    position: absolute;
    width: 100%;
    bottom: 0;
    left: 0;
    right: 0;

    .art-control-progress {
      position: relative;
      display: flex;
      flex-direction: row;
      align-items: center;
      height: 4px;
      cursor: pointer;

      .art-control-progress-inner {
        display: flex;
        align-items: center;
        position: relative;
        height: 50%;
        width: 100%;
        background: rgba(255, 255, 255, 0.2);

        .art-progress-loaded {
          position: absolute;
          z-index: 10;
          left: 0;
          top: 0;
          right: 0;
          bottom: 0;
          height: 100%;
          width: 0;
          background: rgba(255, 255, 255, 0.4);
        }

        .art-progress-played {
          position: absolute;
          z-index: 20;
          left: 0;
          top: 0;
          right: 0;
          bottom: 0;
          height: 100%;
          width: 0;
          background-color: rgb(0, 194, 52);
        }

        .art-progress-highlight {
          position: absolute;
          z-index: 30;
          left: 0;
          top: 0;
          right: 0;
          bottom: 0;
          height: 100%;
          pointer-events: none;

          span {
            display: inline-block;
            position: absolute;
            left: 0;
            top: 0;
            width: 7px;
            height: 100%;
            background: #fff;
            pointer-events: auto;
          }
        }

        .art-progress-indicator {
          visibility: hidden;
          align-items: center;
          justify-content: center;
          position: absolute;
          z-index: 40;
          border-radius: 50%;
          transform: scale(0.1, 0.1);
          transition: transform 0.1s ease-in-out;

          transform: scale(1, 1) !important;
          visibility: visible !important;
          .art-icon {
            width: 100%;
            height: 100%;
            pointer-events: none;
            user-select: none;
          }
        }

        .art-progress-tip {
          display: none;
          position: absolute;
          z-index: 50;
          top: -25px;
          left: 0;
          height: 20px;
          padding: 0 5px;
          line-height: 20px;
          color: #fff;
          font-size: 12px;
          text-align: center;
          background: rgba(0, 0, 0, 0.7);
          border-radius: 3px;
          font-weight: bold;
          white-space: nowrap;
        }
      }
    }

    .art-control-thumbnails {
      display: none;
      position: absolute;
      bottom: 8px;
      left: 0;
      pointer-events: none;
      background-color: rgba(0, 0, 0, 0.7);
    }

    .art-control-loop {
      display: none;
      position: absolute;
      width: 100%;
      height: 100%;
      left: 0;
      top: 0;
      right: 0;
      bottom: 0;
      pointer-events: none;

      .art-loop-point {
        position: absolute;
        left: 0;
        top: -2px;
        width: 2px;
        height: 8px;
        background: rgba(255, 255, 255, 0.75);
      }
    }

    .art-controls {
      position: relative;
      z-index: 1;
      pointer-events: auto;
      display: flex;
      align-items: center;
      justify-content: space-between;
      height: 40px;

      .art-controls-left,
      .art-controls-right {
        display: flex;
      }

      .art-controls-center {
        flex: 1;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 0 10px;
      }

      .art-controls-right {
        justify-content: flex-end;
      }

      .art-control {
        display: flex;
        align-items: center;
        justify-content: center;
        opacity: 0.9;
        font-size: 12px;
        min-height: 36px;
        min-width: 36px;
        line-height: 1;
        text-align: center;
        cursor: pointer;
        white-space: nowrap;
        .art-icon {
          display: flex;
          align-items: center;
          justify-content: center;
          float: left;
          height: 36px;
          width: 36px;
        }
        &:hover {
          opacity: 1;
        }
      }

      .art-control-onlyText {
        padding: 0 10px;
      }

      .art-control-volume {
        .art-volume-panel {
          position: relative;
          float: left;
          width: 0;
          height: 100%;
          transition: margin 0.2s cubic-bezier(0.4, 0, 1, 1),
            width 0.2s cubic-bezier(0.4, 0, 1, 1);
          overflow: hidden;

          .art-volume-slider-handle {
            position: absolute;
            top: 50%;
            left: 0;
            width: 12px;
            height: 12px;
            border-radius: 12px;
            margin-top: -6px;
            background: #fff;

            &::before {
              left: -54px;
              background: #fff;
            }

            &::after {
              left: 6px;
              background: rgba(255, 255, 255, 0.2);
            }

            &::before,
            &::after {
              content: "";
              position: absolute;
              display: block;
              top: 50%;
              height: 3px;
              margin-top: -2px;
              width: 60px;
            }
          }
        }

        &:hover .art-volume-panel {
          width: 60px;
        }
      }

      .art-control-quality {
        position: relative;
        z-index: 30;
        .art-qualitys {
          display: none;
          position: absolute;
          bottom: 35px;
          width: 100px;
          padding: 5px 0;
          text-align: center;
          color: #fff;
          background: rgba(0, 0, 0, 0.8);
          border-radius: 3px;
          .art-quality-item {
            height: 30px;
            line-height: 30px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            text-shadow: 0 0 2px rgba(0, 0, 0, 0.5);
            &:hover {
              background-color: rgba(255, 255, 255, 0.1);
            }
          }
        }
        &:hover .art-qualitys {
          display: block;
        }
      }
    }

    &:hover {
      .art-progress .art-control-progress .art-control-progress-inner {
        height: 100%;
        .art-progress-indicator {
          transform: scale(1, 1);
          visibility: visible;
        }
      }
    }
  }
}
.art-notice,
.art-bottom,
.art-layer-lock {
  display: none !important;
}
</style>

<style lang="scss">
.fade__ease-in-out {
  @keyframes fade__ease-in-out {
    from {
      opacity: 0;
      visibility: hidden;
    }
    to {
      opacity: 1;
      visibility: visible;
    }
  }

  &-enter-active {
    animation: fade__ease-in-out 0.2s ease-in-out;
  }
  &-leave-active {
    animation: fade__ease-in-out 0.2s ease-in-out reverse;
  }
}
</style>
