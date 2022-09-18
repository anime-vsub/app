<template>
  <q-page v-if="data">
    <q-video
      :ratio="16 / 9"
      src="https://www.youtube.com/embed/k3_tw44QsZQ?rel=0"
      :poster="data.poster"
    />

    <div class="px-2 py-4">
      <h1 class="line-clamp-2 text-weight-medium py-0 my-0 text-[18px]">
        {{ data.name }}
      </h1>
      <h5 class="text-gray-400 text-weight-normal">
        {{ formatView(data.views) }} lượt xem
      </h5>

      <div class="text-[rgb(230,230,230)] mt-3">
        <Quality>{{ data.quality }}</Quality>
        <div class="divider"></div>
        {{ data.yearOf }}
        <div class="divider"></div>
        Cập nhật tới tập {{ data.duration.split("/")[0] }}
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
          {{ data.count_rate }} người đánh giá
        </span>
      </div>
    </div>

    <div style="white-space: pre">
      {{ JSON.stringify(data, null, 2) }}
    </div>
  </q-page>
</template>

<script lang="ts" setup>
import { Phim_Id } from "src/apis/phim/[id]"
import { useRequest } from "vue-request"
import html from "src/apis/__test__/data/phim/tonikaku-kawaii-a3860.txt?raw"
import Quality from "components/Quality.vue"
import Star from "components/Star.vue"

const { data } = useRequest(() => Phim_Id(html))

const levels = ["K", "M", "G", "T"]
function formatView(view: number) {
  let index = 0
  while (view > 1000 && i < levels.length - 1) {
    view /= 1000
    i++
  }

  return view
}
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
</style>
