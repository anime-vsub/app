<template>
  <div class="w-full overflow-hidden" ref="playerWrapRef">
    <q-responsive :ratio="16 / 9" class="player__wrap">
      <div
        class="absolute top-0 left-0 w-full h-full z-98"
        @click="setArtControlShow(true)"
        v-show="!artControlShow"
      />
      <transition name="fade__ease-in-out">
        <div
          class="art-layer-controller z-99 overflow-hidden"
          :class="{
            'currenting-time': currentingTime,
          }"
          v-show="artControlShow"
          @click="setArtControlShow(false)"
        >
          <div class="toolbar-top">
            <div class="flex items-start w-max-[70%] flex-nowrap">
              <button class="back mr-2">
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

              <div class="">
                <div class="line-clamp-1 text-[18px] text-weight-medium">
                  {{ name }}
                </div>
                <div v-if="chapName" class="text-gray-300">
                  Tập
                  {{ chapName }}
                </div>
              </div>
            </div>
            <button @click="showDialogSetting = true">
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

          <button
            class="absolute top-[50%] left-[50%] transform translate-y-[-50%] translate-x-[-50%] art-control-playPause z-120"
            @click.stop
            @touchstart.prevent.stop="setArtPlaying(artPlaying)"
            @touchmove.prevent.stop
          >
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
            <div class="art-more-controls flex items-center justify-between">
              <div class="flex items-center">
                <q-btn
                  dense
                  flat
                  no-caps
                  class="mr-6 text-weight-normal art-btn"
                  @click.stop
                >
                  <Icon
                    icon="fluent:next-24-filled"
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
                    icon="fluent:list-24-filled"
                    class="mr-2 art-icon"
                    width="18"
                    height="18"
                  />
                  EP {{ chapName }}
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
              @touchstart.prevent.stop="onIndicatorMove"
              @touchmove.prevent.stop="onIndicatorMove"
              @touchend.prevent.stop="onIndicatorEnd"
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
                    @touchstart.prevent.stop="currentingTime = true"
                    @touchmove.prevent.stop="onIndicatorMove"
                    @touchend.prevent.stop="onIndicatorEnd"
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
                  {{ parseTime(artCurrentTime) }} / {{ parseTime(artDuration) }}
                </div>
              </div>
              <div class="art-controls-center"></div>
              <div class="art-controls-right">
                <div
                  class="art-control art-control-fullscreen hint--rounded hint--top"
                  data-index="70"
                  aria-label="Fullscreen"
                  @click="artFullscreen = !artFullscreen"
                >
                  <i
                    v-if="!artFullscreen"
                    class="art-icon art-icon-fullscreenOn"
                  >
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
                  <i v-else class="art-icon art-icon-fullscreenOff">
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

      <!-- notices -->
      <transition-group
        tag="div"
        name="notices"
        class="absolute z-101 top-0 left-0 w-full flex column justify-end ml-10 text-[14px] pointer-events-none"
      >
        <div v-for="item in notices" :key="item.id" class="pb-2 last:mb-36">
          <span
            class="text-[#fff] bg-[#0009] rounded-[3px] px-[16px] py-[10px] inline-block"
            >{{ item.text }}</span
          >
        </div>
      </transition-group>

      <!-- /notices -->

      <!-- dialogs -->
      <!--    dialog is settings    -->
      <ArtDialog v-model="showDialogSetting" title="Xem thêm" fit>
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
      <ArtDialog v-model="showDialogChapter" title="Danh sách tập" fit>
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
      <ArtDialog v-model="showDialogPlayback" title="Tốc độ">
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
      <ArtDialog v-model="showDialogQuality" title="Chất lượng">
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
      <div ref="playerRef" />
    </q-responsive>
    <video ref="video" controls class="z-200" />
  </div>
</template>

<script lang="ts" setup>
import { StatusBar } from "@capacitor/status-bar";
import { NavigationBar } from "@hugotomazi/capacitor-navigation-bar";
import { Icon } from "@iconify/vue";
import Artplayer from "artplayer";
import ArtDialog from "components/ArtDialog.vue";
import ChapsGridQBtn from "components/ChapsGridQBtn.vue";
import { QTab } from "quasar";
import type { PhimIdChap } from "src/apis/phim/[id]/[chap]";
import { playbackRates } from "src/constants";
import { Hls } from "src/logic/hls";
import { parseTime } from "src/logic/parseTime";
import {
  computed,
  onMounted,
  ref,
  shallowReactive,
  shallowRef,
  watch,
  watchEffect,
} from "vue";

