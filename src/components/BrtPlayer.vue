<template>
  <div
    class="w-full overflow-hidden bg-[#000] focus-visible:outline-none select-none"
    ref="playerWrapRef"
    tabindex="0"
    autofocus
    @keydown="
      (event) => {
        switch (event.code) {
          case 'ArrowUp':
            event.preventDefault()
            if (!artFullscreen) upVolume()
            break
          case 'ArrowDown':
            event.preventDefault()
            if (!artFullscreen) downVolume()
            break
        }
      }
    "
  >
    <q-responsive
      :ratio="841 / 483"
      class="player__wrap max-h-[calc(100vh-169px)]"
      :class="{
        fullscreen: artFullscreen,
      }"
    >
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
        @volumechange="artVolume = ($event.target as HTMLVideoElement).volume"
        @canplay=";(artLoading = false), onVideoCanPlay()"
        @canplaythrough="artLoading = false"
        @suspend="artLoading = false"
        @waiting="artLoading = true"
        @error="onVideoError"
        @ended="onVideoEnded"
      />

      <div
        class="absolute top-0 left-0 w-full h-full cursor-none"
        @touchstart="onBDTouchStart"
        @touchmove="onBDTouchMove"
        @touchend="onBDTouchEnd"
        @click="onClickSkip"
        @mousemove="setArtControlShow(true)"
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
          @click="onClickSkip"
          @mousemove="setArtControlShow(true)"
          v-show="holdedBD || artControlShow"
        >
          <div class="toolbar-top">
            <div class="flex items-start w-max-[70%] flex-nowrap">
              <div class="">
                <div
                  class="line-clamp-1 art-title text-[18px] text-weight-medium leading-normal"
                >
                  {{ name }}
                </div>
                <div v-if="nameCurrentChap" class="art-subtitle text-gray-300">
                  {{ t("tap-_chap", [nameCurrentChap]) }}
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

                <q-tooltip
                  anchor="bottom middle"
                  self="top middle"
                  class="bg-dark text-[14px] text-weight-medium"
                  transition-show="jump-up"
                  transition-hide="jump-down"
                >
                  {{ t("doi-relay") }}
                </q-tooltip>
              </q-btn>
            </div>
          </div>

          <div class="art-controls-main">
            <q-btn
              flat
              dense
              round
              :ripple="false"
              class="prev"
              @click.stop="skipBack"
            >
              <Icon
                icon="fluent:skip-back-10-20-regular"
                width="35"
                height="35"
              />
            </q-btn>

            <q-btn
              flat
              dense
              round
              :ripple="false"
              class="relative z-199 w-[60px] h-[60px]"
              @click.stop="setArtPlaying(!artPlaying)"
              v-show="!holdedBD"
            >
              <template v-if="!artLoading">
                <transition name="q-transition--scale">
                  <Icon
                    v-if="!artPlaying"
                    icon="fluent:play-circle-20-regular"
                    width="60"
                    height="60"
                  />
                  <Icon
                    v-else
                    icon="fluent:pause-circle-20-regular"
                    width="60"
                    height="60"
                  />
                </transition>
              </template>
            </q-btn>

            <q-btn
              flat
              dense
              round
              :ripple="false"
              class="next"
              @click.stop="skipForward"
            >
              <Icon
                icon="fluent:skip-forward-10-20-regular"
                width="35"
                height="35"
              />
            </q-btn>
          </div>

          <div class="toolbar-bottom" @click.stop>
            <div class="art-more-controls flex items-center justify-between">
              <div class="flex items-center">
                <q-btn
                  dense
                  flat
                  no-caps
                  rounded
                  class="mr-6 text-weight-normal art-btn"
                  :disable="!nextChap"
                  replace
                  :to="
                    nextChap
                      ? `/phim/${nextChap.season.value}/${
                          nextChap.chap
                            ? parseChapName(nextChap.chap.name) +
                              '-' +
                              nextChap.chap?.id
                            : ''
                        }`
                      : undefined
                  "
                >
                  <Icon
                    icon="fluent:next-24-regular"
                    class="mr-2 art-icon"
                    width="18"
                    height="18"
                  />
                  {{ t("tiep") }}

                  <q-tooltip
                    v-if="nextChap"
                    anchor="top middle"
                    self="bottom middle"
                    class="bg-dark text-[14px] text-weight-medium"
                    transition-show="jump-up"
                    transition-hide="jump-down"
                  >
                    {{
                      t("_message-hint-next", [
                        currentSeason !== nextChap.season.value
                          ? `Tiếp theo: ${nextChap.season.name}`
                          : `Tiếp theo: Tập ${nextChap.chap?.name}`,
                      ])
                    }}
                  </q-tooltip>
                </q-btn>

                <div
                  class="flex items-center mr-6 text-weight-normal art-btn art-volume"
                  :class="{ active: !artVolumeOutside }"
                  ref="wrapVolumeRef"
                >
                  <q-btn round flat dense @click="toggleMuted">
                    <Icon
                      :icon="
                        [
                          'fluent:speaker-off-24-regular',
                          'fluent:speaker-1-24-regular',
                          'fluent:speaker-2-24-regular',
                        ][artVolume === 0 ? 0 : artVolume < 0.5 ? 1 : 2]
                      "
                      class="mr-2 art-icon"
                      width="18"
                      height="18"
                    />

                    <q-tooltip
                      anchor="top middle"
                      self="bottom middle"
                      class="bg-dark text-[14px] text-weight-medium"
                      transition-show="jump-up"
                      transition-hide="jump-down"
                    >
                      {{ artVolume === 0 ? "Bật tiếng (m)" : "Tắt tiếng (m)" }}
                    </q-tooltip>
                  </q-btn>

                  <div
                    class="overflow-hidden py-1 w-0 transition-width duration-300 ease-in-out slider"
                    @mouseout.stop
                  >
                    <q-slider
                      :model-value="artVolume"
                      @update:model-value="setArtVolume($event ?? 0)"
                      :min="0"
                      :max="1"
                      :step="0.05"
                      dense
                      color="white"
                      track-size="3px"
                      thumb-size="17px"
                    />
                  </div>
                </div>

                <div
                  class="art-control art-control-time art-control-onlyText hide-fscrn"
                  data-index="30"
                  style="cursor: auto"
                >
                  {{ parseTime(artCurrentTime) }} /
                  {{ parseTime(artDuration) }}
                </div>

                <q-btn
                  dense
                  flat
                  no-caps
                  rounded
                  @click="showDialogChapter = true"
                  class="text-weight-normal art-btn only-fscrn"
                >
                  <Icon
                    icon="fluent:list-24-regular"
                    class="mr-2 art-icon"
                    width="18"
                    height="18"
                  />
                  {{ t("ep-_chap", [nameCurrentChap]) }}

                  <q-tooltip
                    v-if="!showDialogChapter"
                    anchor="top middle"
                    self="bottom middle"
                    class="bg-dark text-[14px] text-weight-medium"
                    transition-show="jump-up"
                    transition-hide="jump-down"
                  >
                    {{ t("danh-sach-tap") }}
                  </q-tooltip>

                  <q-menu
                    anchor="top middle"
                    self="bottom middle"
                    :offset="[0, 20]"
                    class="rounded-xl shadow-xl bg-[rgba(28,28,30,1)] min-w-[200px] min-h-[165px] max-w-[329px] flex column flex-nowrap overflow-visible"
                    ref="menuChapsRef"
                  >
                    <div>
                      <div
                        class="py-1 px-4 text-subtitle1 flex items-center justify-between"
                      >
                        {{
                          gridModeTabsSeasons ? t("chon-season") : t("chon-tap")
                        }}

                        <q-btn
                          dense
                          round
                          unelevated
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
                    </div>

                    <div
                      class="h-full min-h-0 overflow-y-auto scrollbar-custom"
                    >
                      <div
                        v-if="!seasons"
                        class="flex-1 flex items-center justify-center py-4"
                      >
                        <q-spinner color="main" size="3em" :thickness="3" />
                      </div>

                      <template v-else>
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
                              <q-spinner
                                style="color: #00be06"
                                size="3em"
                                :thickness="3"
                              />
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
                              <div
                                v-if="_tmp.response.update"
                                class="mb-2 text-gray-300"
                              >
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
                                    _tmp.response.update[0] >
                                    new Date().getDay() + 1
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
                                  (item) =>
                                    value === currentSeason &&
                                    item.id === currentChap
                                "
                                :progress-chaps="_tmp.progressChaps"
                                class-item="px-3 !py-[6px] mb-3"
                              />
                            </template>
                          </q-tab-panel>
                        </q-tab-panels>
                      </template>
                    </div>

                    <q-resize-observer
                      @resize="menuChapsRef?.updatePosition()"
                    />
                  </q-menu>
                </q-btn>
              </div>

              <div>
                <q-btn
                  dense
                  flat
                  no-caps
                  rounded
                  class="mr-6 text-weight-normal art-btn"
                >
                  <Icon
                    icon="bi:badge-hd"
                    class="mr-2 art-icon"
                    width="18"
                    height="18"
                  />
                  {{ artQuality }}

                  <q-menu
                    v-model="showMenuQuality"
                    anchor="top middle"
                    self="bottom middle"
                    :offset="[0, 20]"
                    class="rounded-xl shadow-xl min-w-[200px]"
                  >
                    <div
                      class="bg-[rgba(45,45,45,0.95)] py-2 px-4 flex items-center justify-between relative"
                    >
                      {{ t("chat-luong") }}

                      <q-btn
                        dense
                        flat
                        round
                        icon="close"
                        class="text-zinc-500"
                        v-close-popup
                      />
                    </div>
                    <div
                      class="bg-[rgba(28,28,30,0.95)] !min-h-0 px-4 relative"
                    >
                      <BottomBlurRelative>
                        <ul class="mx-[-16px]">
                          <li
                            v-for="({ html }, index) in sources"
                            :key="html"
                            class="py-2 text-center px-16 cursor-pointer transition-background duration-200 ease-in-out hover:bg-[rgba(255,255,255,0.1)]"
                            :class="{
                              'c--main':
                                html === artQuality ||
                                (!artQuality && index === 0),
                            }"
                            @click="setArtQuality(html)"
                          >
                            {{ html }}
                          </li>
                        </ul>
                      </BottomBlurRelative>
                    </div>
                  </q-menu>

                  <q-tooltip
                    v-if="!showMenuQuality"
                    anchor="top middle"
                    self="bottom middle"
                    class="bg-dark text-[14px] text-weight-medium"
                    transition-show="jump-up"
                    transition-hide="jump-down"
                  >
                    {{ t("chat-luong") }}
                  </q-tooltip>
                </q-btn>
                <q-btn
                  dense
                  flat
                  no-caps
                  rounded
                  class="mr-6 ttext-weight-normal art-btn"
                >
                  <Icon
                    icon="fluent:top-speed-24-regular"
                    class="mr-2 art-icon"
                    width="18"
                    height="18"
                  />
                  {{ t("_playback-x", [artPlaybackRate]) }}

                  <q-menu
                    v-model="showMenuPlaybackRate"
                    anchor="top middle"
                    self="bottom middle"
                    :offset="[0, 20]"
                    class="rounded-xl shadow-xl min-w-[200px]"
                  >
                    <div
                      class="bg-[rgba(45,45,45,0.95)] py-2 px-4 flex items-center justify-between relative"
                    >
                      {{ t("toc-do") }}

                      <q-btn
                        dense
                        flat
                        round
                        icon="close"
                        class="text-zinc-500"
                        v-close-popup
                      />
                    </div>
                    <div
                      class="bg-[rgba(28,28,30,0.95)] !min-h-0 px-4 relative"
                    >
                      <BottomBlurRelative>
                        <ul class="mx-[-16px]">
                          <li
                            v-for="{ name, value } in [
                              ...playbackRates,
                            ].reverse()"
                            :key="value"
                            class="py-2 text-center px-16 cursor-pointer transition-background duration-200 ease-in-out hover:bg-[rgba(255,255,255,0.1)]"
                            :class="{
                              'c--main': value === artPlaybackRate,
                            }"
                            @click="setArtPlaybackRate(value)"
                          >
                            {{ name }}
                          </li>
                        </ul>
                      </BottomBlurRelative>
                    </div>
                  </q-menu>

                  <q-tooltip
                    v-if="!showMenuPlaybackRate"
                    anchor="top middle"
                    self="bottom middle"
                    class="bg-dark text-[14px] text-weight-medium"
                    transition-show="jump-up"
                    transition-hide="jump-down"
                  >
                    {{ t("toc-do-phat") }}
                  </q-tooltip>
                </q-btn>
                <q-btn
                  dense
                  flat
                  round
                  no-caps
                  class="text-weight-normal art-btn"
                  @click="toggleArtFullscreen"
                >
                  <Icon
                    v-if="!artFullscreen"
                    icon="fluent:full-screen-maximize-24-regular"
                    class="art-icon"
                    width="24"
                    height="24"
                  />
                  <Icon
                    v-else
                    icon="fluent:full-screen-minimize-24-regular"
                    class="art-icon"
                    width="24"
                    height="24"
                  />

                  <q-tooltip
                    ref="tooltipFullscreenRef"
                    anchor="top middle"
                    self="bottom middle"
                    class="bg-dark text-[14px] text-weight-medium"
                    transition-show="jump-up"
                    transition-hide="jump-down"
                  >
                    {{
                      artFullscreen
                        ? "Thoát khỏi chế độ toàn màn hình (f)"
                        : "Toàn màn hình (f)"
                    }}
                  </q-tooltip>
                </q-btn>
              </div>
            </div>

            <div
              class="art-control-progress"
              @mousedown.stop="onIndicatorMove"
              @mousemove.stop="onIndicatorMove"
              @mouseover="artControlProgressHoving = true"
              @mouseout="artControlProgressHoving = false"
            >
              <div class="art-control-progress-inner" ref="progressInnerRef">
                <div
                  class="art-progress-loaded"
                  :style="{
                    width: `${artPercentageResourceLoaded * 100}%`,
                  }"
                />
                <div
                  v-if="artControlProgressHoving && !currentingTime"
                  class="art-progress-hoved"
                  :data-title="parseTime(artCurrentTimeHoving)"
                  :style="{
                    width: `${(artCurrentTimeHoving / artDuration) * 100}%`,
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
                    :data-title="parseTime(artCurrentTimeHoving)"
                    @mousedown.stop="currentingTime = true"
                    @mousemove.stop="onIndicatorMove"
                  >
                    <img width="16" heigth="16" src="~assets/indicator.svg" />
                  </div>
                </div>
              </div>
            </div>

            <div class="art-controls only-fscrn">
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
        v-if="!sources || artLoading"
        class="absolute top-0 left-0 w-full h-full flex items-center justify-center pointer-events-none z-200"
      >
        <q-spinner size="60px" :thickness="3" />
      </div>

      <!-- /notices -->
    </q-responsive>
  </div>
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"
import {
  useDocumentVisibility,
  useEventListener,
  useFullscreen,
  useIntervalFn,
  useMouseInElement,
} from "@vueuse/core"
import BottomBlurRelative from "components/BottomBlurRelative.vue"
import ChapsGridQBtn from "components/ChapsGridQBtn.vue"
import type Hlsjs from "hls.js"
import type { PlaylistLoaderConstructor } from "hls.js"
import Hls from "hls.js"
import {
  debounce,
  QBtn,
  QMenu,
  QResizeObserver,
  QResponsive,
  QSlider,
  QSpinner,
  QTab,
  QTabPanel,
  QTabPanels,
  QTabs,
  QTooltip,
  throttle,
  useQuasar,
} from "quasar"
import { playbackRates } from "src/constants"
import { checkContentEditable } from "src/helpers/checkContentEditable"
import { scrollXIntoView, scrollYIntoView } from "src/helpers/scrollIntoView"
import dayjs from "src/logic/dayjs"
import { fetchJava } from "src/logic/fetchJava"
import { parseChapName } from "src/logic/parseChapName"
import { parseTime } from "src/logic/parseTime"
import type {
  ResponseDataSeasonError,
  ResponseDataSeasonPending,
  ResponseDataSeasonSuccess,
} from "src/pages/phim/response-data-season"
import { useAuthStore } from "stores/auth"
import { useHistoryStore } from "stores/history"
import { useSettingsStore } from "stores/settings"
import {
  computed,
  onBeforeUnmount,
  ref,
  shallowReactive,
  watch,
  watchEffect,
} from "vue"
import { useI18n } from "vue-i18n"
import { onBeforeRouteLeave, useRouter } from "vue-router"

