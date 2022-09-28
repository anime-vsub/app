<template>
  <div
    class="w-full overflow-hidden fixed top-0 left-0 z-200"
    ref="playerWrapRef"
  >
    <q-responsive :ratio="16 / 9" class="player__wrap">
      <video
        ref="video"
        :poster="poster"
        @play="artPlaying = true"
        @pause="artPlaying = false"
        @durationchange="
          artDuration = ($event.target as HTMLVideoElement).duration
        "
        @timeupdate="
          !currentingTime &&
            (artCurrentTime = ($event.target as HTMLVideoElement).currentTime),
            onVideoTimeUpdate()
        "
        @progress="onVideoProgress"
        @ratechange="
          artPlaybackRate = ($event.target as HTMLVideoElement).playbackRate
        "
        @canplay=";(artLoading = false), onVideoCanPlay()"
        @canplaythrough="artLoading = false"
        @waiting="artLoading = true"
        @error="onVideoError"
        @ended="onVideoEnded"
      />

      <div
        class="absolute top-0 left-0 w-full h-full"
        @touchstart="onBDTouchStart"
        @touchmove="onBDTouchMove"
        @touchend="onBDTouchEnd"
        @click="onClickSkip($event, true)"
        v-show="holdedBD || !artControlShow"
      />
      <transition name="fade__ease-in-out">
        <div
          class="art-layer-controller overflow-hidden flex items-center justify-center"
          :class="{
            'currenting-time': currentingTime,
          }"
          @touchstart="onBDTouchStart"
          @touchmove="onBDTouchMove"
          @touchend="onBDTouchEnd"
          @click="onClickSkip($event, false)"
          v-show="holdedBD || artControlShow"
        >
          <div class="toolbar-top">
            <div class="flex items-start w-max-[70%] flex-nowrap">
              <q-btn flat dense round class="mr-2" @click.stop="router.back()">
                <Icon
                  icon="fluent:chevron-left-24-regular"
                  width="25"
                  height="25"
                />
              </q-btn>

              <div class="">
                <div class="line-clamp-1 text-[18px] text-weight-medium">
                  {{ name }}
                </div>
                <div v-if="nameCurrentChap" class="text-gray-300">
                  Tập
                  {{ nameCurrentChap }}
                </div>
              </div>
            </div>
            <div>
              <q-btn
                dense
                flat
                round
                @click="runRemount"
                :disable="!currentStream"
                class="mr-2"
              >
                <Icon
                  icon="fluent:flash-flow-24-regular"
                  width="25"
                  height="25"
                />
              </q-btn>
              <q-btn dense flat round @click="showDialogSetting = true">
                <Icon
                  icon="fluent:settings-24-regular"
                  width="25"
                  height="25"
                />
              </q-btn>
            </div>
          </div>

          <!-- fix spacing 2px -->
          <q-btn
            flat
            dense
            round
            v-ripple="false"
            class="p-2 w-[72px] h-[72px] relative z-199"
            @click.stop="setArtPlaying(!artPlaying)"
            v-show="!holdedBD"
          >
            <template v-if="!artLoading">
              <Icon
                v-show="!artPlaying"
                icon="fluent:play-circle-20-regular"
                width="60"
                height="60"
              />
              <Icon
                v-show="artPlaying"
                icon="fluent:pause-circle-20-regular"
                width="60"
                height="60"
              />
            </template>
          </q-btn>

          <div class="toolbar-bottom">
            <div class="art-more-controls flex items-center justify-between">
              <div class="flex items-center">
                <q-btn
                  dense
                  flat
                  no-caps
                  class="mr-6 text-weight-normal art-btn"
                  :disable="!nextChap"
                  :to="{
                    name: 'phim_[season]_[chap]',
                    params: nextChap,
                  }"
                >
                  <Icon
                    icon="fluent:next-24-regular"
                    class="mr-2 art-icon"
                    width="18"
                    height="18"
                  />
                  Tiếp
                </q-btn>

                <q-btn
                  dense
                  flat
                  no-caps
                  @click="showDialogChapter = true"
                  class="text-weight-normal art-btn"
                >
                  <Icon
                    icon="fluent:list-24-regular"
                    class="mr-2 art-icon"
                    width="18"
                    height="18"
                  />
                  EP {{ nameCurrentChap }}
                </q-btn>
              </div>

              <div>
                <q-btn
                  dense
                  flat
                  no-caps
                  class="mr-6 text-weight-normal art-btn"
                  @click="showDialogQuality = true"
                >
                  <Icon
                    icon="bi:badge-hd"
                    class="mr-2 art-icon"
                    width="18"
                    height="18"
                  />
                  {{ artQuality }}
                </q-btn>
                <q-btn
                  dense
                  flat
                  no-caps
                  @click="showDialogPlayback = true"
                  class="text-weight-normal art-btn"
                >
                  <Icon
                    icon="fluent:top-speed-24-regular"
                    class="mr-2 art-icon"
                    width="18"
                    height="18"
                  />
                  {{ artPlaybackRate }}x
                </q-btn>
              </div>
            </div>

            <div
              class="art-control-progress"
              @touchstart.stop="onIndicatorMove"
              @touchmove.stop="onIndicatorMove"
              @touchend.stop="onIndicatorEnd"
            >
              <div class="art-control-progress-inner" ref="progressInnerRef">
                <div
                  class="art-progress-loaded"
                  :style="{
                    width: `${artPercentageResourceLoaded * 100}%`,
                  }"
                />
                <div
                  class="art-progress-played"
                  :style="{
                    width: `${(artCurrentTime / artDuration) * 100}%`,
                  }"
                >
                  <div
                    class="absolute w-[20px] h-[20px] right-[-10px] top-[calc(100%-10px)] art-progress-indicator"
                    :data-title="parseTime(artCurrentTime)"
                    @touchstart.stop="currentingTime = true"
                    @touchmove.stop="onIndicatorMove"
                    @touchend.stop="onIndicatorEnd"
                  >
                    <img
                      width="16"
                      heigth="16"
                      src="src/assets/indicator.svg"
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
                  {{ parseTime(artCurrentTime) }} /
                  {{ parseTime(artDuration) }}
                </div>
              </div>
              <div class="art-controls-center"></div>
              <div class="art-controls-right">
                <div
                  class="art-control art-control-fullscreen hint--rounded hint--top"
                  data-index="70"
                  aria-label="Fullscreen"
                  @click="setArtFullscreen(!artFullscreen)"
                >
                  <i
                    v-if="!artFullscreen"
                    class="art-icon art-icon-fullscreenOn"
                  >
                    <Icon
                      icon="fluent:full-screen-maximize-24-regular"
                      width="22"
                      height="22"
                    />
                  </i>
                  <i v-else class="art-icon art-icon-fullscreenOff">
                    <Icon
                      icon="fluent:full-screen-minimize-24-regular"
                      width="22"
                      height="22"
                    />
                  </i>
                </div>
              </div>
            </div>
          </div>
        </div>
      </transition>

      <!-- notices -->
      <transition-group
        tag="div"
        name="notices"
        class="absolute bottom-[40%] left-0 w-full flex column justify-end ml-10 text-[14px] pointer-events-none"
      >
        <div v-for="item in notices" :key="item.id" class="pb-2">
          <span
            class="text-[#fff] bg-[#0009] rounded-[3px] px-[16px] py-[10px] inline-block"
            >{{ item.text }}</span
          >
        </div>
      </transition-group>

      <div
        v-if="artLoading"
        class="absolute top-0 left-0 w-full h-full flex items-center justify-center pointer-events-none z-200"
      >
        <q-spinner size="60px" :thickness="3" />
      </div>

      <!-- /notices -->

      <!-- dialogs -->
      <!--    dialog is settings    -->
      <ArtDialog
        :model-value="artFullscreen && showDialogSetting"
        @update:model-value="showDialogSetting = $event"
        title="Xem thêm"
        fit
      >
        <div class="text-zinc-500 text-[12px] mb-2">Chất lượng</div>
        <div>
          <q-btn
            dense
            flat
            no-caps
            class="px-0 flex-1 text-weight-norrmal text-[13px] py-2 c--main"
            v-for="({ html }, index) in sources"
            :class="{
              'c--main': html === artQuality || (!artQuality && index === 0),
            }"
            :key="html"
            @click="setArtQuality(html)"
            >{{ html }}</q-btn
          >
        </div>

        <div class="text-zinc-500 text-[12px] mt-4 mb-2">Tốc độ phát lại</div>
        <div class="flex flex-nowrap mx-[-8px]">
          <q-btn
            dense
            flat
            no-caps
            class="px-0 flex-1 text-weight-norrmal text-[13px] py-2 c--main"
            v-for="{ name, value } in playbackRates"
            :key="name"
            :class="artPlaybackRate === value ? 'c--main' : 'text-stone-200'"
            @click="setArtPlaybackRate(value)"
            >{{ name }}</q-btn
          >
        </div>
      </ArtDialog>
      <ArtDialog
        :model-value="artFullscreen && showDialogChapter"
        @update:model-value="showDialogChapter = $event"
        title="Danh sách tập"
        fit
      >
        <div class="h-full w-full flex column flex-nowrap">
          <q-tabs
            v-model="seasonActive"
            no-caps
            dense
            inline-label
            active-class="c--main"
            indicator-color="transparent"
          >
            <q-tab
              v-for="item in allSeasons"
              :key="item.value"
              :name="item.value"
              :label="item.name"
              class="bg-[#2a2a2a] mx-1 rounded-sm !min-h-0 py-[3px]"
              content-class="children:!font-normal children:!text-[13px] children:!min-h-0"
              :ref="(el) => item.value === currentSeason && (tabsRef = el as QTab)"
            />
          </q-tabs>

          <q-tab-panels
            v-model="seasonActive"
            animated
            keep-alive
            class="overflow-y-scroll flex-1 mt-4 bg-transparent"
          >
            <q-tab-panel
              v-for="{ value } in allSeasons"
              :key="value"
              :name="value"
              class="!h-[45px] py-0 !px-0"
            >
              <div
                v-if="_cacheDataSeasons.get(value)?.status === 'pending'"
                class="flex justify-center"
              >
                <q-spinner-infinity
                  style="color: #00be06"
                  size="3em"
                  :thickness="3"
                />
              </div>
              <div
                v-else-if="_cacheDataSeasons.get(value)?.status === 'error'"
                class="text-center"
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
                class="!px-3 !py-2 !mx-1"
                class-active="!bg-[rgba(0,194,52,.15)]"
                :chaps="(_cacheDataSeasons.get(value) as ResponseDataSeasonSuccess | undefined)?.response.chaps"
                :season="value"
                :find="
                  (item) => value === currentSeason && item.id === currentChap
                "
              />
            </q-tab-panel>
          </q-tab-panels>
        </div>
      </ArtDialog>
      <ArtDialog
        :model-value="artFullscreen && showDialogPlayback"
        @update:model-value="showDialogPlayback = $event"
        title="Tốc độ"
      >
        <ul>
          <li
            v-for="{ name, value } in [...playbackRates].reverse()"
            :key="value"
            class="py-2 text-center px-12"
            :class="{
              'c--main': value === artPlaybackRate,
            }"
            @click="setArtPlaybackRate(value)"
          >
            {{ name }}
          </li>
        </ul>
      </ArtDialog>
      <ArtDialog
        :model-value="artFullscreen && showDialogQuality"
        @update:model-value="showDialogQuality = $event"
        title="Chất lượng"
      >
        <ul>
          <li
            v-for="({ html }, index) in sources"
            :key="html"
            class="py-2 text-center px-15"
            :class="{
              'c--main': html === artQuality || (!artQuality && index === 0),
            }"
            @click="setArtQuality(html)"
          >
            {{ html }}
          </li>
        </ul>
      </ArtDialog>
      <!-- /dialogs -->
    </q-responsive>
  </div>

  <!-- teleporting -->
  <q-dialog
    :model-value="!artFullscreen && showDialogSetting"
    @update:model-value="showDialogSetting = $event"
    position="bottom"
    full-width
  >
    <q-card flat class="w-full pt-3">
      <q-list>
        <q-item clickable v-ripple>
          <q-item-section avatar>
            <Icon
              icon="fluent:text-bullet-list-square-warning-24-regular"
              width="22"
              height="22"
            />
          </q-item-section>

          <q-item-section>
            <q-item-label> Báo lỗi </q-item-label>
          </q-item-section>
        </q-item>

        <q-item clickable v-ripple>
          <q-item-section avatar>
            <Icon
              icon="fluent:person-feedback-24-regular"
              width="22"
              height="22"
            />
          </q-item-section>

          <q-item-section>
            <q-item-label> Phản hồi </q-item-label>
          </q-item-section>
        </q-item>

        <q-item clickable v-ripple @click="showDialogQuality = true">
          <q-item-section avatar>
            <Icon icon="bi:badge-hd" width="22" height="22" />
          </q-item-section>

          <q-item-section>
            <q-item-label> Chất lượng </q-item-label>
          </q-item-section>
        </q-item>

        <q-item clickable v-ripple @click="showDialogPlayback = true">
          <q-item-section avatar>
            <Icon icon="fluent:top-speed-24-regular" width="22" height="22" />
          </q-item-section>
          <q-item-section>
            <q-item-label> Tốc độ </q-item-label>
          </q-item-section>
        </q-item>

        <q-btn flat no-caps class="w-full py-2 mt-2" v-close-popup>Hủy</q-btn>
      </q-list>
    </q-card>
  </q-dialog>
  <!-- quality -->
  <q-dialog
    :model-value="!artFullscreen && showDialogQuality"
    @update:model-value="showDialogQuality = $event"
    position="bottom"
    full-width
  >
    <q-card flat class="w-full text-[16px]">
      <q-list>
        <q-item v-for="item in sources" :key="item.html" clickable v-ripple>
          <q-item-section avatar>
            <q-icon v-if="artQuality === item.html" name="check" />
          </q-item-section>
          <q-item-section>
            {{ item.html }}
          </q-item-section>
        </q-item>
      </q-list>
    </q-card>
  </q-dialog>
  <!-- playback -->
  <q-dialog
    :model-value="!artFullscreen && showDialogPlayback"
    @update:model-value="showDialogPlayback = $event"
    position="bottom"
    full-width
  >
    <q-card flat class="w-full text-[16px]">
      <q-list>
        <q-item
          v-for="{ name, value } in playbackRates"
          :key="value"
          clickable
          v-ripple
          @click="setArtPlaybackRate(value)"
        >
          <q-item-section avatar>
            <q-icon v-if="artPlaybackRate === value" name="check" />
          </q-item-section>
          <q-item-section>
            {{ name }}
          </q-item-section>
        </q-item>
      </q-list>
    </q-card>
  </q-dialog>
