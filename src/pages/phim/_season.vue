<template>
  <q-page v-if="loading || !data" class="fit flex items-center justify-between">
    <q-spinner style="color: #00be06" size="3em" :thickness="3" />
  </q-page>

  <q-page v-else-if="error">
    {{ error }}
  </q-page>

  <q-page v-else>
    <div class="w-full overflow-hidden" ref="playerWrapRef">
      <q-responsive :ratio="16 / 9" class="player__wrap">
        <div
          class="absolute top-0 left-0 w-full h-full z-98"
          @click="showControl"
          v-show="!artControlShow"
        />
        <transition name="fade__ease-in-out">
          <div
            class="art-layer-controller z-99 overflow-hidden"
            :class="{
              'currenting-time': currentingTime,
            }"
            v-show="artControlShow"
            @click="artControlShow = false"
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
                    {{ data.name }}
                  </div>
                  <div v-if="currentMetaChap" class="text-gray-300">
                    Tập
                    {{ currentMetaChap.name }}
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
              @touchstart.prevent.stop="artPlaying = !artPlaying"
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
                    EP {{ currentMetaChap?.name }}
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
                      class="absolute w-[20px] h-[20px] right-[-10px] top-[calc(100%-10px)] art-progress-indicator"
                      :data-title="parseTime(currentTime)"
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
                    {{ parseTime(currentTime) }} / {{ parseTime(duration) }}
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
                :ref="(el) => item.value === currentSeason && (tabsRef = el)"
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
                  :chaps="_cacheDataSeasons.get(value)?.response.chaps"
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
    </div>

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

    <q-tab-panels
      v-model="seasonActive"
      animated
      keep-alive
      class="h-full bg-transparent overflow-scroll whitespace-nowrap mb-3"
    >
      <q-tab-panel
        v-for="{ value } in allSeasons"
        :key="value"
        :name="value"
        class="!h-[47px] py-0 !px-0"
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
          :chaps="_cacheDataSeasons.get(value)?.response.chaps"
          :season="value"
          :find="(item) => value === currentSeason && item.id === currentChap"
        />
      </q-tab-panel>
    </q-tab-panels>

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
        class="bg-[#2a2a2a] mx-1 rounded-sm !min-h-0 py-[6px]"
        content-class="children:!font-normal children:!text-[13px] children:!min-h-0"
        :ref="(el) => item.value === currentSeason && (tabsRef = el)"
      />
    </q-tabs>

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
        <q-tabs
          v-model="seasonActive"
          no-caps
          dense
          inline-label
          active-class="c--main"
        >
          <q-tab
            v-for="(item, index) in allSeasons"
            :key="item.value"
            :name="item.value"
            :label="item.name"
          />
        </q-tabs>

        <q-tab-panels v-model="seasonActive" animated keep-alive class="h-full">
          <q-tab-panel
            v-for="({ value }, index) in allSeasons"
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
              :chaps="_cacheDataSeasons.get(value)?.response.chaps"
              :season="value"
              :find="
                (item) => value === currentSeason && item.id === currentChap
              "
              class="px-4 py-[10px] mx-2 mb-3"
            />
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
import Artplayer from "artplayer";
import OverScrollX from "components/OverScrollX.vue";
import Quality from "components/Quality.vue";
import Star from "components/Star.vue";
import dayjs from "dayjs";
import { PhimId } from "src/apis/phim/[id]";
import { PhimIdChap } from "src/apis/phim/[id]/[chap]";
import BottomSheet from "src/components/BottomSheet.vue";
import { formatView } from "src/logic/formatView";
import { Hls } from "src/logic/hls";
import { post } from "src/logic/http";
import {
  computed,
  onMounted,
  reactive,
  ref,
  shallowRef,
  watch,
  watchEffect,
  shallowReactive,
} from "vue";
import { useRequest } from "vue-request";
import { useRoute, useRouter } from "vue-router";
import { parseTime } from "src/logic/parseTime";
import { Icon } from "@iconify/vue";
import ArtDialog from "components/ArtDialog.vue";
import ChapsGridQBtn from "components/ChapsGridQBtn.vue";

const tabsBtnSeasonRefs = reactive<HTMLButtonElement[]>([]);

const route = useRoute();
const router = useRouter();

const currentSeason = computed(() => route.params.season as string);
const allSeasons = computed(() => {
  const season = data.value?.season ?? [];

  if (season.length > 0) {
    return season.map((item) => {
      return {
        ...item,
        value: router.resolve(item.path).params.season as string,
      };
    });
  }

  return [
    {
      path: `/phim/${currentSeason.value}/`,
      name: "[[DEFAULT]]",
      value: currentSeason.value,
    },
  ];
});
const currentMetaSeason = computed(() => {
  return allSeasons.value?.find((item) => item.value === currentSeason.value);
});

