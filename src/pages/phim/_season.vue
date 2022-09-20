<template>
  <q-page v-if="data">
    <q-video :ratio="16 / 9" :poster="data.poster" src="" />

    <div class="px-2 pt-4">
      {{ currentStream }}
      <h1 class="line-clamp-2 text-weight-medium py-0 my-0 text-[18px]">
        {{ data.name }}
      </h1>
      <h5 class="text-gray-400 text-weight-normal">
        {{ formatView(data.views) }} lượt xem
        <span
          class="inline-block w-1 h-1 rounded bg-[currentColor] mb-1 mx-1"
        />
        {{ data.seasonOf }}
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
      v-if="
        datasSeason[currentSeason]?.status ==='succ' &&
       ( datasSeason[currentSeason] as any).response?.update
      "
      class="text-gray-300 px-2"
    >
      Tập mới cập nhật lúc
      {{ (datasSeason[currentSeason] as any).response?.update }}
    </div>

    <q-btn
      flat
      no-caps
      class="w-full !px-2 mt-4"
      @click="openBottomSheetChap = true"
    >
      <div class="flex items-center justify-between text-subtitle2 w-full">
        {{ seasons?.find((item) => item.value === currentSeason)?.name }} Tập

        <span class="flex items-center text-gray-300 font-weight-normal">
          Trọn bộ <q-icon name="chevron_right" class="mr-[-8px]"></q-icon>
        </span>
      </div>
    </q-btn>
    <OverScrollX>
      <router-link
        v-for="item in  ( datasSeason[currentSeason] as any).response?.chaps"
        :key="item.name"
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
    <transition name="slide">
      <div
        v-show="openBottomSheetChap"
        class="h-[calc(100%-100vmin/16*9)] bottom-0 fixed left-0 w-full bg-dark flex column flex-nowrap"
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
              v-for="(item, index) in seasons"
              :key="item.value"
              class="chap-name"
              :class="{
                active: item.value === seasonSelect,
              }"
              @click="switchToTabSeason(index)"
              :ref="(el) => (tabsBtnSeasonRefs[index] = el as unknown as HTMLButtonElement)"
            >
              {{ item.name }}
            </button>
            <div class="tabs-season-line" :style="lineSeasonStyle" />
          </OverScrollX>

          <q-tab-panels
            v-model="seasonSelect"
            animated
            keep-alive
            class="h-full"
          >
            <q-tab-panel
              v-for="{ value } in seasons"
              :key="value"
              :name="value"
            >
              <div
                v-if="
                  !datasSeason[value] || datasSeason[value].status === 'pending'
                "
                class="absolute top-[50%] left-[50%] transform -translate-x-1/2 -translate-y-1/2"
              >
                <q-spinner style="color: #00be06" size="3em" :thickness="3" />
              </div>
              <div
                v-else-if="datasSeason[value].status === 'fail'"
                class="absolute top-[50%] left-[50%] text-center transform -translate-x-1/2 -translate-y-1/2"
              >
                Lỗi khi lấy dữ liệu
                <br />
                <q-btn
                  dense
                  no-caps
                  style="color: #00be06"
                  @click="fetchChaptersInSeason(value)"
                  >Thử lại</q-btn
                >
              </div>
              <router-link
                v-else
                v-for="item in  ( datasSeason[currentSeason] as any).response?.chaps"
                :key="item.name"
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
      </div>
    </transition>

    <!--
      trailer
      toPut
      followed
    -->
  </q-page>
</template>

<style lang="scss">
.slide {
  &-enter-active {
    animation: slide-up 0.44s ease;
    @keyframes slide-up {
      from {
        transform: translateY(100%);
      }
      to {
        transform: translateY(0);
      }
    }
  }
  &-leave-active {
    animation: slide-down 0.44s ease;
    @keyframes slide-down {
      from {
        transform: translateY(0);
      }
      to {
        transform: translateY(100%);
      }
    }
  }
}
</style>
<script lang="ts" setup>
import OverScrollX from "components/OverScrollX.vue"
import Quality from "components/Quality.vue"
import Star from "components/Star.vue"
import html from "src/apis/__test__/data/phim/tonikaku-kawaii-a3860.txt?raw"
import { PhimId } from "src/apis/phim/[id]"
import { PhimIdChap } from "src/apis/phim/[id]/[chap]"
import { formatView } from "src/logic/formatView"
import { computed, reactive, ref, shallowReactive, watchEffect } from "vue"
import { useRequest } from "vue-request"
import { useRoute, useRouter } from "vue-router"

const route = useRoute()
const router = useRouter()

const { data } = useRequest(() => PhimId(html))

const datasSeason = shallowReactive<
  Record<
    string,
    | {
        status: "pending" | "fail"
      }
    | {
        status: "succ"
        response: Awaited<ReturnType<typeof PhimIdChap>>
      }
  >
>({})
const seasonSelect = ref<string>()
watchEffect(() => {
  if (!seasonSelect.value) return

  fetchChaptersInSeason(seasonSelect.value)
})

const seasons = computed(() => {
  return data.value?.season.map((item) => {
    return {
      ...item,
      value: router.resolve(item.path).params.season as string,
    }
  })
})
watchEffect(() => {
  const { value } = seasons
  if (value)
    switchToTabSeason(
      value.findIndex((item) => item?.value === route.params.season)
    )
})

const currentSeason = computed(() => route.params.season as string)
const currentChap = computed(
  () =>
    route.params.chap ??
    (
      datasSeason[currentSeason.value] as unknown as
        | { response: Awaited<ReturnType<typeof PhimIdChap>> }
        | undefined
    )?.response?.chaps[0]?.id
)
const currentStream = computed(() => {
  return (
    datasSeason[currentSeason.value] as unknown as
      | { response: Awaited<ReturnType<typeof PhimIdChap>> }
      | undefined
  )?.response?.chaps.find((item) => item.id === currentChap.value)
})

function fetchChaptersInSeason(val: string) {
  // eslint-disable-next-line functional/immutable-data
  datasSeason[val] = {
    status: "pending",
  }
  PhimIdChap(val)
    // eslint-disable-next-line promise/always-return
    .then((response) => {
      // eslint-disable-next-line functional/immutable-data
      datasSeason[val] = { status: "succ", response }
    })
    // eslint-disable-next-line @typescript-eslint/no-unused-vars, n/handle-callback-err
    .catch((err) => {
      // eslint-disable-next-line functional/immutable-data
      datasSeason[val] = {
        status: "fail",
      }
    })
}
// ===== setup =====

const tabsBtnSeasonRefs = reactive<HTMLButtonElement[]>([])
const lineSeasonStyle = reactive<{
  left: string
  width: string
}>({
  left: "0px",
  width: "0px",
})

function switchToTabSeason(index: number) {
  if (!seasons.value?.[index]) return

  // eslint-disable-next-line functional/immutable-data
  seasonSelect.value = seasons.value[index].value

  const itemBtn = tabsBtnSeasonRefs[index]
  if (!itemBtn) return

  const left = itemBtn.offsetLeft
  const width = itemBtn.offsetWidth

  // eslint-disable-next-line functional/immutable-data
  lineSeasonStyle.left = left + width / 2 + "px"
  // eslint-disable-next-line functional/immutable-data
  lineSeasonStyle.width = width * 0.8 + "px"
}

const openBottomSheetChap = ref(false)

// eslint-disable-next-line promise/catch-or-return
fetch("https://animevietsub.cc")
  .then((res) => res.text())
  // eslint-disable-next-line promise/always-return
  .then((e) => {
    console.log(e)
  })
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