interface ResponseDataSeasonPending {
  status: "pending";
}
interface ResponseDataSeasonSuccess {
  status: "success";
  response: Awaited<ReturnType<typeof PhimIdChap>>;
}
interface ResponseDataSeasonError {
  status: "error";
  response: {
    status: number;
  };
}

const props = defineProps<{
  sources?: { html: string; url: string }[];
  currentSeason?: string;
  currentChap?: string;
  name: string;
  chapName?: string;
  poster?: string;
  allSeasons?: {
    value: string;
    path: string;
    name: string;
  }[];
  _cacheDataSeasons: Map<
    string,
    | ResponseDataSeasonPending
    | ResponseDataSeasonSuccess
    | ResponseDataSeasonError
  >;
  fetchSeason: (season: string) => Promise<void>;
}>();
const currentStream = computed(() => {
  return props.sources?.find((item) => item.html === artQuality.value);
});

const video = ref<HTMLVideoElement>();
// value control get play
const artPlaying = ref(false);
const setArtPlaying = (playing: boolean) => {
  if (playing) {
    // video.value.load();
    video.value.play();
  } else {
    video.value.pause();
  }
};
const artDuration = ref<number>(0);
const artCurrentTime = ref<number>(0);
const setArtCurrentTime = (currentTime: number) => {
  video.value.currentTime = currentTime;
};
const artPercentageResourceLoaded = ref<number>(0);
const artPlaybackRate = ref(1);
const setArtPlaybackRate = (value: number) => {
  video.value.playbackRate = value;
  addNotice(`${value}x`);
};

// value control other
const artControlShow = ref(true);
let activeTime = Date.now();
const setArtControlShow = (show: boolean) => {
  artControlShow.value = show;
  if (show) activeTime = Date.now();
};
watch(
  artControlShow,
  (artControlShow) => artControlShow && artFullscreen.value && StatusBar.hide()
);
const artFullscreen = ref(false);
const setArtFullscreen = async (fullscreen: boolean) => {
  if (fullscreen) {
    await playerWrapRef.value.requestFullscreen();
    screen.orientation.lock("landscape");
    StatusBar.hide();
    NavigationBar.hide();
    StatusBar.setOverlaysWebView({
      overlay: true,
    });
  } else {
    // eslint-disable-next-line @typescript-eslint/no-empty-function
    await document.exitFullscreen().catch(() => {});
    screen.orientation.unlock();
    StatusBar.show();
    NavigationBar.show();
    StatusBar.setOverlaysWebView({
      overlay: false,
    });
  }
};
const artQuality = ref<string>();
const setArtQuality = (value: string) => {
  artQuality.value = value;
  addNotice(`Chất lượng đã chuyển sang ${value}`);
};
const hls = new Hls();

watch(video, (video) => {
  if (!video) return;

  video.addEventListener("play", () => (artPlaying.value = true));
  video.addEventListener("pause", () => (artPlaying.value = false));
  video.addEventListener(
    "durationchange",
    () => (artDuration.value = video.duration)
  );
  video.addEventListener(
    "timeupdate",
    () => (artCurrentTime.value = video.currentTime)
  );
  video.addEventListener("progress", () => {
    const target = event.target as HTMLVideoElement;
    // eslint-disable-next-line functional/no-let
    let range = 0;
    const bf = target.buffered;
    const time = target.currentTime;

    try {
      while (!(bf.start(range) <= time && time <= bf.end(range))) {
        range += 1;
      }
      // const loadStartPercentage = bf.start(range) / target.duration
      const loadEndPercentage = bf.end(range) / target.duration;
      // const loadPercentage = loadEndPercentage - loadStartPercentage

      artPercentageResourceLoaded.value = loadEndPercentage;
    } catch {
      try {
        artPercentageResourceLoaded.value = bf.end(0) / target.duration;
      } catch {}
    }
  });
  video.addEventListener(
    "ratechange",
    () => (artPlaybackRate.value = video.playbackRate)
  );
  playerWrapRef.value.addEventListener(
    "fullscreenchange",
    () =>
      (artFullscreen.value = document.fullscreenElement?.nodeName === "VIDEO")
  );
  // controls
  video.addEventListener("canplay", () => (activeTime = Date.now()));
  video.addEventListener("timeupdate", () => {
    if (
      artPlaying.value &&
      !currentingTime.value &&
      artControlShow.value &&
      Date.now() - activeTime >= 3e3
    ) {
      artControlShow.value = false;
    }
  });

  watch(
    () => currentStream.value?.url,
    (url) => {
      if (!url) return;

      console.log("set url art %s", url);

      if (Hls.isSupported()) {
        // customLoader(hls.config)
        hls.loadSource(url);
        hls.attachMedia(video);
      } else {
        const canPlay = video.canPlayType("application/vnd.apple.mpegurl");
        if (canPlay === "probably" || canPlay === "maybe") {
          video.src = url;
        }
      }
    },
    { immediate: true }
  );
});
const playerRef = ref<HTMLDivElement>();
watch(playerRef, (playerRef) => {
  if (!playerRef) return;

  const art = new Artplayer({
    container: playerRef,
    autoplay: true,
    url: "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
    customType: {
      m3u8: function (video, url) {
        if (Hls.isSupported()) {
          const hls = new Hls();
          hls.loadSource(url);
          hls.attachMedia(video);
        } else {
          const canPlay = video.canPlayType("application/vnd.apple.mpegurl");
          if (canPlay === "probably" || canPlay === "maybe") {
            video.src = url;
          } else {
            art.notice.show = "不支持播放格式：m3u8";
          }
        }
      },
    },
    volume: 0.5,
    isLive: false,
    muted: false,
    autoplay: false,
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
    moreVideoAttr: {
      crossOrigin: "anonymous",
    },
    icons: {
      loading: '<img src="/assets/img/ploading.gif">',
      state: '<img width="150" heigth="150" src="/assets/img/state.svg">',
      indicator: '<img width="16" heigth="16" src="/assets/img/indicator.svg">',
    },
    volume: 0.5,
    isLive: false,
    muted: false,
    autoplay: false,
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
    moreVideoAttr: {
      crossOrigin: "anonymous",
    },
    icons: {
      loading: '<img src="/assets/img/ploading.gif">',
      state: '<img width="150" heigth="150" src="/assets/img/state.svg">',
      indicator: '<img width="16" heigth="16" src="/assets/img/indicator.svg">',
    },
  });

  watch(
    () => currentStream.value?.url,
    (url) => {
      if (!url) return;

      console.log("set url art %s", url);
      art.url = url;
    },
    { immediate: true }
  );
});