</template>

<script lang="ts" setup>
import { StatusBar } from "@capacitor/status-bar"
import { Haptics } from "@capacitor/haptics"
import { NavigationBar } from "@hugotomazi/capacitor-navigation-bar"
import { Preferences } from "@capacitor/preferences"
import { Icon } from "@iconify/vue"
import ArtDialog from "components/ArtDialog.vue"
import ChapsGridQBtn from "components/ChapsGridQBtn.vue"
import { QTab, useQuasar } from "quasar"
import type { PhimIdChap } from "src/apis/phim/[id]/[chap]"
import { playbackRates } from "src/constants"
import { Hls } from "src/logic/hls"
import { parseTime } from "src/logic/parseTime"
import { computed, ref, shallowReactive, watch, watchEffect } from "vue"
import { onBeforeRouteLeave, useRouter } from "vue-router"

import type { Source } from "./sources"

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

const router = useRouter()
const $q = useQuasar()

const props = defineProps<{
  sources?: Source[]
  currentSeason?: string
  currentChap?: string
  nameCurrentChap?: string
  nextChap?: {
    season: string
    chap?: string
  }
  name: string
  poster?: string
  allSeasons?: {
    value: string
    path: string
    name: string
  }[]
  _cacheDataSeasons: Map<
    string,
    | ResponseDataSeasonPending
    | ResponseDataSeasonSuccess
    | ResponseDataSeasonError
  >
  fetchSeason: (season: string) => Promise<void>
}>()
const uniqueCurChap = computed(() =>
  props.currentChap && props.currentSeason
    ? `${props.currentChap}@${props.currentSeason}`
    : undefined
)

