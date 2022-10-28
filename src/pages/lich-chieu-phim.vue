<template>
  <q-page-sticky position="top" class="children:w-full bg-dark-page z-10">
    <div class="w-full">
      <div class="flex mx-2">
        <q-avatar size="60px">
          <img src="~/assets/trending_avatar.png" />
        </q-avatar>
        <div class="flex items-center">
          <div class="text-subtitle1 text-weight-medium text-[20px] ml-3">
            Lịch chiếu
          </div>
        </div>
      </div>
      <div class="text-[#9a9a9a] mt-3 mb-2 text-[16px]">
        <div
          v-for="(item, index) in data"
          :key="index"
          class="relative inline-block px-4 py-1"
          v-ripple
          :class="{
            'c--main text-weight-medium children:before:block':
              activeIndex === index ||
              dayjs(
                `${item.month}/${item.date}/${new Date().getFullYear()}`
              ).isToday(),
          }"
          @click="activeIndex = index"
        >
          T{{ dayTextToNum(item.day) }}
          <br />
          <span
            class="relative inline-block before:content-DEFAULT before:hidden before:absolute before:h-[2px] before:w-full before:bg-[currentColor] before:bottom-[-4px] pb-[2px] before:rounded"
          >
            {{ item.date
            }}<template v-if="item.month !== data?.[index - 1]?.month"
              >/{{ item.month }}</template
            >
          </span>
        </div>
      </div>
    </div>
  </q-page-sticky>

  <div class="pt-[123px]">
    <ScreenLoading v-if="loading" />

    <div v-else-if="data">
      <template
        v-if="
          dayjs(
            `${data[activeIndex].month}/${
              data[activeIndex].date
            }/${new Date().getFullYear()}`
          ).isToday()
        "
      >
        <!-- overtime -->
        <template
          v-for="[time, items] in (_tmp = splitOverTime(
              groupArray(data[activeIndex].items, 'time_release') as unknown as  Record<string, Awaited<ReturnType<typeof LichChieuPhim>>[0]['items']>
            ))[0]"
          :key="time"
        >
          <div class="text-grey text-[12px] mt-7 mb-2 flex items-center">
            <span class="h-[1px] w-[12px] bg-grey inline-block" />
            {{ dayjs(+time).format("HH:mm") }}
          </div>

          <CardVertical
            v-for="anime in items"
            :key="anime.name"
            :data="anime"
            class="mx-3"
          >
            <template v-slot:img-content>
              <BottomBlur />
            </template>
          </CardVertical>
        </template>

        <div
          class="w-full my-7 flex relative items-center justify-center before:content-DEFAULT before:absolute before:w-full before:h-[1px] before:bg-[rgba(200,200,200,0.5)] text-[14px]"
        >
          <span class="px-2 bg-dark-page z-2 text-grey"
            >Bây giờ:{{ dayjs().format("HH:mm") }}</span
          >
        </div>

        <!-- next on today -->
        <template v-for="[time, items] in _tmp[1]" :key="time">
          <div class="text-[12px] mt-7 mb-2 flex items-center">
            <span class="h-[1px] w-[12px] bg-[currentColor] inline-block" />
            {{ dayjs(+time).format("HH:mm") }}
          </div>

          <CardVertical
            v-for="anime in items"
            :key="anime.name"
            :data="anime"
            class="mx-3"
          >
            <template v-slot:img-content>
              <BottomBlur />
            </template>
          </CardVertical>
        </template>
      </template>
      <template
        v-else
        v-for="(items, time) in (groupArray(data[activeIndex].items, 'time_release') as Record<string, typeof item.items>)"
        :key="time"
      >
        <div class="text-[12px] mt-7 mb-2 flex items-center">
          <span class="h-[1px] w-[12px] bg-[currentColor] inline-block" />
          {{ dayjs(+time).format("HH:mm") }}
        </div>

        <CardVertical
          v-for="anime in items"
          :key="anime.name"
          :data="anime"
          class="mx-3"
        >
          <template v-slot:img-content>
            <BottomBlur />
          </template>
        </CardVertical>
      </template>
    </div>

    <ScreenError v-else />
  </div>
</template>

<script lang="ts" setup>
import BottomBlur from "components/BottomBlur.vue"
import CardVertical from "components/CardVertical.vue"
import ScreenError from "components/ScreenError.vue"
import ScreenLoading from "components/ScreenLoading.vue"
import groupArray from "group-array"
import { LichChieuPhim } from "src/apis/runs/lich-chieu-phim"
// Import Swiper styles
import "swiper/css"
import { dayTextToNum } from "src/logic/dayTextToNum"
import { ref } from "vue"
import { useRequest } from "vue-request"


const { loading, data } = useRequest(() => LichChieuPhim())

// eslint-disable-next-line functional/no-let
let _tmp: ReturnType<typeof splitOverTime>

function splitOverTime(
  items: Record<string, Awaited<ReturnType<typeof LichChieuPhim>>[0]["items"]>
) {
  const overWrite = Object.entries(items)
  const now = Date.now()

  const indexFirstItemNextTime =
    overWrite.findIndex(([time]) => {
      return +time > now
    }) >>> 0

  return [
    overWrite.slice(0, indexFirstItemNextTime),
    overWrite.slice(indexFirstItemNextTime),
  ]
}

const activeIndex = ref(0)
</script>
