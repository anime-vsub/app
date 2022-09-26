<template>
  <q-page v-if="loading || !data" class="fit flex items-center justify-between">
    <q-spinner style="color: #00be06" size="3em" :thickness="3" />
  </q-page>

  <q-page v-else-if="error">
    {{ error }}
  </q-page>

  <q-page v-else>
    <BrtPlayer
      :sources="sources"
      :current-season="currentSeason"
      :current-chap="currentChap"
      :name="data.name"
      :chap-name="currentMetaChap?.name"
      :poster="data.poster"
      :all-seasons="allSeasons"
      :_cache-data-seasons="_cacheDataSeasons"
      :fetch-season="fetchSeason"
    />

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
          :chaps="(_cacheDataSeasons.get(value) as ResponseDataSeasonSuccess | undefined)?.response.chaps"
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
        :ref="(el) => item.value === currentSeason && (tabsRef = el as QTab)"
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
            v-for="item in allSeasons"
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
              :chaps="(_cacheDataSeasons.get(value) as ResponseDataSeasonSuccess | undefined)?.response.chaps"
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
import BrtPlayer from "components/BrtPlayer.vue"
import ChapsGridQBtn from "components/ChapsGridQBtn.vue"
import Quality from "components/Quality.vue"
import Star from "components/Star.vue"
import dayjs from "dayjs"
import { QTab } from "quasar"
import { PhimId } from "src/apis/phim/[id]"
import { PhimIdChap } from "src/apis/phim/[id]/[chap]"
import BottomSheet from "src/components/BottomSheet.vue"
import { labelToQuality } from "src/constants"
import { formatView } from "src/logic/formatView"
import { post } from "src/logic/http"
import { computed, reactive, ref, shallowRef, watch, watchEffect } from "vue"
import { useRequest } from "vue-request"
import { useRoute, useRouter } from "vue-router"

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
watch(currentSeason, (val) => (seasonActive.value = val), { immediate: true })

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
  currentMetaChap,
  async (currentMetaChap) => {
    if (!currentMetaChap) return

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

// @scrollIntoView
const tabsRef = ref<QTab>()
watchEffect(() => {
  if (!tabsRef.value) return
  if (!currentSeason.value) return

  setTimeout(() => {
    tabsRef.value?.$el.scrollIntoView({
      inline: "center",
    })
  }, 70)
})

// ================ status ================
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