// ===== setup effect =====

const seasonActive = ref<string>()
// sync data by active route
watch(
  () => props.currentSeason,
  (val) => (seasonActive.value = val),
  { immediate: true }
)

watch(
  seasonActive,
  (seasonActive) => {
    if (!seasonActive) return

    // download data season active
    props.fetchSeason(seasonActive)
  },
  {
    immediate: true,
  }
)

// @scrollIntoView
const tabsRef = ref<QTab>()
watchEffect(() => {
  if (!tabsRef.value) return
  if (!props.currentSeason) return

  setTimeout(() => {
    tabsRef.value?.$el.scrollIntoView({
      inline: "center",
    })
  }, 70)
})

// =========================== huuuu player API。馬鹿馬鹿しい ====================================

const currentStream = computed(() => {
  return props.sources?.find((item) => item.html === artQuality.value)
})

const video = ref<HTMLVideoElement>()
// value control get play
const artPlaying = ref(true)
const setArtPlaying = (playing: boolean) => {
  if (!video.value) {
    console.log("video element not ready")
    return
  }
  if (playing) {
    // video.value.load();
    if (video.value.paused) video.value.play()
  } else {
    if (!video.value.paused) video.value.pause()
  }
}
let artEnded = false
const artLoading = ref(true)
const artDuration = ref<number>(0)
const artCurrentTime = ref<number>(0)
const setArtCurrentTime = (currentTime: number) => {
  if (!video.value) {
    console.warn("video not ready")
    return
  }
  video.value.currentTime = currentTime
}
watch(uniqueCurChap, () => setArtCurrentTime(0), { immediate: true })
const artPercentageResourceLoaded = ref<number>(0)
const artPlaybackRate = ref(1)
const setArtPlaybackRate = (value: number) => {
  if (!video.value) {
    console.warn("video not ready")
    return
  }
  video.value.playbackRate = value
  addNotice(`${value}x`)
}

