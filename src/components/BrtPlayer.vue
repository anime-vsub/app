<template>
  <div
    class="w-full overflow-hidden fixed top-0 left-0 z-200 bg-[#000]"
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

      <!-- backdrop show skip icon -->
      <div
        v-if="doubleClicking"
        class="absolute top-0 left-0 w-full h-full pointer-events-none flex items-center justify-center text-[16px]"
      >
        <div
          v-if="doubleClicking === 'left'"
          class="flex items-center bg-[rgba(0,0,0,.5)] py-1 px-2 rounded-md"
        >
          <Icon
            icon="fluent:fast-forward-24-regular"
            :rotate="2"
            width="24"
            height="24"
            class="mr-1"
          />
          -{{ countSkip * 10 }}
        </div>

        <div
          v-else
          class="flex items-center bg-[rgba(0,0,0,.5)] py-1 px-2 rounded-md"
        >
          +{{ countSkip * 10 }}
          <Icon
            icon="fluent:fast-forward-24-regular"
            width="24"
            height="24"
            class="ml-1"
          />
        </div>
      </div>

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
                <div
                  class="line-clamp-1 text-[18px] text-weight-medium leading-normal"
                >
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

          <div class="toolbar-bottom">
            <div class="art-more-controls flex items-center justify-between">
              <div class="flex items-center">
                <q-btn
                  dense
                  flat
                  no-caps
                  class="mr-6 text-weight-normal art-btn"
                  :disable="!nextChap"
                  replace
                  :to="{
                    name: 'watch-anime',
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
                  @click="showDialogServer = true"
                >
                  <Icon
                    icon="solar:server-square-broken"
                    class="mr-2 art-icon"
                    width="18"
                    height="18"
                  />
                  {{ settingsStore.player.server }}
                </q-btn>
                <q-btn
                  dense
                  flat
                  no-caps
                  class="mr-6 text-weight-normal art-btn"
                  @click="showDialogQuality = true"
                >
                  <Icon
                    icon="solar:high-quality-broken"
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
                    width: percentageResourceLoadedText,
                  }"
                />
                <div
                  class="art-progress-played"
                  :style="{
                    width: percentagePlaytimeText,
                  }"
                >
                  <div
                    class="absolute w-[20px] h-[20px] right-[-10px] top-[calc(100%-10px)] art-progress-indicator"
                    :data-title="playtimeText"
                    @touchstart.stop="currentingTime = true"
                    @touchmove.stop="onIndicatorMove"
                    @touchend.stop="onIndicatorEnd"
                  >
                    <img width="16" heigth="16" src="~assets/indicator.svg" />
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
                  {{ playtimeText }} /
                  {{ durationText }}
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
        v-if="!sources || artLoading"
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
        <div class="text-zinc-500 text-[12px] mb-2">Máy chủ phát</div>
        <div>
          <q-btn
            dense
            flat
            no-caps
            class="px-2 flex-1 text-weight-norrmal py-2 rounded-xl"
            v-for="(label, id) in servers"
            :class="{
              'c--main': settingsStore.player.server === id,
            }"
            :key="label"
            @click="settingsStore.player.server = id"
            >{{ label }}</q-btn
          >
        </div>

        <div class="text-zinc-500 text-[12px] mt-4 mb-2">Chất lượng</div>
        <div>
          <q-btn
            dense
            flat
            no-caps
            class="px-2 flex-1 text-weight-norrmal py-2 rounded-xl"
            v-for="{ label, qualityCode } in sources"
            :class="{
              'c--main': qualityCode === artQuality,
            }"
            :key="label"
            @click="setArtQuality(qualityCode)"
            >{{ label }}</q-btn
          >
        </div>

        <div class="text-zinc-500 text-[12px] mt-4 mb-2">Tốc độ phát lại</div>
        <div class="flex flex-nowrap mx-[-8px]">
          <q-btn
            dense
            flat
            no-caps
            class="flex-1 text-weight-norrmal text-[13px] py-2"
            v-for="{ name, value } in playbackRates"
            :key="name"
            :class="{
              'c--main': artPlaybackRate === value,
            }"
            @click="setArtPlaybackRate(value)"
            >{{ name }}</q-btn
          >
        </div>

        <div class="flex items-center justify-between mt-4 mb-2">
          Tự động phát
          <q-toggle
            v-model="settingsStore.player.autoNext"
            size="sm"
            color="green"
          />
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
              class="bg-[#2a2a2a] mx-1 rounded-sm !min-h-0 py-[3px]"
              content-class="children:!font-normal children:!text-[13px] children:!min-h-0"
              :ref="(el: QTab) => void(item.value === currentSeason && (tabsRef = el as QTab))"
            />
          </q-tabs>

          <q-tab-panels
            v-model="seasonActive"
            animated
            keep-alive
            class="flex-1 mt-4 bg-transparent"
          >
            <q-tab-panel
              v-for="{ value } in seasons"
              :key="value"
              :name="value"
              class="h-full py-0 !px-0 flex justify-around place-items-center place-content-start relative overflow-y-auto pb-3"
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
                  rounded
                  @click="fetchSeason(value)"
                  >Thử lại</q-btn
                >
              </div>
              <ChapsGridQBtn
                v-else
                class-item="!px-3 !py-2 !mx-1"
                class-active="!bg-[rgba(0,194,52,.15)]"
                grid
                :chaps="(_cacheDataSeasons.get(value) as ResponseDataSeasonSuccess | undefined)?.response.chaps"
                :season="value"
                :find="
                  (item) => value === currentSeason && item.id === currentChap
                "
                :progress-chaps="
                                  (progressWatchStore.get(value) as unknown as any)?.response
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
            v-for="{ label, qualityCode } in sources"
            :key="label"
            class="py-2 text-center px-15"
            :class="{
              'c--main': qualityCode === artQuality,
            }"
            @click="setArtQuality(qualityCode)"
          >
            {{ label }}
          </li>
        </ul>
      </ArtDialog>
      <ArtDialog
        :model-value="artFullscreen && showDialogServer"
        @update:model-value="showDialogServer = $event"
        title="Máy chủ phát"
      >
        <ul>
          <li
            v-for="(label, id) in servers"
            :key="label"
            class="py-2 text-center px-15"
            :class="{
              'c--main': settingsStore.player.server === id,
            }"
            @click="settingsStore.player.server = id"
          >
            {{ label }}
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
    class="children:!px-0"
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

        <q-item clickable v-ripple @click="showDialogServer = true">
          <q-item-section avatar>
            <Icon icon="solar:server-square-broken" width="22" height="22" />
          </q-item-section>

          <q-item-section>
            <q-item-label>
              Máy chủ phát
              <span class="text-gray-200 mx-2">&bull;</span>

              {{ settingsStore.player.server }}
            </q-item-label>
          </q-item-section>
        </q-item>

        <q-item clickable v-ripple @click="showDialogQuality = true">
          <q-item-section avatar>
            <Icon icon="solar:high-quality-broken" width="22" height="22" />
          </q-item-section>

          <q-item-section>
            <q-item-label>
              Chất lượng
              <span class="text-gray-200 mx-2">&bull;</span>

              {{ artQuality }}
            </q-item-label>
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
    class="children:!px-0"
    full-width
  >
    <q-card flat class="w-full text-[16px]">
      <q-list>
        <q-item
          v-for="{ label, qualityCode } in sources"
          :key="label"
          clickable
          v-ripple
        >
          <q-item-section avatar>
            <q-icon v-if="artQuality === qualityCode" name="check" />
          </q-item-section>
          <q-item-section>
            {{ label }}
          </q-item-section>
        </q-item>
      </q-list>
    </q-card>
  </q-dialog>
  <!-- server -->
  <q-dialog
    :model-value="!artFullscreen && showDialogServer"
    @update:model-value="showDialogServer = $event"
    position="bottom"
    class="children:!px-0"
    full-width
  >
    <q-card flat class="w-full text-[16px]">
      <q-list>
        <q-item
          v-for="(label, id) in servers"
          :key="label"
          clickable
          v-ripple
          @click="settingsStore.player.server = id"
        >
          <q-item-section avatar>
            <q-icon v-if="settingsStore.player.server === id" name="check" />
          </q-item-section>
          <q-item-section>
            {{ label }}
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
    class="children:!px-0"
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
import { Haptics } from "@capacitor/haptics"
import { StatusBar } from "@capacitor/status-bar"
import { NavigationBar } from "@hugotomazi/capacitor-navigation-bar"
import { Icon } from "@iconify/vue"
import ArtDialog from "components/ArtDialog.vue"
import ChapsGridQBtn from "components/ChapsGridQBtn.vue"
import type { PlaylistLoaderConstructor } from "hls.js"
import Hls from "hls.js"
import workerHls from "hls.js/dist/hls.worker?url"
import {
  QBtn,
  QCard,
  QDialog,
  QIcon,
  QItem,
  QItemLabel,
  QItemSection,
  QList,
  QResponsive,
  QSpinner,
  QSpinnerInfinity,
  QTab,
  QTabPanel,
  QTabPanels,
  QTabs,
  QToggle,
  useQuasar,
} from "quasar"
import type { PlayerLink } from "src/apis/runs/ajax/player-link"
import { useMemoControl } from "src/composibles/memo-control"
import {
  C_URL,
  DELAY_SAVE_HISTORY,
  DELAY_SAVE_VIEWING_PROGRESS,
  playbackRates,
  servers,
} from "src/constants"
import { scrollXIntoView } from "src/helpers/scrollIntoView"
import { fetchJava } from "src/logic/fetchJava"
import { patcher } from "src/logic/hls-patcher"
import { parseTime } from "src/logic/parseTime"
import { ResponseDataSeasonSuccess } from "src/pages/phim/_season.interface"
import type {
  ProgressWatchStore,
  ResponseDataSeasonError,
  ResponseDataSeasonPending,
  Season,
} from "src/pages/phim/_season.interface"
import { useAuthStore } from "stores/auth"
import { useHistoryStore } from "stores/history"
import { useSettingsStore } from "stores/settings"
import { useStateStorageStore } from "stores/state"
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

