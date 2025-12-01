<template>
  <div
    class="w-full overflow-hidden fixed top-0 left-0 z-200 bg-[#000]"
    :class="{
      'h-full': IS_IOS && artFullscreen,
    }"
    ref="playerWrapRef"
  >
    <q-responsive :ratio="16 / 9" class="player__wrap">
      <video
        ref="video"
        :poster="poster + '#image'"
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
                  {{ engNameCurrentChap ? " - " + engNameCurrentChap : "" }}
                </div>
              </div>
            </div>
            <div class="flex items-end flex-nowrap">
              <q-btn
                dense
                flat
                round
                :disable="!MEDIA_STREAM_SUPPORT"
                @click.stop="openPopupFlashNetwork"
              >
                <i-ri-water-flash-line
                  v-if="settingsStore.player.preResolve === 0"
                  width="25"
                  height="25"
                />
                <i-ri-water-flash-fill
                  v-else
                  width="25"
                  height="25"
                  :class="`text-${
                    optionsPreResolve.find(
                      (item) => item.value === settingsStore.player.preResolve
                    )?.color
                  }`"
                />
                <q-tooltip
                  v-if="MEDIA_STREAM_SUPPORT"
                  anchor="bottom middle"
                  self="top middle"
                  class="bg-dark text-[14px] text-weight-medium"
                  transition-show="jump-up"
                  transition-hide="jump-down"
                >
                  {{ $t("tang-toc-mang") }}
                </q-tooltip>
                <q-tooltip
                  v-else
                  anchor="bottom middle"
                  self="top middle"
                  class="bg-dark text-[14px] text-weight-medium"
                  transition-show="jump-up"
                  transition-hide="jump-down"
                >
                  Phiên bản iOS không hỗ trợ tính năng này
                </q-tooltip>
              </q-btn>
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
                />
                <!-- seek thumbs -->
                <div
                  v-if="currentingTime"
                  class="art-sk-hoved"
                  :class="{
                    'art-sk-hoved--thumbs': sktStyle.thumbs,
                  }"
                  :data-title="
                    playtimeText +
                    (intro && inClamp(artCurrentTime, intro.start, intro.end)
                      ? ' Mở đầu'
                      : outro && inClamp(artCurrentTime, outro.start, outro.end)
                      ? ' Kết thúc'
                      : '')
                  "
                  :style="{ left: leftSkt + 'px' }"
                  ref="skRef"
                />
                <!-- /seek thumbs -->
                <div
                  class="absolute z-22 left-0 top-0 right-0 bottom-0 w-0 h-full pointer-events-none"
                  :style="{
                    width: percentagePlaytimeText,
                  }"
                >
                  <div
                    class="absolute w-[20px] h-[20px] right-[-10px] top-[calc(100%-10px)] art-progress-indicator"
                    @touchstart.stop="currentingTime = true"
                    @touchmove.stop="onIndicatorMove"
                    @touchend.stop="onIndicatorEnd"
                  >
                    <img width="16" heigth="16" src="~assets/indicator.svg" />
                  </div>
                </div>

                <div
                  v-if="intro"
                  class="absolute h-full bg-blue top-0 z-21"
                  :style="{
                    width: `${
                      ((intro.end - intro.start) / artDuration) * 100
                    }%`,
                    left: `${(intro.start / artDuration) * 100}%`,
                  }"
                />
                <div
                  v-if="outro"
                  class="absolute h-full bg-blue top-0 z-21"
                  :style="{
                    width: `${
                      ((outro.end - outro.start) / artDuration) * 100
                    }%`,
                    left: `${(outro.start / artDuration) * 100}%`,
                  }"
                />
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
      <!-- /notices -->

      <!-- loading global -->
      <div
        v-if="!sources || artLoading"
        class="absolute top-0 left-0 w-full h-full flex items-center justify-center pointer-events-none z-200"
      >
        <q-spinner size="60px" :thickness="3" />
      </div>
      <!-- /loading global -->

      <!-- question skip -->
      <transition
        v-if="intro && outro"
        name="q-transition--fade"
        :duration="500"
      >
        <div
          v-if="skiping && !storeSkipFragment.has(skiping)"
          class="absolute bottom-[min(35%,100px)] text-[14px] right-10px bg-[#1c1c1c] bg-opacity-90 rounded-[5px] py-2 px-3 flex flex-nowrap items-center !h-auto !w-auto z-60"
        >
          <span>
            Bỏ qua
            <span class="font-weight-medium">{{
              skiping.intro ? "Mở đầu" : "Kết thúc"
            }}</span>
          </span>
          <q-separator vertical class="mx-2" />
          <div class="text-13px flex items-center">
            <button
              class="px-2 py-1 rounded-5px transition-background hover:bg-gray-300 hover:bg-opacity-10"
              @click="skipOpEnd"
            >
              Bỏ qua
              <span class="block mt-1 text-12px text-gray-300"
                >{{
                  Math.round(
                    skiping.intro
                      ? intro.end - artCurrentTime
                      : outro.end - artCurrentTime
                  )
                }}
                giây</span
              >
            </button>
            <q-btn
              round
              flat
              padding="4px"
              class="self-start"
              @click="storeSkipFragment.add(skiping)"
            >
              <Icon icon="ion:close" class="text-1.2em" />
            </q-btn>
          </div>
        </div>
      </transition>
      <!-- /question skip -->

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

        <div class="flex items-center justify-between mt-4 mb-2">
          Tự động bỏ qua mở đầu / kết thúc
          <q-toggle
            v-model="settingsStore.player.autoSkipIEnd"
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
              :ref="
                (el: QTab) =>
                  void (item.value === currentSeason && (tabsRef = el as QTab))
              "
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
                :chaps="
                  (
                    _cacheDataSeasons.get(value) as
                      | ResponseDataSeasonSuccess
                      | undefined
                  )?.response.chaps
                "
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
import { Fullscreen } from "@boengli/capacitor-fullscreen"
import { Haptics } from "@capacitor/haptics"
import { StatusBar } from "@capacitor/status-bar"
import {
  OrientationType,
  ScreenOrientation,
} from "@capawesome/capacitor-screen-orientation"
import { Icon } from "@iconify/vue"
import {
  useDocumentVisibility,
  useEventListener,
  useIntervalFn,
} from "@vueuse/core"
import ArtDialog from "components/ArtDialog.vue"
import ChapsGridQBtn from "components/ChapsGridQBtn.vue"
import Hls from "hls.js"
import workerHls from "hls.js/dist/hls.worker?url"
import {
  debounce,
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
  IS_IOS,
  MEDIA_STREAM_SUPPORT,
  playbackRates,
  servers,
} from "src/constants"
import { scrollXIntoView } from "src/helpers/scrollIntoView"
import { fetchJava } from "src/logic/fetchJava"
import { findInRangeSet } from "src/logic/find-in-range-set"
import { HlsPatched } from "src/logic/hls-patched"
import { isConnectedToNetwork } from "src/logic/is-connected-to-network"
import { parseChapName } from "src/logic/parseChapName"
import { parseTime } from "src/logic/parseTime"
import { getSegments } from "src/logic/resolve-master-manifest"
import { resolveMasterManifestWorker } from "src/logic/resolve-master-manifest.thread"
import { sleep } from "src/logic/sleep"
import type {
  ProgressWatchStore,
  ResponseDataSeasonError,
  ResponseDataSeasonPending,
  ResponseDataSeasonSuccess,
  Season,
} from "src/pages/phim/_season.interface"
import { useAuthStore } from "stores/auth"
import { useHistoryStore } from "stores/history"
import { useSettingsStore } from "stores/settings"
import { useStateStorageStore } from "stores/state"
import { retryAsync } from "ts-retry"
import {
  computed,
  onBeforeUnmount,
  ref,
  shallowReactive,
  shallowRef,
  watch,
  watchEffect,
} from "vue"
import { useI18n } from "vue-i18n"
import { onBeforeRouteLeave, useRouter } from "vue-router"

import { getSktAt } from "../logic/get-skt-at"
import { loadVttSk } from "../logic/load-vtt-sk"

const authStore = useAuthStore()
const settingsStore = useSettingsStore()
const historyStore = useHistoryStore()
const stateStorageStore = useStateStorageStore()

const router = useRouter()
const $q = useQuasar()
const { t } = useI18n()

const playerWrapRef = ref<HTMLDivElement>()
const documentVisibility = useDocumentVisibility()

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
  sources?: Awaited<ReturnType<typeof PlayerLink>>["link"]
  currentSeason: string
  nameCurrentSeason?: string
  currentChap?: string
  nameCurrentChap?: string
  engNameCurrentChap?: string
  nextChap?: SiblingChap
  prevChap?: SiblingChap
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
  intro?: {
    start: number
    end: number
  }
  outro?: {
    start: number
    end: number
  }
  skUrl?: string | null
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
  (newVal, oldVal) => {
    if (newVal && oldVal) setArtPlaying(true)
  }
)
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

      const currentUid = uidChap.value

      try {
        if (stateStorageStore.disableAutoRestoration > 0) {
          addNotice(t("bo-qua-khoi-phuc-tien-trinh-xem"))
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
          throw new Error("NOT_RESET")
        }
      } catch (err) {
        setArtCurrentTime(0)
        if ((err as Error)?.message !== "NOT_RESET") console.error(err)
      }

      progressRestored = currentUid
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

let activeTime = Date.now()
const setArtControlShow = (show: boolean) => {
  artControlShow.value = show
  if (show) activeTime = Date.now()
}
if (isNative)
  watch(
    artControlShow,
    (artControlShow) =>
      artControlShow && artFullscreen.value && StatusBar.hide()
  )
const artFullscreen = ref(false)
const setArtFullscreen = async (fullscreen: boolean) => {
  console.log("set art fullscreen ", fullscreen)
  if (fullscreen) {
    if (IS_IOS) {
      await ScreenOrientation.lock({ type: OrientationType.LANDSCAPE })
    } else if (!isNative) {
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      await playerWrapRef.value!.requestFullscreen()
    }
    if (isNative) {
      await Fullscreen.activateImmersiveMode()
    }
  } else {
    if (IS_IOS) await ScreenOrientation.unlock()
    else if (!isNative) await document.exitFullscreen()
    if (isNative) {
      await Fullscreen.deactivateImmersiveMode()
    }
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
// const seasonMetaCreated = new Set<string>()

// async function createSeason(
//   currentSeason: string,
//   seasonName: string,
//   poster: string,
//   name: string
// ): Promise<boolean> {
//   // eslint-disable-next-line camelcase
//   const { user_data } = authStore

//   if (seasonMetaCreated.has(currentSeason)) return true

//   if (
//     // eslint-disable-next-line camelcase
//     !user_data
//   )
//     return false
//   console.log("set new season poster %s", poster)
//   await Promise.race([
//     historyStore.createSeason(currentSeason, {
//       poster,
//       seasonName,
//       name,
//     }),
//     sleep(1_000),
//   ])
//   seasonMetaCreated.add(currentSeason)
//   return true
// }

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
function throttle<T extends (...args: any[]) => Promise<void>>(
  fn: T
): T & {
  cancel: () => void
} {
  let wait = false

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
    nameSeason: string,
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

    try {
      // get data from uid and process because processingSaveCurTimeIn === uid then load all of time current

      let cur = artCurrentTime.value

      let dur = artDuration.value

      if (!dur || cur <= 5) {
        // <5s -> pass
        console.warn("[saveCurTime]: artDuration is %s", dur)
        return
      }

      // if (!(await createSeason(currentSeason, nameSeason, poster, name))) return

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

      await Promise.race([
        historyStore
          .setProgressChap(
            currentSeason,
            currentChap,
            {
              name,
              poster,
              season: currentSeason,
              season_name: nameSeason,
            },
            {
              cur,
              dur,
              name: nameCurrentChap,
            }
          )
          .catch((err) => console.warn("save viewing progress failed: ", err)),

        sleep(1_000),
      ])

      console.log("save viewing progress")
    } catch (err) {
      console.error(err)
    } finally {
      savingTimeEpStore.delete(uidTask)
    }
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
  if (props.currentChap === undefined || props.nameCurrentSeason === undefined)
    return
  if (typeof props.nameCurrentChap !== "string") return

  console.log("call throw emit")
  saveCurTimeToPer(
    props.currentSeason,
    props.nameCurrentSeason,
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
    emitNextChap()
  }
}

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
  let resume: (() => void) | null = null

  let pause: (() => void) | null = null
  const resumeDelay = debounce(() => resume?.(), 1_000)
  onBeforeUnmount(() => pause?.())
  watch(
    () => settingsStore.player.enableRemindStop,
    (enabled) => {
      if (enabled) {
        if (resume) resumeDelay()
        else {
          const interval = useIntervalFn(() => {
            if (!artPlaying.value) return

            pause?.()
            setArtPlaying(false)

            $q.dialog({
              title: t("xac-nhan"),
              message: t("ban-van-dang-xem-chu"),
              cancel: { rounded: true, flat: true },
              ok: { rounded: true, flat: true },
              persistent: false,
            })
              .onOk(() => {
                setArtPlaying(true)
                resumeDelay?.()
              })
              .onDismiss(() => {
                setArtPlaying(true)
                resumeDelay?.()
              })
              .onCancel(() => {
                console.warn("cancel continue play")
              })
          }, 1 /* hours */ * 3600_000)
          resume = interval.resume
          pause = interval.pause
        }
      } else {
        resumeDelay.cancel()
        pause?.()
      }
    },
    { immediate: true }
  )

  watch(
    artPlaying,
    (playing) => {
      if (playing) {
        resumeDelay()
      } else {
        pause?.()
      }
    },
    { immediate: true }
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
    title: "Relay change",
    message: "Bạn đang muốn đổi relay khác?",
    cancel: true,
    persistent: false,
  }).onOk(remount)
}

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
    const offEnds = "_extra"
    let セグメントｓ: string[] | null = null
    const セグメント解決済み = new Map<string, readonly [string, number]>()
    const resolvingTask = new Set<number>()

    const hls = new HlsPatched(
      {
        debug: false && import.meta.env.DEV,
        workerPath: workerHls,
        progressive: true,
        fragLoadingRetryDelay: 10000,
        fetchSetup(context, initParams) {
          // set header because this version always cors not fix by extension like desktop-web
          if (isNative) initParams.headers.set("referer", C_URL)
          if (!context.url.includes("base64"))
            context.url += `#animevsub-vsub${offEnds}`

          return new Request(context.url, initParams)
        },
      },
      settingsStore.player.preResolve !== 0 &&
      Number.MAX_SAFE_INTEGER !== settingsStore.player.preResolve
        ? (request) => {
            /// pre setup
            // transforming
            if (!セグメントｓ)
              void getSegments(file).then((arr) => (セグメントｓ = arr))
            if (セグメントｓ) {
              // セグメントｓ ready
              // preload if in セグメントｓ
              const realUrl = request.url.split("#")[0]
              const インデントセグメント = セグメントｓ.indexOf(realUrl)
              const 解決済み = セグメント解決済み.get(realUrl)
              if (
                インデントセグメント > -1 &&
                (!解決済み ||
                  settingsStore.player.preResolve - 解決済み[1] <
                    settingsStore.player.checkEndPreList) &&
                !findInRangeSet(
                  resolvingTask,
                  インデントセグメント,
                  settingsStore.player.preResolve
                )
              ) {
                console.log(
                  "Starting pre resolve segment",
                  インデントセグメント,
                  resolvingTask
                )
                resolvingTask.add(インデントセグメント)
                // あとで５セメント
                void retryAsync(
                  () =>
                    resolveMasterManifestWorker(
                      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
                      セグメントｓ!,
                      セグメント解決済み,
                      インデントセグメント,
                      settingsStore.player.preResolve +
                        settingsStore.player.checkEndPreList
                    ),
                  { maxTry: 10, delay: 3_000 }
                ).catch(() => resolvingTask.add(インデントセグメント))
                // load balance 20
                // check fn セグメントｓ in ２０。workerせとじゃない。メモリー？
              }
              if (import.meta.env.DEV && 解決済み)
                console.info("[Segment]: using url resolved")
              if (解決済み)
                return fetchJava(解決済み[0], request).catch(async (err) => {
                  if (
                    err instanceof TypeError &&
                    err.message === "Failed to fetch" &&
                    (await isConnectedToNetwork())
                  ) {
                    // url expired
                    セグメント解決済み.clear()
                    resolvingTask.clear()
                    console.log("[Segment]: url expired. Clear all cache")
                    if (
                      Number.MAX_SAFE_INTEGER ===
                      settingsStore.player.preResolve
                    )
                      preResolveHot()
                    return fetchJava(request.url, request)
                  }

                  throw err
                })
            }
            return request.url.includes("base64")
              ? CapacitorWebFetch(request)
              : fetchJava(request.url, request)
          }
        : Number.MAX_SAFE_INTEGER === settingsStore.player.preResolve
        ? (request) => {
            const realUrl = request.url.split("#")[0]
            const 解決済み = セグメント解決済み.get(realUrl)
            if (import.meta.env.DEV && 解決済み)
              console.info("[Segment]: using url resolved")
            if (解決済み) return fetchJava(解決済み[0], request)
            return request.url.includes("base64")
              ? CapacitorWebFetch(request)
              : fetchJava(request.url, request)
          }
        : (request) =>
            request.url.includes("base64")
              ? CapacitorWebFetch(request)
              : fetchJava(request.url, request)
    )
    // if (!offEnds) patcher(hls)
    currentHls = hls
    // customLoader(hls.config)
    hls.loadSource(file)
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    hls.attachMedia(video.value!)
    hls.on(Hls.Events.MANIFEST_PARSED, () => {
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      if (playing) video.value!.play()

      // !NOTIFY resolve redirect urls
      if (Number.MAX_SAFE_INTEGER === settingsStore.player.preResolve)
        preResolveHot()
    })

    // eslint-disable-next-line no-inner-declarations
    function preResolveHot() {
      void retryAsync(
        async () =>
          resolveMasterManifestWorker(
            await getSegments(file),
            セグメント解決済み
          ),
        {
          maxTry: 10,
          delay: 3_000,
        }
      )
    }

    let needSwapCodec = false

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