// value control other
const artControlShow = ref(true)
// eslint-disable-next-line functional/no-let
let activeTime = Date.now()
const setArtControlShow = (show: boolean) => {
  artControlShow.value = show
  if (show) activeTime = Date.now()
}
watch(
  artControlShow,
  (artControlShow) => artControlShow && artFullscreen.value && StatusBar.hide()
)
const artFullscreen = ref(false)
const setArtFullscreen = async (fullscreen: boolean) => {
  console.log("set art fullscreen ", fullscreen)
  if (fullscreen) {
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    await playerWrapRef.value!.requestFullscreen()
    screen.orientation.lock("landscape")
    StatusBar.hide()
    NavigationBar.hide()
    StatusBar.setOverlaysWebView({
      overlay: true,
    })
  } else {
    await document.exitFullscreen()
    screen.orientation.unlock()
    StatusBar.show()
    NavigationBar.show()
    StatusBar.setOverlaysWebView({
      overlay: false,
    })
  }

  artFullscreen.value = document.fullscreenElement !== null
}

onBeforeRouteLeave(() => {
  if (artFullscreen.value) {
    setArtFullscreen(false)

    return false
  }

  return true
})

const artQuality = ref<string>()
const setArtQuality = (value: string) => {
  artQuality.value = value
  addNotice(`Chất lượng đã chuyển sang ${value}`)
}