import type { Source } from "./sources"

const { t } = useI18n()
// fix toolip fullscreen not hide if change fullscreen

// keyboard binding

const authStore = useAuthStore()
const settingsStore = useSettingsStore()
const historyStore = useHistoryStore()

const router = useRouter()
const $q = useQuasar()

interface SiblingChap {
  season: {
    name: string
    value: string
  }
  chap?: {
    name: string
    id: string
  }
}

const props = defineProps<{
  sources?: Source[]
  currentSeason: string
  nameCurrentSeason?: string
  currentChap?: string
  nameCurrentChap?: string
  nextChap?: SiblingChap
  // prevChap?: SiblingChap
  name: string
  poster?: string
  seasons?: {
    value: string
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

const playerWrapRef = ref<HTMLDivElement>()
const documentVisibility = useDocumentVisibility()

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

const menuChapsRef = ref<QMenu>()
// =========================== huuuu player API。馬鹿馬鹿しい ====================================

const currentStream = computed(() => {
  return props.sources?.find((item) => item.html === artQuality.value)
})

const video = ref<HTMLVideoElement>()
watch(
  video,
  (video) => {
    if (video && documentVisibility.value === "visible")
      try {
        video.play()
      } catch {}
  },
  { immediate: true }
)
// value control get play
const artPlaying = ref(false)
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
watch(
  () => props.currentChap,
  (newVal, oldVal) => {
    if (newVal && oldVal) setArtPlaying(true)
  }
)
// eslint-disable-next-line functional/no-let
let artEnded = false
const artLoading = ref(false)
const artDuration = ref<number>(0)
const artCurrentTime = ref<number>(0)
const setArtCurrentTime = (currentTime: number) => {
  if (!video.value) {
    console.warn("video not ready")
    return
  }
  video.value.currentTime = currentTime
  artCurrentTime.value = currentTime
}
// eslint-disable-next-line functional/no-let
let progressRestored = false
watch(
  [() => props.currentChap, () => authStore.uid],
  async ([currentChap, uid]) => {
    if (currentChap && uid) {
      progressRestored = false

      try {
        setArtCurrentTime(0)
        const cur = (
          await historyStore.getProgressChap(props.currentSeason, currentChap)
        )?.cur

        if (
          cur &&
          !artControlProgressHoving.value /* disable if user hoving to progress bar */
        ) {
          setArtCurrentTime(cur)
          addNotice(t("da-khoi-phuc-phien-xem-truoc-_time", [parseTime(cur)]))
        }
      } catch (err) {
        console.error(err)
      }

      progressRestored = true
    }
  },
  { immediate: true }
)
const artPercentageResourceLoaded = ref<number>(0)
const artPlaybackRate = ref(1)
const setArtPlaybackRate = (value: number) => {
  if (!video.value) {
    console.warn("video not ready")
    return
  }
  video.value.playbackRate = value
  addNotice(t("toc-do-phat-_value-x", [value]))
}
const artVolume = computed<number>({
  get: () => settingsStore.player.volume,
  set: (val) => {
    settingsStore.player.volume = val
  },
})
const setArtVolume = (value: number) => {
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  video.value!.volume = value
}
// eslint-disable-next-line functional/no-let
let lastVolumeBeforeMute: number
const toggleMuted = () => {
  const newValue = artVolume.value === 0 ? lastVolumeBeforeMute ?? 0.05 : 0

  if (artVolume.value > 0) {
    lastVolumeBeforeMute = artVolume.value
  }

  setArtVolume(newValue)

  addNotice(t("am-luong-_volume", [Math.round(newValue * 100)]))
}

// value control other
const artControlShow = ref(true)
// eslint-disable-next-line functional/no-let
let activeTime = Date.now()
const setArtControlShow = (show: boolean) => {
  artControlShow.value = show
  if (show) activeTime = Date.now()
}
const {
  isFullscreen: artFullscreen,
  toggle: toggleArtFullscreen,
  exit: exitArtFullscreen,
} = useFullscreen(playerWrapRef)
const tooltipFullscreenRef = ref<QTooltip>()
watch(artFullscreen, () => tooltipFullscreenRef.value?.hide())
// fix done
onBeforeRouteLeave(() => {
  if (artFullscreen.value) {
    exitArtFullscreen()

    return false
  }

  return true
})

const artQuality = ref<string>()
const setArtQuality = (value: string) => {
  artQuality.value = value
  addNotice(t("chat-luong-da-chuyen-sang-_value", [value]))
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

watch(
  [
    () => authStore.user_data,
    () => props.currentSeason,
    () => props.nameCurrentSeason,
    () => props.poster,
  ],
  // eslint-disable-next-line camelcase
  async ([user_data, currentSeason, seasonName, poster]) => {
    // eslint-disable-next-line camelcase
    if (!user_data || !currentSeason || !seasonName || !poster) return
    console.log("set new season poster %s", poster)
    await historyStore.createSeason(currentSeason, {
      poster,
      seasonName,
      name: props.name,
    })
  },
  { immediate: true }
)

const emit = defineEmits<{
  (
    name: "cur-update",
    val: {
      cur: number
      dur: number
      id: string
    }
  ): void
}>()
const saveCurTimeToPer = throttle(async () => {
  if (!progressRestored) return
  if (!props.currentChap) return
  if (!props.nameCurrentChap) return

  await historyStore.setProgressChap(props.currentSeason, props.currentChap, {
    cur: artCurrentTime.value,
    dur: artDuration.value,
    name: props.nameCurrentChap,
  })

  emit("cur-update", {
    cur: artCurrentTime.value,
    dur: artDuration.value,
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    id: props.currentChap!,
  })
  console.log("save viewing progress")
}, 3_000)
function onVideoTimeUpdate() {
  if (
    artPlaying.value &&
    !currentingTime.value &&
    artControlShow.value &&
    !showMenuQuality.value &&
    !showMenuPlaybackRate.value &&
    artVolumeOutside.value &&
    !artControlProgressHoving.value &&
    Date.now() - activeTime >= 3e3
  ) {
    artControlShow.value = false
  }
  saveCurTimeToPer()
}
function onVideoError(event: Event) {
  if (!(event.target as HTMLVideoElement).error) return

  console.log("video error ", (event.target as HTMLVideoElement).error)

  $q.notify({
    message: t("da-gap-su-co-khi-phat-lai"),
    position: "bottom-right",
    timeout: 0,
    actions: [
      {
        label: t("thu-lai"),
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
        label: t("remount"),
        color: "white",
        handler: remount,
      },
    ],
  })
}
function onVideoEnded() {
  artEnded = true
  if (props.nextChap && settingsStore.player.autoNext) {
    addNotice(
      props.currentSeason !== props.nextChap.season.value
        ? t("dang-phat-season-_season", [props.nextChap.season.name])
        : t("dang-phat-tap-tiep-theo")
    )

    router.push(
      `/phim/${props.nextChap.season.value}/${
        props.nextChap.chap
          ? parseChapName(props.nextChap.chap.name) +
            "-" +
            props.nextChap.chap?.id
          : ""
      }`
    )
  }
}

// eslint-disable-next-line functional/no-let
let artPlayingOfBeforeDocumentHide: boolean
watch(documentVisibility, (visibility) => {
  console.log("document %s", visibility)
  if (visibility === "visible") {
    if (!artPlaying.value && (artPlayingOfBeforeDocumentHide ?? true))
      setArtPlaying(true)
  } else {
    artPlayingOfBeforeDocumentHide = artPlaying.value
  }
})

{
  const { resume, pause } = useIntervalFn(() => {
    if (!artPlaying.value) return

    setArtPlaying(false)

    $q.dialog({
      title: t("xac-nhan"),
      message: t("ban-van-dang-xem-chu"),
      cancel: true,
      persistent: false,
    })
      .onOk(() => {
        setArtPlaying(true)
      })
      .onDismiss(() => {
        setArtPlaying(true)
      })
      .onCancel(() => {
        console.warn("cancel continue play")
      })
  }, 1 /* hours */ * 3600_000)

  const resumeDelay = debounce(() => {
    if (settingsStore.player.enableRemindStop) resume()
  }, 1_000)
  watch(
    () => settingsStore.player.enableRemindStop,
    (enabled) => {
      if (!enabled) {
        resumeDelay.cancel()
        pause()
      } else {
        resumeDelay()
      }
    }
  )
  ;[
    "mousedown",
    "mouseup",
    "mousemove",
    "keydown",
    "touchstart",
    "touchmove",
    "touchend",
    "scroll",
  ].forEach((name) => {
    useEventListener(window, name, resumeDelay)
  })
  watch(documentVisibility, resumeDelay)
}
function runRemount() {
  $q.dialog({
    title: t("relay-change"),
    message: t("ban-dang-muon-doi-relay-khac"),
    cancel: true,
    persistent: false,
  }).onOk(remount)
}
// eslint-disable-next-line functional/no-let
let currentHls: Hlsjs
onBeforeUnmount(() => currentHls?.destroy())
function remount() {
  currentHls?.destroy()

  if (!currentStream.value) {
    $q.notify({
      position: "bottom-right",
      message: t("video-tam-thoi-khong-kha-dung"),
    })

    return
  }

  const { url, type } = currentStream.value

  const currentTime = artCurrentTime.value
  const playing = artPlaying.value || artEnded
  artEnded = false

  switch (type) {
    case "hls":
    case "m3u":
      // eslint-disable-next-line no-case-declarations
      const hls = new Hls({
        debug: import.meta.env.isDev,
        progressive: true,
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        pLoader: class CustomLoader extends (Hls.DefaultConfig.loader as any) {
          loadInternal(): void {
            const { config, context } = this
            if (!config) {
              return
            }

            const { stats } = this
            stats.loading.first = 0
            stats.loaded = 0

            const controller = new AbortController()
            const xhr = (this.loader = {
              readyState: 0,
              status: 0,
              abort() {
                controller.abort()
              },
              onreadystatechange: <(() => void) | null>null,
              onprogress: <
                ((eventt: { loaded: number; total: number }) => void) | null
              >null,
              response: <ArrayBuffer | null>null,
              responseText: <string | null>null,
            })
            const headers = new Headers()
            if (this.context.headers)
              for (const [key, val] of Object.entries(this.context.headers))
                headers.set(key, val as string)

            if (context.rangeEnd) {
              headers.set(
                "Range",
                "bytes=" + context.rangeStart + "-" + (context.rangeEnd - 1)
              )
            }

            xhr.onreadystatechange = this.readystatechange.bind(this)
            xhr.onprogress = this.loadprogress.bind(this)
            self.clearTimeout(this.requestTimeout)
            this.requestTimeout = self.setTimeout(
              this.loadtimeout.bind(this),
              config.timeout
            )

            fetchJava(context.url, {
              headers,
              signal: controller.signal,
            })
              .then(async (res) => {
                // eslint-disable-next-line functional/no-let
                let byteLength: number
                if (context.responseType === "arraybuffer") {
                  xhr.response = await res.arrayBuffer()
                  byteLength = xhr.response.byteLength
                } else {
                  xhr.responseText = await res.text()
                  byteLength = xhr.responseText.length
                }

                xhr.readyState = 4
                xhr.status = 200

                xhr.onprogress?.({
                  loaded: byteLength,
                  total: byteLength,
                })
                // eslint-disable-next-line promise/always-return
                xhr.onreadystatechange?.()
              })
              .catch((e) => {
                // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
                this.callbacks!.onError(
                  { code: xhr.status, text: e.message },
                  context,
                  xhr
                )
              })
          }
        } as unknown as PlaylistLoaderConstructor,
      })
      currentHls = hls
      // customLoader(hls.config)
      hls.loadSource(url)
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      hls.attachMedia(video.value!)
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      hls.on((Hls as unknown as any).Events.MANIFEST_PARSED, () => {
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

      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      if ((Hls as unknown as any).isSupported()) {
        remount()
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

const currentingTime = ref(false)
const progressInnerRef = ref<HTMLDivElement>()

const artCurrentTimeHoving = ref(0)
const artControlProgressHoving = ref(false)

// eslint-disable-next-line functional/no-let
let bindedMouseUp = false

function onIndicatorMove(
  event: TouchEvent | MouseEvent,
  innerEl?: HTMLDivElement
): void
// eslint-disable-next-line no-redeclare
function onIndicatorMove(
  event: TouchEvent | MouseEvent,
  innerEl: HTMLDivElement,
  offsetX: number,
  curTimeStart: number
): void

// eslint-disable-next-line no-redeclare
function onIndicatorMove(
  event: TouchEvent | MouseEvent,
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  innerEl: HTMLDivElement = progressInnerRef.value!,
  offsetX?: number,
  curTimeStart?: number
) {
  if (event.type.startsWith("mouse") && !bindedMouseUp) {
    bindedMouseUp = true
  }

  const maxX = innerEl.offsetWidth
  const { left } = innerEl.getBoundingClientRect()

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
    artCurrentTimeHoving.value = Math.max(
      0,
      Math.min(
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        video.value!.duration,
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        curTimeStart! + (video.value!.duration * clientX) / maxX
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
    artCurrentTimeHoving.value = (video.value!.duration * clientX) / maxX
  }

  if (event.type === "mousemove" && (event as MouseEvent).buttons !== 1) return

  setArtCurrentTime(artCurrentTimeHoving.value)
  currentingTime.value = true

  activeTime = Date.now()
}
function onIndicatorEnd() {
  currentingTime.value = false

  setArtCurrentTime(artCurrentTimeHoving.value)
  activeTime = Date.now()
}
useEventListener(window, "mouseup", () => {
  if (currentingTime.value) {
    currentingTime.value = false
    // setArtCurrentTime(artCurrentTimeHoving.value)
    activeTime = Date.now()
  }
})

// ==== addons swipe backdrop ====
// eslint-disable-next-line functional/no-let, no-undef
let timeoutHoldBD: number | NodeJS.Timeout | null = null

const holdedBD = ref(false)
// eslint-disable-next-line functional/no-let
let xStart: number | null = null
// eslint-disable-next-line functional/no-let
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
    navigator.vibrate?.(90)
  }, 400)
}
function onBDTouchMove(event: TouchEvent) {
  if (timeoutHoldBD) {
    clearTimeout(timeoutHoldBD)
    timeoutHoldBD = null
  }

  if (holdedBD.value) {
    console.log("bd move")
    onIndicatorMove(
      event,
      event.target as HTMLDivElement,
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      xStart!,
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      curTimeStart!
    )
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

function skipBack() {
  setArtCurrentTime(
    (artCurrentTime.value = Math.max(0, artCurrentTime.value - 10))
  )
}
function skipForward() {
  setArtCurrentTime(
    (artCurrentTime.value = Math.min(
      artCurrentTime.value + 10,
      artDuration.value
    ))
  )
}

const doubleClicking = ref<"left" | "right" | false>(false)
// eslint-disable-next-line functional/no-let, no-undef
let timeoutResetDoubleClicking: number | NodeJS.Timeout | null = null
// eslint-disable-next-line functional/no-let
let lastTimeClick: number
// eslint-disable-next-line functional/no-let
let lastPositionClickIsLeft: boolean | null = null
// eslint-disable-next-line functional/no-let, no-undef
let timeoutDbClick: number | NodeJS.Timeout | null = null
const countSkip = ref(0)
function onClickSkip(event: MouseEvent) {
  // click
  const now = Date.now()

  const isLeft =
    Math.round(event.clientX) <
    Math.round((event.target as HTMLDivElement).offsetWidth / 2)

  if (
    (lastPositionClickIsLeft === null || lastPositionClickIsLeft === isLeft) &&
    now - lastTimeClick <= 300
  ) {
    // is double click
    timeoutDbClick && clearTimeout(timeoutDbClick)

    if (artControlShow.value) activeTime = Date.now() // fix for if control show continue show

    // on double click
    doubleClicking.value = isLeft ? "left" : "right"
    timeoutResetDoubleClicking && clearTimeout(timeoutResetDoubleClicking)
    timeoutResetDoubleClicking = setTimeout(() => {
      doubleClicking.value = false
      countSkip.value = 0
    }, 700)

    if (isLeft) {
      // previous 10s
      skipBack()
      countSkip.value++
    } else {
      // next 10s
      skipForward()
      countSkip.value++
    }
    // soku
  } else {
    timeoutDbClick && clearTimeout(timeoutDbClick)
    timeoutDbClick = setTimeout(() => {
      // setArtControlShow(orFalse)
      setArtPlaying(!artPlaying.value)
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
  if (notices.length > 3) notices.splice(0, notices.length - 3)

  setTimeout(() => {
    notices.splice(notices.findIndex((item) => item.id === uuid) >>> 0, 1)
  }, 5000)
}

const wrapVolumeRef = ref<HTMLDivElement>()
const { isOutside: artVolumeOutside } = useMouseInElement(wrapVolumeRef)

const showDialogChapter = ref(false)

watch(showDialogChapter, (status) => {
  if (!status) seasonActive.value = props.currentSeason
})

const showMenuQuality = ref(false)
const showMenuPlaybackRate = ref(false)

function upVolume() {
  if (artVolume.value < 1) {
    const newVal = Math.min(1, artVolume.value + 0.05)
    setArtVolume(newVal)
    addNotice(t("am-luong-_volume", [Math.round(newVal * 100)]))
  }
}
function downVolume() {
  if (artVolume.value > 0) {
    const newVal = Math.max(0, artVolume.value - 0.05)
    setArtVolume(newVal)
    addNotice(t("am-luong-_volume", [Math.round(newVal * 100)]))
  }
}
function skipOpening() {
  setArtCurrentTime(
    (artCurrentTime.value = Math.min(
      artCurrentTime.value + 90,
      artDuration.value
    ))
  )
}
useEventListener(window, "keydown", (event: KeyboardEvent) => {
  switch (event.code) {
    case "Space":
    case "KeyK": {
      if (!checkContentEditable(event.target as Element | null))
        event.preventDefault()
      const playing = artPlaying.value
      setArtPlaying(!playing)
      if (playing) setArtControlShow(true)
      // setArtControlShow(playing)
      break
    }
    case "KeyF":
      toggleArtFullscreen()
      break
    case "ArrowLeft":
      skipBack()
      break
    case "ArrowRight":
      skipForward()
      break

    case "ArrowUp":
      if (artFullscreen.value) upVolume()
      break
    case "ArrowDown":
      if (artFullscreen.value) downVolume()
      break

    case "KeyM":
      toggleMuted()
      break
    case "KeyN":
      if (event.shiftKey && props.nextChap)
        router.push(
          `/phim/${props.nextChap.season.value}/${
            props.nextChap.chap
              ? parseChapName(props.nextChap.chap.name) +
                "-" +
                props.nextChap.chap?.id
              : ""
          }`
        )

      break
    case "KeyJ":
      skipOpening()
      break
  }
})

// eslint-disable-next-line functional/no-let
let _tmp:
  | ResponseDataSeasonPending
  | ResponseDataSeasonSuccess
  | ResponseDataSeasonError
  | undefined
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
    opacity: 1;
    visibility: visible;
    transition: all 0.2s ease-in-out;
  }
  &.currenting-time {
    @media (hover: none) {
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

      img {
        transition: transform 0.22s ease-in-out;
      }
      .art-control-progress-inner {
        transition: height 0.22s ease-in-out;
      }

      &:hover {
        img {
          transform: scale(1.35);
        }
        .art-control-progress-inner {
          height: 5px;
        }
      }

      .art-control-progress-inner {
        display: flex;
        align-items: center;
        position: relative;
        height: 3px;
        width: 100%;
        background: rgba(255, 255, 255, 0.2);

        .art-progress-loaded,
        .art-progress-hoved {
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

        .art-progress-hoved {
          background: rgba($color: #fff, $alpha: 0.5);
          &:after {
            content: attr(data-title);
            @apply absolute right-0 bottom-[100%] transform translate-x--1/2 translate-y-[-16px];
            background: rgba(0, 0, 0, 0.7);
            @apply py-2 px-3 font-weight-medium rounded-lg;
          }
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

        @media (min-width: $breakpoint-sm-min) {
          font-size: 14px;
        }

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

  .art-controls-main {
    white-space: nowrap;
    width: 100%;
    max-width: 720px;
    text-align: center;
    // 20
    .prev {
      margin-right: 20%;
      transform: translateX(100%);
    }
    .next {
      margin-left: 20%;
      transform: translateX(-100%);
      //margin-left: 40px
    }

    .prev,
    .next {
      display: none;
    }
  }

  .art-volume.active {
    .slider {
      @apply px-[20px] mx-[-20px];
      width: (60px + 40px);
    }
  }
}

.only-fscrn {
  display: none !important;
}

.player__wrap.fullscreen {
  .only-fscrn {
    display: block !important;
  }
  .hide-fscrn {
    display: none;
  }

  width: 100vw !important;
  height: 100vh !important;

  max-height: 100vh !important;
  .toolbar-top,
  .toolbar-bottom {
    padding: {
      left: 58px !important;
      right: 58px !important;
    }
  }

  .toolbar-top {
    padding-top: 50px !important;
  }
  .toolbar-bottom {
    padding-bottom: 50px !important;

    .art-more-controls {
      display: flex;
    }

    .art-control-progress {
      .art-control-progress-inner {
        // height: 4px;
      }
    }
  }

  .art-controls-main {
    .prev,
    .next {
      display: inline-block;
    }
  }

  .art-btn .art-icon {
    @media (min-width: $breakpoint-sm-min) {
      height: 30px;
      width: 30px;
    }
  }

  .art-title {
    font-size: 25px;
  }
  .art-subtitle {
    font-size: 20px;
  }
  .art-control-onlyText {
    font-size: 18px;
  }
}

.player__wrap {
  .toolbar-top,
  .toolbar-bottom {
    padding: {
      left: 44px !important;
      right: 44px !important;
    }
  }

  .toolbar-top {
    padding-top: 32px !important;
  }
  .toolbar-bottom {
    padding-bottom: 32px !important;

    .art-more-controls {
      display: flex;
    }

    .art-control-progress {
      .art-control-progress-inner {
        // height: 4px;
      }
    }
  }

  .art-controls-main {
    .prev,
    .next {
      display: inline-block;
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
      @media (min-width: $breakpoint-sm-min) {
        height: 25px;
        width: 25px;
      }
    }
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

<style lang="scss" scoped>
.art-layer-controller :deep(.q-btn .q-focus-helper) {
  display: none;
}

@import "src/pages/phim/tabs-seasons.scss";

.grid-mode {
  @apply absolute;
  height: calc(100% - 41.59px) !important;
}
</style>
