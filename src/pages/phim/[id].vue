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
        <span
          class="inline-block w-1 h-1 rounded bg-[currentColor] mb-1 mx-1"
        />
        {{ data.seasonOf }}
      </h5>

      <span class="text-gray-400">Tác giả </span>
      <router-link :to="data.author.path" class="text-[rgb(28,199,73)]">
        {{ data.author.name }}
      </router-link>

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

      <div class="tags mt-1 text-[12px]">
        <router-link
          v-for="item in data.genre"
          :key="item.name"
          :to="item.path"
          class="text-[rgb(28,199,73)] mr-3"
        >
          #{{ item.name.replace(/ /, "_") }}
        </router-link>
      </div>

      <ul class="properties mt-1">
        <li>
          <span class="key">Studio: </span>
          <span class="value">{{ data.studio }}</span>
        </li>
      </ul>
    </div>

    <!--
      author
      follows
      language
      studio
      seasonOf
      trailer
      followed
    -->

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

const levels = ["N", "Tr", "T", "V"]
function formatView(view: number) {
  if (view < 1000) return view
  const index = levels.findIndex((item, index) => {
    view /= 1000

    if (view < 1000) return true
  })
  // while (view > 1000 && i < levels.length - 1) {
  //   view /= 1000
  //   i++
  // }

  return `${view.toFixed(2).replace(/\./, ",")}${levels[index]}`
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

<style lang="scss" scoped>
.key {
  color: rgb(149, 149, 149);
}
.value {
  color: rgb(230, 230, 230);
}
</style>
