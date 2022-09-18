<template>
  <q-page v-if="data">
    <q-video
      :ratio="16 / 9"
      src="https://www.youtube.com/embed/k3_tw44QsZQ?rel=0"
      :poster="data.poster"
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

    <div v-if="chapters?.update" class="text-gray-300 px-2">
      Tập mới cập nhật lúc {{ chapters.update }}
    </div>

    <q-btn flat no-caps class="w-full !px-2 mt-4">
      <div class="flex items-center justify-between text-subtitle2 w-full">
        Tập

        <span class="flex items-center text-gray-300 font-weight-normal">
          Trọn bộ <q-icon name="chevron_right" class="mr-[-8px]"></q-icon>
        </span>
      </div>
    </q-btn>
    <div class="relative mx-2">
      <EndOfBackdrop
        class="absolute !h-full top-0 left-0"
        reverse
        v-if="statusChaptersScroll !== 'start'"
      />
      <div class="overflow-x-scroll scrollbar-hide" @scroll="onChaptersScroll">
        <div class="whitespace-nowrap">
          <button
            v-for="item in chapters.chaps"
            :key="item.name"
            class="btn-chap"
          >
            {{ item.name }}
          </button>
        </div>
      </div>
      <EndOfBackdrop
        class="absolute !h-full top-0 right-0"
        v-if="
          statusChaptersScroll !== 'end' && statusChaptersScroll !== 'startImp'
        "
      />
    </div>

    <!-- bottom sheet -->

    <div
      class="h-[calc(100%-(9/16*100vw))] bottom-0 fixed left-0 w-full bg-dark"
    >
      <div class="flex items-center justify-between text-subtitle1 px-2 py-2">
        Season
        <q-btn dense icon="close" />
      </div>

      <div>
        <button class="chap-name">TV Show 1</button>
        <button class="chap-name active">TV Show 2</button>
      </div>
    </div>
    <!--
      trailer
      toPut
      followed
    -->
  </q-page>
</template>

<style lang="scss" scoped>
.btn-chap {
  display: inline-block;
  @apply h-8 rounded-[2px] text-4 border border-[rgb(35,37,43)] text-[14px] px-2 mr-2;
  line-height: 40px;
  white-space: nowrap;
  text-align: center;

  &.active {
    color: rgb(28, 199, 73);
    border-color: rgb(28, 199, 73);
  }
}
.chap-name {
  height: 20px;
  text-align: center;
  font-size: 14px;
  color: rgb(130, 131, 135);
  font-weight: 500;
  @apply px-2;
  &.active {
    color: rgb(0, 190, 6);
  }
  &:after {
    content: "";
    width: 100%;
    height: 2px;
    border-radius: 1px;
    bottom: 0px;
    margin-top: 8px;
    background: rgb(0, 190, 6);
  }
}
</style>

<script lang="ts" setup>
import { Phim_Id } from "src/apis/phim/[id]"
import { Phim_Id_Chap } from "src/apis/phim/[id]/[chap]"
import { useRequest } from "vue-request"
import html from "src/apis/__test__/data/phim/tonikaku-kawaii-a3860.txt?raw"
import htmlx from "src/apis/__test__/data/phim/tonikaku-kawaii-a3860/xem-phim-72839.txt?raw"
import Quality from "components/Quality.vue"
import Star from "components/Star.vue"
import EndOfBackdrop from "components/EndOfBackdrop.vue"
import { ref } from "vue"
import { debounce } from "quasar"

const { data } = useRequest(() => Phim_Id(html))
const { data: chapters } = useRequest(() => Phim_Id_Chap(htmlx))

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

const statusChaptersScroll = ref<"start" | "startImp" | "end" | "pending">(
  "start"
)
const onChaptersScroll = debounce(function ({ target }: MouseEvent) {
  const { scrollLeft, offsetWidth, scrollWidth } = target

  if (scrollLeft === 0) {
    statusChaptersScroll.value =
      scrollWidth <= offsetWidth ? "startImp" : "start"

    return
  }
  if (offsetWidth + scrollLeft === scrollWidth) {
    statusChaptersScroll.value = "end"

    return
  }

  statusChaptersScroll.value = "pending"
}, 70)

const tab = ref("mails")
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