const currentingTime = ref(false)
const progressInnerRef = ref<HTMLDivElement>()
function onIndicatorMove(
  event: TouchEvent | MouseEvent,
  innerEl?: HTMLDivElement
): void

function onIndicatorMove(
  event: TouchEvent | MouseEvent,
  innerEl: HTMLDivElement,
  offsetX: number,
  curTimeStart: number
): void

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

let timeoutHoldBD: number | NodeJS.Timeout | null = null

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

let timeoutResetDoubleClicking: number | NodeJS.Timeout | null = null

let lastTimeClick: number

let lastPositionClickIsLeft: boolean | null = null

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
let id = 1
function addNotice(text: string) {
  const uuid = id++
  notices.push({ id: uuid, text })
  setTimeout(() => {
    notices.splice(notices.findIndex((item) => item.id === uuid) >>> 0, 1)
  }, 5000)
}

function emitNextChap(noNotice?: boolean) {
  if (!props.nextChap) return

  if (!noNotice)
    addNotice(
      props.currentSeason !== props.nextChap.season.value
        ? `Đang phát season ${props.nextChap.season.name} sau`
        : `Đang phát tập ${props.nextChap.chap?.name ?? ""} tiếp theo`
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
function emitPrevChap(noNotice?: boolean) {
  if (!props.prevChap) return

  if (!noNotice)
    addNotice(
      props.currentSeason !== props.prevChap.season.value
        ? `Đang phát season ${props.prevChap.season.name} trước`
        : `Đang phát tập ${props.prevChap.chap?.name ?? ""} trước`
    )

  router.push(
    `/phim/${props.prevChap.season.value}/${
      props.prevChap.chap
        ? parseChapName(props.prevChap.chap.name) +
          "-" +
          props.prevChap.chap?.id
        : ""
    }`
  )
}

if (typeof MediaMetadata !== "undefined" && navigator.mediaSession)
  watchEffect(() => {
    if (!props.nameCurrentChap || !props.name || !props.poster) return

    const title = t("tap-_chap-_name-_othername", [
      props.nameCurrentChap,
      props.name,
      "",
    ])

    navigator.mediaSession.metadata = new MediaMetadata({
      title,
      artist: props.name,
      artwork: [
        {
          src: props.poster,
        },
      ],
    })
  })
// keybind for headphone control
navigator.mediaSession?.setActionHandler("pause", () => {
  const playing = artPlaying.value
  setArtPlaying(false)
  if (playing) setArtControlShow(true)
})
navigator.mediaSession?.setActionHandler("play", () => {
  setArtPlaying(true)
})
navigator.mediaSession?.setActionHandler("previoustrack", () => {
  emitPrevChap()
})
navigator.mediaSession?.setActionHandler("nexttrack", () => {
  emitNextChap()
})
onBeforeUnmount(() => {
  navigator.mediaSession?.setActionHandler("pause", null)
  navigator.mediaSession?.setActionHandler("play", null)
  navigator.mediaSession?.setActionHandler("previoustrack", null)
  navigator.mediaSession?.setActionHandler("nexttrack", null)
})

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

function inClamp(value: number, min: number, max: number) {
  return value >= min && value < max
}
const skiping = shallowRef<{ readonly intro: boolean } | null>(null)
watch(
  () => {
    const { intro, outro } = props
    if (!intro || !outro) return null
    const current = artCurrentTime.value
    if (inClamp(current, intro.start, intro.end)) return true
    if (inClamp(current, outro.start, outro.end)) return false
  },
  (intro) => {
    if (typeof intro === "boolean") skiping.value = { intro }
    else skiping.value = null
  }
)
function skipOpEnd() {
  if (!skiping.value) return
  if (storeSkipFragment.has(skiping.value)) return
  if (skiping.value.intro && props.intro) {
    setArtCurrentTime(props.intro.end)
    return
  }
  if (!skiping.value.intro && props.outro) setArtCurrentTime(props.outro.end)
}
const storeSkipFragment = shallowReactive(
  new WeakSet<{ readonly intro: boolean }>()
)
watch(skiping, (skiping) => {
  if (!skiping) return
  if (!settingsStore.player.autoSkipIEnd) return

  skipOpEnd()
})

const optionsPreResolve = computed(() => [
  {
    label: t("tat"),
    value: 0,
    color: "secondary",
    keepColor: true,
    checkedIcon: "task_alt",
    uncheckedIcon: "panorama_fish_eye",
  },
  ...[20, 30, 40, 50, 60, 70, 80, 100].map((val, i) => ({
    label: t("val-yeu-cau", [val]),
    value: val,
    keepColor: true,
    checkedIcon: "task_alt",
    uncheckedIcon: "panorama_fish_eye",
    color: `light-green-${4 + i}`,
  })),
  {
    label: t("nong"),
    value: Number.MAX_SAFE_INTEGER,
    color: "red",
    keepColor: true,
    checkedIcon: "task_alt",
    uncheckedIcon: "panorama_fish_eye",
  },
])
function openPopupFlashNetwork() {
  $q.dialog({
    title: t("giai-quyet-truoc-yeu-cau-mang"),
    message: t("msg-pre-resolve"),
    options: {
      type: "radio",
      model: settingsStore.player.preResolve as unknown as string,
      // inline: true
      items: optionsPreResolve.value,
    },
    cancel: {
      label: t("huy"),
      noCaps: true,
      color: "grey",
      text: true,
      flat: true,
      rounded: true,
    },
    ok: { color: "green", text: true, flat: true, rounded: true },
  }).onOk((data) => {
    settingsStore.player.preResolve = data
  })
}

// sk thumb
const vttMeta = computedAsync(
  async () => {
    if (!props.skUrl) return

    const meta = await loadVttSk(props.skUrl)
    const aspect = (meta[0].h / meta[0].w) * 100

    return { meta, aspect }
  },
  undefined,
  {
    onError: WARN,
    lazy: true,
    shallow: true,
  }
)
const currentSkt = computed(() => {
  if (!vttMeta.value) return
  const { meta, aspect } = vttMeta.value

  const current = getSktAt(artCurrentTime.value, meta)
  if (!current) return

  return [current, aspect] as const
})
const sktStyle = computed(() => {
  const val = currentSkt.value
  if (!val) return {}

  const [current, aspect] = val

  // max width = 220px
  const scale = current.w > 220 ? 220 / current.w : 1

  return {
    thumbs: true,
    aspect: `${aspect}%`,
    image: `url(${new URL(current.text, props.skUrl ?? "")})`,
    scale,
    x: current.x + "px",
    y: current.y + "px",
    w: current.w + "px",
    h: current.h + "px",
  }
})
const skRef = ref<HTMLDivElement>()
const leftSkt = computed(() => {
  if (!skRef.value || !playerWrapRef.value || !progressInnerRef.value)
    return null

  const minWidth = (skRef.value.offsetWidth / 2) * (sktStyle.value.scale ?? 1)
  const maxWidth = playerWrapRef.value.offsetWidth - minWidth
  const padding =
    (playerWrapRef.value.offsetWidth - progressInnerRef.value.offsetWidth) / 2
  const offset = Math.max(
    minWidth - padding + 3,
    Math.min(
      maxWidth - padding - 3,
      (artCurrentTime.value / artDuration.value) *
        (playerWrapRef.value.offsetWidth - padding * 2)
    )
  )

  return offset
})
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
      // &:after {
      //   display: block !important;
      // }
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
    @apply h-min-[100px] z-199;
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
        }

        .art-sk-hoved {
          @apply absolute z-199 left-0 top-0 bottom-0 h-full inline-block;
          pointer-events: none;
          &:after {
            content: attr(data-title);
            @apply relative transform translate-x-[-50%] translate-y-[calc(-100%-16px)] left-0 inline-block;
            background: rgba(0, 0, 0, 0.7);
            @apply py-2 px-3 font-weight-medium rounded-lg;
            white-space: nowrap;
          }
          &.art-sk-hoved--thumbs {
            &:after {
              padding: 3px 5px;
              font-size: 12px;
              background-color: #000000b3;
              border-radius: 3px;
              @apply absolute right-0 bottom-[100%] bottom-unset right-auto translate-x-[-50%] translate-y-[calc(-10%-16px)];
              // @apply relative translate-y-[calc(-100%-16px)] translate-x-[-50%] bottom-auto right-auto;
            }
            &:before {
              content: "";
              display: block;
              @apply rounded-[3px] relative left-0; // transform translate-y-[calc(-100%-10px)] translate-x-[-50%] left-0 scale-[v-bind("sktStyle.scale")];
              box-shadow: 0 1px 3px #0003, 0 1px 2px -1px #0003;
              pointer-events: none;
              width: v-bind("sktStyle.w");
              // height: v-bind("sktStyle.h");
              transform: translateY(calc(-100% - 10px)) translateX(-50%)
                scale(v-bind("sktStyle.scale"));
              padding-top: v-bind("sktStyle.aspect");
              background: {
                color: #000000d9;
                repeat: no-repeat;
                image: v-bind("sktStyle.image");
                position: v-bind("sktStyle.x") v-bind("sktStyle.y");
              }
            }
          }
          &:before {
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