function onVideoProgress(event: Event) {
  const target = event.target as HTMLVideoElement
  // eslint-disable-next-line functional/no-let
  let range = 0
  const bf = target.buffered
  const time = target.currentTime

  try {
    while (!(bf.start(range) <= time && time <= bf.end(range))) {
      range += 1
    }
    // const loadStartPercentage = bf.start(range) / target.duration
    const loadEndPercentage = bf.end(range) / target.duration
    // const loadPercentage = loadEndPercentage - loadStartPercentage

    artPercentageResourceLoaded.value = loadEndPercentage
  } catch {
    try {
      artPercentageResourceLoaded.value = bf.end(0) / target.duration
    } catch {}
  }
}
function onVideoCanPlay() {
  activeTime = Date.now()
}
import { debounce } from "quasar"
const saveCurTimeToPer = debounce(async () => {
  localStorage.setItem(
    `cur_time:${uniqueCurChap.value}`,
    JSON.stringify(artCurrentTime.value)
  )
  console.log("saaved to per")
}, 900)
function onVideoTimeUpdate() {
  if (
    artPlaying.value &&
    !currentingTime.value &&
    artControlShow.value &&
    Date.now() - activeTime >= 3e3
  ) {
    artControlShow.value = false
  }
  saveCurTimeToPer()
}
function onVideoError(event: Event) {
  console.log("video error ", event)

  $q.notify({
    message: "Đã gặp sự cố khi phát lại",
    position: "bottom-left",
    timeout: 0,
    actions: [
      {
        label: "Thử lại",
        color: "white",
        handler() {
          console.log("retry force")
          // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
          video.value!.load()
          // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
          video.value!.play()
        },
      },
      {
        label: "Remount",
        color: "white",
        handler: remount,
      },
    ],
  })
}
function onVideoEnded() {
  artEnded = true
  if (props.nextChap) {
    addNotice(
      props.currentSeason !== props.nextChap.season
        ? `Đang phát ${props.nextChap.season}`
        : "Đang phát tập tiếp theo"
    )

    router.push({
      name: "phim_[season]_[chap]",
      params: props.nextChap,
    })
  }
}