const { data, loading, error } = useRequest(
  () => {
    return PhimId(`/phim/${currentSeason.value}/`);
  },
  {
    refreshDeps: [currentSeason],
  }
);

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
const _cacheDataSeasons = reactive<
  Map<
    string,
    | ResponseDataSeasonPending
    | ResponseDataSeasonSuccess
    | ResponseDataSeasonError
  >
>(new Map());

const seasonActive = ref<string>();
// sync data by active route
watch(currentSeason, (val) => (seasonActive.value = val), { immediate: true });

watch(
  seasonActive,
  (seasonActive) => {
    if (!seasonActive) return;

    // download data season active
    fetchSeason(seasonActive);
  },
  {
    immediate: true,
  }
);

const currentDataSeason = computed(() => {
  const inCache = _cacheDataSeasons.get(currentSeason.value);

  if (inCache?.status === "success") return inCache.response;

  return undefined;
});
const currentChap = computed(() => {
  if (route.params.chap) return route.params.chap;

  // get first chap in season

  return currentDataSeason.value?.chaps[0].id;
});
const currentMetaChap = computed(() => {
  return currentDataSeason.value?.chaps.find(
    (item) => item.id === currentChap.value
  );
});
const configPlayer = shallowRef<{
  link: {
    file: string;
    label: string;
    preload: string;
    type: "hls";
  }[];
  playTech: "api";
}>();
watch(
  currentMetaChap,
  async (currentMetaChap) => {
    if (!currentMetaChap) return;

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
      );
    } catch (err) {
      console.log({ err });
    }
  },
  { immediate: true }
);
const sources = computed(() =>
  configPlayer.value?.link.map((item) => {
    return {
      html: labelToQuality[item.label] ?? item.label,
      url: item.file.startsWith("http") ? item.file : `https:${item.file}`,
    };
  })
);

// ===================== player =========================

const playerRef = ref<HTMLDivElement>();

const labelToQuality: Record<string, string> = {
  HD: "720p",
  SD: "480p",
};
const playbackRates = [
  {
    name: "0.5x",
    value: 0.5,
  },
  {
    name: "0.75x",
    value: 0.75,
  },
  {
    name: "1.0x",
    value: 1,
  },
  {
    name: "1.25x",
    value: 1.25,
  },
  {
    name: "1.5x",
    value: 1.5,
  },
  {
    name: "2.0x",
    value: 2,
  },
];

const art = shallowRef<Artplayer | null>(null);
const handlersArtMounted = new Set<(art: Artplayer) => void>();
const watcherArt = watch(art, (art) => {
  if (!art) return;
  watcherArt();
  handlersArtMounted.forEach((handler) => handler(art));
  handlersArtMounted.clear();
});
function onArtMounted(handler: (art: Artplayer) => void) {
  if (art.value) handler(art.value);
  else handlersArtMounted.add(handler);
}

// value control set
const artControlShow = ref(true);
watch(artControlShow, (artControlShow) => {
  if (artControlShow && artFullscreen.value) StatusBar.hide();
});
const artPlaying = ref(true);
const artFullscreen = ref(false);
const artPlaybackRate = ref(1);
const setArtPlaybackRate = (value: number) => {
  artPlaybackRate.value = value;
  addNotice(`${value}x`);
};
const artQuality = ref<string>();
const setArtQuality = (value: string) => {
  artQuality.value = value;
  addNotice(`Chất lượng đã chuyển sang ${value}`);
};
const currentStream = computed(() => {
  return sources.value?.find((item) => item.html === artQuality.value);
});
// re-set quality if quality not in sources
watch(
  sources,
  (sources) => {
    if (!sources || sources.length === 0) return;
    // not ready quality on this
    if (!artQuality || !currentStream.value) {
      artQuality.value = sources[0].html;
    }
  },
  { immediate: true }
);

// value control get
const duration = ref<number>(0);
const currentTime = ref<number>(0);
const percentageResourceLoaded = ref<number>(0);

// bind value control get
onArtMounted((art) => {
  art.on("video:durationchange", () => {
    duration.value = art.duration;
  });
  art.on("video:timeupdate", () => {
    if (currentingTime.value) return;
    currentTime.value = art.currentTime;
  });

  art.on("video:progress", (event: Event) => {
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

      percentageResourceLoaded.value = loadEndPercentage;
    } catch {
      percentageResourceLoaded.value = bf.end(0) / target.duration;
    }
  });
});

// eslint-disable-next-line functional/no-let
let activeTime = Date.now();
function showControl() {
  artControlShow.value = true;
  activeTime = Date.now();
}