// re-set quality if quality not in sources
watch(
  () => props.sources,
  (sources) => {
    if (!sources || sources.length === 0) return;
    // not ready quality on this
    if (!artQuality.value || !currentStream.value) {
      setArtQuality(sources[0].html);
    }
  },
  { immediate: true }
);

const playerWrapRef = ref<HTMLDivElement>();

const currentingTime = ref(false);
const progressInnerRef = ref<HTMLDivElement>();
function onIndicatorMove(event: TouchEvent | MouseEvent) {
  currentingTime.value = true;
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const maxX = progressInnerRef.value!.offsetWidth;

  const clientX = Math.min(
    maxX,
    Math.max(
      0,
      (
        (event as TouchEvent).changedTouches?.[0] ??
        (event as TouchEvent).touches?.[0] ??
        event
      ).clientX
    )
  );

  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  artCurrentTime.value = (video.value!.duration * clientX) / maxX;
  activeTime = Date.now();
}
function onIndicatorEnd() {
  currentingTime.value = false;

  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  setArtCurrentTime(artCurrentTime.value);
  activeTime = Date.now();
}

const notices = shallowReactive<
  {
    id: number;
    text: string;
  }[]
>([]);
// eslint-disable-next-line functional/no-let
let id = 1;
function addNotice(text: string) {
  const uuid = id++;
  notices.push({ id: uuid, text });
  setTimeout(() => {
    notices.splice(notices.findIndex((item) => item.id === uuid) >>> 0, 1);
  }, 5000);
}

// @scrollIntoView
const tabsRef = ref<QTab>();
watchEffect(() => {
  if (!tabsRef.value) return;
  if (!props.currentSeason) return;

  setTimeout(() => {
    tabsRef.value?.$el.scrollIntoView({
      inline: "center",
    });
  }, 70);
});
const seasonActive = ref<string>();
// sync data by active route
watch(
  () => props.currentSeason,
  (val) => (seasonActive.value = val),
  { immediate: true }
);

const showDialogSetting = ref(false);
const showDialogChapter = ref(false);
const showDialogPlayback = ref(false);
const showDialogQuality = ref(false);
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
  .art-control-playPause {
    padding: 8px;
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
    svg {
      width: 1.2rem;
      height: 1.2rem;
    }
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
    display: flex;
    flex-direction: column-reverse;
    // background-color: red;

    .art-more-controls {
      display: flex;
      margin-top: 16px;
    }

    .art-control-progress {
      position: relative;
      display: flex;
      flex-direction: row;
      align-items: center;
      padding: 16px 4px;
      cursor: pointer;

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
.art-notice,
.art-bottom,
.art-layer-lock {
  display: none !important;
}
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
    animation: fade__ease-in-out 0.44s ease-in-out;
  }
  &-leave-active {
    animation: fade__ease-in-out 0.44s ease-in-out reverse;
  }
}
</style>