function runRemount() {
  $q.dialog({
    title: "Relay change",
    message: "Bạn đang muốn đổi relay khác?",
    cancel: true,
    persistent: false,
  }).onOk(remount)
}

function remount() {
  if (!currentStream.value) {
    $q.notify({
      position: "bottom-left",
      message: "Video tạm thời không khả dụng",
    })

    return
  }

  const { url, type } = currentStream.value

  const currentTime = artCurrentTime.value
  const playing = !video.value.paused || artPlaying.value || artEnded
  artEnded = false

  switch (type) {
    case "hls":
    case "m3u":
      // eslint-disable-next-line no-case-declarations
      const hls = new Hls()
      // customLoader(hls.config)
      hls.loadSource(url)
      hls.attachMedia(video.value)
      hls.on(Hls.Events.MANIFEST_PARSED, () => {
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        if (playing) video.value!.play()
      })
      break
    default:
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      video.value!.src = url
  }

  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  if (playing) video.value!.play()
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  video.value!.currentTime = currentTime
}
const watcherVideoTagReady = watch(video, (video) => {
  if (!video) return

  // eslint-disable-next-line promise/catch-or-return
  Promise.resolve().then(watcherVideoTagReady) // fix this not ready value

  watch(
    () => currentStream.value?.url,
    (url) => {
      if (!url) return

      console.log("set url art %s", url)

      if (Hls.isSupported()) {
        setTimeout(() => {
          remount()
        }, 100)
      } else {
        const canPlay = video.canPlayType("application/vnd.apple.mpegurl")
        if (canPlay === "probably" || canPlay === "maybe") {
          video.src = url
        }
      }
    },
    { immediate: true }
  )
})

// re-set quality if quality not in sources
watch(
  () => props.sources,
  (sources) => {
    if (!sources || sources.length === 0) return
    // not ready quality on this
    if (!artQuality.value || !currentStream.value) {
      artQuality.value = sources[0].html // not use setArtQuality because skip notify
    }
  },
  { immediate: true }
)

const playerWrapRef = ref<HTMLDivElement>()