const authStore = useAuthStore()
const settingsStore = useSettingsStore()
const historyStore = useHistoryStore()
const stateStorageStore = useStateStorageStore()

const router = useRouter()
const $q = useQuasar()
const { t } = useI18n()

const props = defineProps<{
  sources?: Awaited<ReturnType<typeof PlayerLink>>["link"]
  currentSeason: string
  nameCurrentSeason?: string
  currentChap?: string
  nameCurrentChap?: string
  nextChap?: {
    season: string
    chap?: string
  }
  name?: string
  poster?: string
  seasons?: Season[]
  _cacheDataSeasons: Map<
    string,
    | ResponseDataSeasonPending
    | ResponseDataSeasonSuccess
    | ResponseDataSeasonError
  >
  fetchSeason: (season: string) => Promise<void>
  progressWatchStore: ProgressWatchStore
}>()
const uidChap = computed(() => {
  const uid = `${props.currentSeason}/${props.currentChap ?? ""}` // 255 byte

  return uid
})

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
    if (tabsRef.value?.$el) scrollXIntoView(tabsRef.value.$el)
  }, 70)
})

// =========================== huuuu player API。馬鹿馬鹿しい ====================================

const currentStream = computed(() => {
  return props.sources?.find((item) => item.qualityCode === artQuality.value)
})
if (import.meta.env.DEV)
  watch(
    () => props.sources,
    (sources) => {
      console.log("sources changed: ", sources)
    }
  )