import { StatusBar, Style } from "@capacitor/status-bar";
import { useQuasar } from "quasar";
import { NavigationBar } from "@hugotomazi/capacitor-navigation-bar";

const $q = useQuasar();

const playerWrapRef = ref<HTMLDivElement>();
// bind value control set
onArtMounted((art) => {
  watch(
    artPlaying,
    (val) => {
      if (val) {
        art.play();
      } else {
        art.pause();
      }
    },
    { immediate: true }
  );
  art.on("play", () => (artPlaying.value = true));
  art.on("pause", () => (artPlaying.value = false));
  art.on("video:timeupdate", () => {
    if (
      artPlaying.value &&
      !currentingTime.value &&
      artControlShow.value &&
      Date.now() - activeTime >= 3000
    ) {
      artControlShow.value = false;
    }
  });
  watch(
    artFullscreen,
    async (artFullscreen) => {
      if (artFullscreen) {
        await playerWrapRef.value.requestFullscreen();
        screen.orientation.lock("landscape");
        StatusBar.hide();
        NavigationBar.hide();
        StatusBar.setOverlaysWebView({
          overlay: true,
        });
      } else {
        await document.exitFullscreen().catch(() => {});
        screen.orientation.unlock("landscape");
        StatusBar.show();
        NavigationBar.show();
        StatusBar.setOverlaysWebView({
          overlay: false,
        });
      }
    },
    { immediate: true }
  );
  watch(artPlaybackRate, (artPlaybackRate) => {
    art.playbackRate = artPlaybackRate;
  });
  watch(
    () => currentStream?.url,
    (url) => {
      console.log("set url art ");
      if (url) art.url = url;
    },
    { immediate: true }
  );
  watch(
    () => data.value?.poster,
    (poster) => {
      // if (poster) art.poster = poster
    },
    { immediate: true }
  );
});

/// save currentTime play
onArtMounted(() => {});

onMounted(() => {
  const watcher = watch(
    sources,
    async (sources) => {
      if (!sources) return;

      if (!art.value) {
        watcher();
        art.value = new Artplayer({
          // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
          container: playerRef.value!,
          url: sources[0].url, // 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8',
          // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
          // poster: data.value!.poster,
          id: "player",
          autoplay: true,

          customType: {
            m3u8: function (video, url) {
              if (Hls.isSupported()) {
                const hls = new Hls({
                  // progressive: true,
                  // debug: true
                });
                // customLoader(hls.config)
                hls.loadSource(url);
                hls.attachMedia(video);
              } else {
                const canPlay = video.canPlayType(
                  "application/vnd.apple.mpegurl"
                );
                if (canPlay === "probably" || canPlay === "maybe") {
                  video.src = url;
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
        });

        Object.defineProperty(art.value.controls, "show", {
          get() {
            return artControlShow.value;
          },
          // eslint-disable-next-line @typescript-eslint/no-unused-vars
          set(_val) {
            // empty
          },
        });
      }
    },
    { immediate: true }
  );
});

const currentingTime = ref(false);
const progressInnerRef = ref<HTMLDivElement>();
function onIndicatorMove(event: TouchEvent | MouseEvent) {
  currentingTime.value = true;
  const maxX = progressInnerRef.value.offsetWidth;

  const clientX = Math.min(
    maxX,
    Math.max(
      0,
      (event.changedTouches?.[0] ?? event.touches?.[0] ?? event).clientX - 10
    )
  );

  currentTime.value = (art.value.duration * clientX) / maxX;
  activeTime = Date.now();
}
function onIndicatorEnd() {
  currentingTime.value = false;

  art.value.currentTime = currentTime.value;
  activeTime = Date.now();
  if (artPlaying.value) {
    art.value.play();
  }
}
// =======================================================
async function fetchSeason(season: string) {
  if (_cacheDataSeasons.get(season)?.status === "success") return;

  _cacheDataSeasons.set(season, {
    status: "pending",
  });
  try {
    _cacheDataSeasons.set(season, {
      status: "success",
      response: await PhimIdChap(`/phim/${season}/xem-phim.html`),
    });
  } catch (err) {
    _cacheDataSeasons.set(season, {
      status: "error",
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      response: err as any,
    });
  }
}
// ===== setup =====

const notices = shallowReactive<
  {
    id: number;
    text: string;
  }[]
>([]);
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
  if (!currentSeason.value) return;

  setTimeout(() => {
    tabsRef.value?.$el.scrollIntoView({
      inline: "center",
    });
  }, 70);
});

// ================ status ================
const openBottomSheetChap = ref(false);
const showDialogSetting = ref(false);
const showDialogChapter = ref(false);
const showDialogPlayback = ref(false);
const showDialogQuality = ref(false);
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
    font-size : 14px;
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