const currentingTime = ref(false)
const progressInnerRef = ref<HTMLDivElement>()
function onIndicatorMove(
  event: TouchEvent | MouseEvent,
  innerEl: HTMLDivElement = progressInnerRef.value!,
  offsetX?: number,
  curTimeStart?: number
) {
  currentingTime.value = true
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const maxX = innerEl.offsetWidth
  const { left } = innerEl.getBoundingClientRect()

  console.log(offsetX)

  if (offsetX) {
    const clientX =
      (
        (event as TouchEvent).changedTouches?.[0] ??
        (event as TouchEvent).touches?.[0] ??
        event
      ).clientX -
      offsetX -
      left

    // patch x exists -> enable mode add
    artCurrentTime.value = Math.max(
      0,
      Math.min(
        video.value!.duration,
        curTimeStart + (video.value!.duration * clientX) / maxX
      )
    )
  } else {
    const clientX = Math.min(
      maxX,
      Math.max(
        0,
        (
          (event as TouchEvent).changedTouches?.[0] ??
          (event as TouchEvent).touches?.[0] ??
          event
        ).clientX - left
      )
    )

    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    artCurrentTime.value = (video.value!.duration * clientX) / maxX
  }

  activeTime = Date.now()
}
function onIndicatorEnd() {
  currentingTime.value = false

  setArtCurrentTime(artCurrentTime.value)
  activeTime = Date.now()
}

// ==== addons swipe backdrop ====
// eslint-disable-next-line functional/no-let, no-undef
let timeoutHoldBD: number | NodeJS.Timeout | null = null
// eslint-disable-next-line functional/no-let
const holdedBD = ref(false)
let xStart: number | null = null
let curTimeStart: number | null = null
function onBDTouchStart(event: TouchEvent) {
  holdedBD.value = false
  timeoutHoldBD && clearTimeout(timeoutHoldBD)

  timeoutHoldBD = setTimeout(() => {
    holdedBD.value = true
    currentingTime.value = true
    xStart = (
      (event as TouchEvent).changedTouches?.[0] ??
      (event as TouchEvent).touches?.[0] ??
      event
    ).clientX
    curTimeStart = artCurrentTime.value
    // vibrate
    console.log("hold")
    Haptics.vibrate({ duration: 90 })
  }, 400)
}
function onBDTouchMove(event: TouchEvent) {
  if (timeoutHoldBD) {
    clearTimeout(timeoutHoldBD)
    timeoutHoldBD = null
  }

  if (holdedBD.value) {
    console.log("bd move")
    onIndicatorMove(event, event.target as HTMLDivElement, xStart, curTimeStart)
  }
}
function onBDTouchEnd() {
  if (timeoutHoldBD) {
    clearTimeout(timeoutHoldBD)
    timeoutHoldBD = null
  }

  if (holdedBD.value) {
    holdedBD.value = false
    onIndicatorEnd()
  }

  xStart = null
  curTimeStart = null
}

let lastTimeClick: number
let lastPositionClickIsLeft: boolean | null = null
let timeoutDbClick: number | NodeJS.number | null = null
function onClickSkip(event: MouseEvent, orFalse: boolean) {
  // click
  const now = Date.now()

  const isLeft =
    Math.round(
      (event.changedTouches?.[0] ?? event.touches?.[0] ?? event).clientX
    ) < Math.round((event.target as HTMLDivElement).offsetWidth / 2)

  if (
    (lastPositionClickIsLeft === null || lastPositionClickIsLeft === isLeft) &&
    now - lastTimeClick <= 300
  ) {
    // is double click
    clearTimeout(timeoutDbClick)

    if (artControlShow.value) activeTime = Date.now() // fix for if control show continue show

    if (isLeft) {
      // previous 10s
      setArtCurrentTime(
        (artCurrentTime.value = Math.max(0, artCurrentTime.value - 10))
      )
    } else {
      // next 10s
      setArtCurrentTime(
        (artCurrentTime.value = Math.min(
          artCurrentTime.value + 10,
          artDuration.value
        ))
      )
    }
    // soku
  } else {
    clearTimeout(timeoutDbClick)
    timeoutDbClick = setTimeout(() => {
      setArtControlShow(orFalse)
      lastTimeClick = 0
      lastPositionClickIsLeft = null
    }, 300)
  }

  lastTimeClick = now
  lastPositionClickIsLeft = isLeft
}