const video = ref<HTMLVideoElement>()
// value control get play
const artPlaying = ref(false)
const setArtPlaying = (playing: boolean) => {
  if (!video.value) {
    console.log("video element not ready")
    return
  }
  artPlaying.value = playing
  if (playing) {
    // video.value.load();
    if (video.value.paused) video.value.play()
  } else {
    if (!video.value.paused) video.value.pause()
  }
}
watch(
  () => props.currentChap,
  () => setArtPlaying(true),
  { immediate: true }
)
// eslint-disable-next-line functional/no-let
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
  artCurrentTime.value = currentTime
}
// eslint-disable-next-line functional/no-let
let progressRestored: false | string = false
watch(
  [() => props.currentChap, () => props.currentSeason, () => authStore.uid],
  async ([currentChap, currentSeason, uid]) => {
    if (currentChap && currentSeason) {
      progressRestored = false

      if (!uid) {
        // not login
        setArtCurrentTime(0)
        return
      }

      try {
        if (stateStorageStore.disableAutoRestoration > 0) {
          addNotice(t("bo-qua-khoi-phuc-tien-trinh-xem"))
          // eslint-disable-next-line functional/no-throw-statement
          throw new Error("NOT_RESET")
        }
        console.log(":restore progress")
        const cur = (
          await historyStore.getProgressChap(currentSeason, currentChap)
        )?.cur

        if (cur && !holdedBD.value) {
          setArtCurrentTime(cur)
          addNotice(t("da-khoi-phuc-phien-xem-truoc-_time", [parseTime(cur)]))
        } else {
          // eslint-disable-next-line functional/no-throw-statement
          throw new Error("NOT_RESET")
        }
      } catch (err) {
        setArtCurrentTime(0)
        if ((err as Error)?.message !== "NOT_RESET") console.error(err)
      }

      progressRestored = uidChap.value
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

const _artQuality =
  ref<Awaited<ReturnType<typeof PlayerLink>>["link"][0]["qualityCode"]>()
const artQuality = computed({
  get() {
    if (props.sources?.find((item) => item.qualityCode === _artQuality.value))
      return _artQuality.value

    return props.sources?.[0]?.qualityCode
  },
  set(value) {
    _artQuality.value = value
  },
})
const setArtQuality = (value: Exclude<typeof artQuality.value, undefined>) => {
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

const seasonMetaCreated = new Set<string>()

async function createSeason(
  currentSeason: string,
  seasonName: string,
  poster: string,
  name: string
): Promise<boolean> {
  // eslint-disable-next-line camelcase
  const { user_data } = authStore

  if (seasonMetaCreated.has(currentSeason)) return true

  if (
    // eslint-disable-next-line camelcase
    !user_data
  )
    return false
  console.log("set new season poster %s", poster)
  await historyStore.createSeason(currentSeason, {
    poster,
    seasonName,
    name,
  })
  seasonMetaCreated.add(currentSeason)
  return true
}

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

const firstSaveStore = new Set<string>()
// eslint-disable-next-line @typescript-eslint/no-explicit-any
function throttle<T extends (...args: any[]) => void>(
  fn: Promise<T>
): T & {
  cancel: () => void
} {
  // eslint-disable-next-line functional/no-let
  let wait = false

  // eslint-disable-next-line functional/no-let, no-undef
  let timeout: NodeJS.Timeout | number | undefined
  // eslint-disable-next-line functional/functional-parameters, @typescript-eslint/no-explicit-any
  const cb = function (...args: any[]) {
    if (wait === false) {
      wait = true
      timeout = setTimeout(
        async () => {
          firstSaveStore.add(uidChap.value)
          // eslint-disable-next-line no-void
          await fn(...args).catch(() => void 0)
          wait = false
        },
        firstSaveStore.has(uidChap.value)
          ? DELAY_SAVE_VIEWING_PROGRESS
          : DELAY_SAVE_HISTORY
      )
    }
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
  } as any

  cb.cancel = () => {
    clearTimeout(timeout)
    wait = false
  }

  return cb
}

const savingTimeEpStore = new Set<string>()
const saveCurTimeToPer = throttle(
  async (
    currentSeason: string,
    currentChap: string,
    nameCurrentChap: string,
    poster: string,
    name: string
  ) => {
    console.log("call main fn cur time")
    const uidTask = uidChap.value

    if (savingTimeEpStore.has(uidTask)) {
      if (import.meta.env.DEV) console.warn("Task saving %s exists", uidTask)

      return
    }

    savingTimeEpStore.add(uidTask)

    // get data from uid and process because processingSaveCurTimeIn === uid then load all of time current
    // eslint-disable-next-line functional/no-let
    let cur = artCurrentTime.value
    // eslint-disable-next-line functional/no-let
    let dur = artDuration.value

    if (!dur) {
      console.warn("[saveCurTime]: artDuration is %s", dur)
      return
    }

    if (!(await createSeason(currentSeason, nameCurrentChap, poster, name)))
      return

    // NOTE: if this uid (processingSaveCurTimeIn === uid) -> update cur and dur
    if (uidTask === uidChap.value) {
      // update value now
      cur = artCurrentTime.value
      dur = artDuration.value
    } else {
      // because changed ep -> use old data not change
    }

    if (stateStorageStore.disableAutoRestoration === 2) return

    console.log("%ccall sav curTime", "color: green")

    if (!dur) {
      console.warn("[saveCurTime]: artDuration is %s", dur)
      return
    }
    
    emit("cur-update", {
      cur,
      dur,
      id: currentChap,
    })
   
    historyStore
      .setProgressChap(
        currentSeason,
        currentChap,
        {
          cur,
          dur,
          name: nameCurrentChap,
        },
        {
          poster,
          seasonName: nameCurrentChap,
          name,
        }
      )
      .catch((err) => console.warn("save viewing progress failed: ", err))

    console.log("save viewing progress")

    savingTimeEpStore.delete(uidTask)
  }
)
watch(uidChap, saveCurTimeToPer.cancel)
function onVideoTimeUpdate() {
  if (
    artPlaying.value &&
    !currentingTime.value &&
    artControlShow.value &&
    Date.now() - activeTime >= 3e3
  ) {
    artControlShow.value = false
  }

  if (progressRestored !== uidChap.value) return
  if (!props.currentChap) return
  if (typeof props.nameCurrentChap !== "string") return

  console.log("call throw emit")
  saveCurTimeToPer(
    props.currentSeason,
    props.currentChap,
    props.nameCurrentChap,
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    props.poster!,
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    props.name!
  )
}
// function onVideoError(event: Event) {
//   console.log("video error ", event)

//   $q.notify({
//     message: "Đã gặp sự cố khi phát lại",
//     position: "bottom-right",
//     timeout: 0,
//     actions: [
//       {
//         label: "Thử lại",
//         color: "white",
//         handler() {
//           console.log("retry force")
//           // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
//           video.value!.load()
//           // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
//           video.value!.play()
//         },
//       },
//       {
//         label: "Remount",
//         color: "white",
//         handler: remount,
//       },
//     ],
//   })
// }
function onVideoEnded() {
  artEnded = true
  if (props.nextChap && settingsStore.player.autoNext) {
    addNotice(
      props.currentSeason !== props.nextChap.season
        ? `Đang phát ${props.nextChap.season}`
        : "Đang phát tập tiếp theo"
    )

    router.push({
      name: "watch-anime",
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

// eslint-disable-next-line functional/no-let
let currentHls: Hls
onBeforeUnmount(() => currentHls?.destroy())
function remount(resetCurrentTime?: boolean, noDestroy = false) {
  if (!noDestroy) currentHls?.destroy()
  else {
    const type = currentStream.value?.type

    if (
      (type === "hls" || type === "m3u" || type === "m3u8") &&
      Hls.isSupported()
    ) {
      // current stream is HLS -> no cancel if canPlay
    } else {
      console.warn("can't play HLS stream")
      // cancel
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      const currentVideo = video.value!
      currentVideo.oncanplay = function () {
        const { src } = currentVideo
        currentHls?.destroy()
        if (currentVideo.src !== src) currentVideo.src = src
        currentVideo.oncanplay = null
      }
    }
  }

  if (!currentStream.value) {
    $q.notify({
      position: "bottom-right",
      message: t("video-tam-thoi-khong-kha-dung"),
    })
    return
  }

  const { file, type } = currentStream.value

  const currentTime = artCurrentTime.value
  const playing = artPlaying.value || artEnded
  artEnded = false

  if (
    (type === "hls" || type === "m3u" || type === "m3u8") &&
    Hls.isSupported()
  ) {
    const hls = new Hls({
      debug: import.meta.env.isDev,
      workerPath: workerHls,
      progressive: true,
      fragLoadingRetryDelay: 10000,
      fetchSetup(context, initParams) {
        context.url += process.env.MODE === "spa" ? "#animevsub-vsub" : ""

        // set header because this version always cors not fix by extension liek desktop-web
        initParams.headers.set("x-referer", C_URL)

        return new Request(context.url, initParams)
      },
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
            responseType: context.responseType,
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
          const { maxTimeToFirstByteMs, maxLoadTimeMs } = config.loadPolicy

          if (context.rangeEnd) {
            headers.set(
              "Range",
              "bytes=" + context.rangeStart + "-" + (context.rangeEnd - 1)
            )
          }

          xhr.onreadystatechange = this.readystatechange.bind(this)
          xhr.onprogress = this.loadprogress.bind(this)
          self.clearTimeout(this.requestTimeout)
          config.timeout =
            maxTimeToFirstByteMs && Number.isFinite(maxTimeToFirstByteMs)
              ? maxTimeToFirstByteMs
              : maxLoadTimeMs
          this.requestTimeout = self.setTimeout(
            this.loadtimeout.bind(this),
            config.timeout
          )

          // set header because this version always cors not fix by extension liek desktop-web
          headers.set("referer", C_URL)

          fetchJava(
            context.url + (process.env.MODE === "spa" ? "#animevsub-vsub" : ""),
            {
              headers,
              signal: controller.signal,
            }
          )
            .then(async (res) => {
              // eslint-disable-next-line functional/no-let
              let byteLength: number
              if (context.responseType !== "text") {
                xhr.response = await res.arrayBuffer()
                // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
                byteLength = xhr.response!.byteLength
              } else {
                xhr.responseText = await res.text()
                byteLength = xhr.responseText.length
              }

              xhr.readyState = 4
              xhr.status = 200
              xhr.responseType = context.responseType

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
    patcher(hls)
    currentHls = hls
    // customLoader(hls.config)
    hls.loadSource(file)
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    hls.attachMedia(video.value!)
    hls.on(Hls.Events.MANIFEST_PARSED, () => {
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      if (playing) video.value!.play()
    })
    // eslint-disable-next-line functional/no-let
    let needSwapCodec = false
    // eslint-disable-next-line functional/no-let, no-undef
    let timeoutUnneedSwapCodec: NodeJS.Timeout | number | null = null
    hls.on(Hls.Events.ERROR, (event, data) => {
      if (data.fatal) {
        console.warn("Player fatal: ", data)
        switch (data.type) {
          case Hls.ErrorTypes.NETWORK_ERROR: {
            // try to recover network error
            $q.notify({
              message: t("loi-mang-khong-kha-dung"),
              position: "bottom-right",
              timeout: 0,
              actions: [
                {
                  label: t("thu-lai"),
                  color: "yellow",
                  noCaps: true,
                  handler: () => hls.startLoad(),
                },
                {
                  icon: "close",
                  round: true,
                },
              ],
            })
            break
          }
          case Hls.ErrorTypes.MEDIA_ERROR: {
            const playing = artPlaying.value
            if (timeoutUnneedSwapCodec) {
              clearTimeout(timeoutUnneedSwapCodec)
              timeoutUnneedSwapCodec = null
            }
            console.warn("fatal media error encountered, try to recover")
            if (needSwapCodec) {
              hls.swapAudioCodec()
              needSwapCodec = false
              if (timeoutUnneedSwapCodec) {
                clearTimeout(timeoutUnneedSwapCodec)
                timeoutUnneedSwapCodec = null
              }
            } else {
              needSwapCodec = true
              timeoutUnneedSwapCodec = setTimeout(() => {
                needSwapCodec = false
                timeoutUnneedSwapCodec = null
              }, 1_000)
            }
            hls.recoverMediaError()
            if (playing)
              // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
              video.value!.play()
            break
          }
          default: {
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
            break
          }
        }
      } else {
        console.warn("Player error: ", data)
      }
    })
  } else {
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    video.value!.src = file
  }

  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  if (playing) video.value!.play()

  if (
    resetCurrentTime
      ? props.currentChap && progressRestored === uidChap.value
      : true
  )
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    video.value!.currentTime = currentTime
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  else setArtCurrentTime((video.value!.currentTime = 0))
}
const watcherVideoTagReady = watch(video, (video) => {
  if (!video) return

  // eslint-disable-next-line promise/catch-or-return
  Promise.resolve().then(watcherVideoTagReady) // fix this not ready value

  // eslint-disable-next-line functional/no-let
  let currentEpStream: null | string = null
  watch(
    () => currentStream.value?.file,
    (url) => {
      if (!url) return

      console.log("set url art %s", url)

      // // eslint-disable-next-line @typescript-eslint/no-explicit-any
      // if ((Hls as unknown as any).isSupported()) {
      remount(
        currentEpStream !== uidChap.value,
        currentEpStream === uidChap.value
      )
      currentEpStream = uidChap.value
      // } else {
      //   const canPlay = video.canPlayType("application/vnd.apple.mpegurl")
      //   if (canPlay === "probably" || canPlay === "maybe") {
      //     video.src = url
      //   }
      // }
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
      artQuality.value = sources[0].qualityCode // not use setArtQuality because skip notify
    }
  },
  { immediate: true }
)

const playerWrapRef = ref<HTMLDivElement>()

const currentingTime = ref(false)
const progressInnerRef = ref<HTMLDivElement>()
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
  currentingTime.value = true

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
function onClickSkip(event: MouseEvent, orFalse: boolean) {
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
const showDialogServer = ref(false)

watch(showDialogChapter, (status) => {
  if (!status) seasonActive.value = props.currentSeason
})

// memo-control time and progress
const showArtLayerController = computed(
  () => holdedBD.value || artControlShow.value
)

const playtimeText = useMemoControl(() => {
  return parseTime(artCurrentTime.value)
}, showArtLayerController)
const durationText = useMemoControl(() => {
  return parseTime(artDuration.value)
}, showArtLayerController)
const percentageResourceLoadedText = useMemoControl(() => {
  return `${artPercentageResourceLoaded.value * 100}%`
}, showArtLayerController)
const percentagePlaytimeText = useMemoControl(() => {
  return `${(artCurrentTime.value / artDuration.value) * 100}%`
}, showArtLayerController)
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
      @media (orientation: landscape) {
        display: inline-block;
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