const notices = shallowReactive<
  {
    id: number
    text: string
  }[]
>([])
// eslint-disable-next-line functional/no-let
let id = 1
function addNotice(text: string) {
  const uuid = id++
  notices.push({ id: uuid, text })
  setTimeout(() => {
    notices.splice(notices.findIndex((item) => item.id === uuid) >>> 0, 1)
  }, 5000)
}

const showDialogSetting = ref(false)
const showDialogChapter = ref(false)
const showDialogPlayback = ref(false)
const showDialogQuality = ref(false)
</script>

<style lang="scss" scoped>
.art-layer-controller {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  background-color: rgba(0, 0, 0, 0.5);
  background-image: linear-gradient(to bottom, #0006, #0000);

  button {
    background: none;
    outline: none;
    border: none;
    color: inherit;
    cursor: pointer;
  }

  .toolbar-top,
  .toolbar-bottom {
    @media (orientation: landscape) {
      padding: {
        left: 44px !important;
        right: 44px !important;
      }
    }
  }

  .toolbar-top {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    z-index: 2;
    padding: 8px 16px;
    @media (orientation: landscape) {
      padding-top: 32px !important;
    }
    display: flex;
    align-items: center;
    justify-content: space-between;
    opacity: 1;
    visibility: visible;
    transition: all 0.2s ease-in-out;
  }
  &.currenting-time {
    .toolbar-top,
    .art-controls {
      opacity: 0 !important;
      visibility: hidden !important;
    }
    .art-progress-indicator {
      &:after {
        display: block !important;
      }
      transform: scale(1.2) !important;
    }
    .art-control-progress-inner {
      height: 3px !important;
    }
  }
  .toolbar-bottom {
    padding: 50px 7px 0;
    padding: {
      left: 16px;
      right: 16px;
      bottom: 8px;
    }
    display: flex;
    flex-direction: column-reverse;
    @media (orientation: landscape) {
      padding-bottom: 32px !important;
    }
    @apply h-min-[100px] z-60;
    background: {
      image: linear-gradient(to top, #000, #0006, #0000);
      position: bottom;
      repeat: repeat-x;
    }
    justify-content: space-between;
    position: absolute;
    width: 100%;
    bottom: 0;
    left: 0;
    right: 0;
    // background-color: red;

    .art-more-controls {
      display: none;
      @media (orientation: landscape) {
        display: flex;
      }
      margin-top: 16px;
    }

    .art-control-progress {
      position: relative;
      display: flex;
      flex-direction: row;
      align-items: center;
      padding: 16px 4px;
      cursor: pointer;
      z-index: 30;

      .art-control-progress-inner {
        display: flex;
        align-items: center;
        position: relative;
        height: 2px;
        @media (orientation: landscape) {
          height: 4px;
        }
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
          pointer-events: none;
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
          pointer-events: none;
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

        .art-progress-indicator {
          transition: transform 0.2s ease-in-out;
          &:after {
            content: attr(data-title);
            position: absolute;
            z-index: 50;
            top: -25px;
            left: 50%;
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
            transform: translateX(-50%);
            display: none;
          }
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
    }
  }
}
.player__wrap {
  @media (orientation: landscape) {
    width: 100vw !important;
    height: 100vh !important;
  }
}
.notices {
  &-move,
  &-enter-active,
  &-leave-active {
    transition: opacity 0.5s ease, transform 0.5s ease;
  }
  &-enter-from,
  &-leave-to {
    opacity: 0;
    transform: translateX(-30px);
  }
  &-active {
    position: absolute;
  }
}
.art-btn {
  font-size: 12px;
  @media (min-width: 718px) {
    font-size: 14px;
  }
  .art-icon {
    @media (min-width: 718px) {
      width: 20px;
      height: 20px;
    }
  }
}
</style>

<style lang="scss">
.fade__ease-in-out {
  @keyframes fade__ease-in-out {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  }

  &-enter-active {
    animation: fade__ease-in-out 0.22s ease-in-out;
  }
  &-leave-active {
    animation: fade__ease-in-out 0.22s ease-in-out reverse;
  }
}
</style>
