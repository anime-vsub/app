<template>
  <router-link :to="data.path">
    <q-card
      flat
      dense
      class="bg-transparent"
      @mousemove="data.description ? (eventMouseoverCard = $event) : undefined"
    >
      <q-img-custom
        no-spinner
        :src="forceHttp2(data.image)"
        referrerpolicy="no-referrer"
        :ratio="280 / 400"
        :initial-ratio="744 / 530"
        class="!rounded-[4px]"
        ref="imgRef"
      >
        <BottomBlur class="update-info-layer">
          <template v-if="trending">
            <!-- <div class="text-[30px]">#{{ trending }}</div> -->
            <div class="card-title line-clamp-2 h-[42px]">
              {{ data.name }}
            </div>
          </template>

          <span v-if="!data.chap">
            <template v-if="data.process">{{
              t("tap-_chap", [data.process])
            }}</template>
            <template v-else-if="data.quality">{{ t("movie") }}</template>
          </span>
          <span v-else-if="data.chap === 'Full_Season'">{{
            t("full-season")
          }}</span>
          <span v-else>{{ t("tap-_chap", [data.chap]) }}</span>
        </BottomBlur>
        <Quality
          v-if="data.quality"
          class="top-2"
          :class="
            trending || qualityFloatRight ? 'right-0 absolute' : undefined
          "
          >{{ data.quality }}</Quality
        >
        <img v-if="trending" :src="ranks[trending - 1]" class="h-[1.5rem]" />
      </q-img-custom>
      <span v-if="!trending" class="a line-clamp-2 min-h-10 mt-1">{{
        data.name
      }}</span>
      <div v-else class="flex items-center text-weight-medium">
        {{ data.rate }}

        <Star class="inline" />
      </div>
    </q-card>

    <q-menu
      v-if="data.description"
      ref="menuRef"
      no-parent-event
      anchor="center right"
      self="center left"
      touch-position
      class="bg-[rgba(20,22,27,0.98)] scrollbar-custom overflow-x-visible max-w-[280px] md:max-w-[320px]"
      @mouseover.stop
    >
      <q-card class="bg-transparent" ref="cardMenuRef">
        <q-card-section>
          <h3
            class="text-subtitle1 font-weight-medium line-clamp-3 leading-normal"
          >
            {{ data.name }}
          </h3>
          <h4 class="text-grey-5 text-[13px] leading-normal">
            <template v-if="data.views">{{
              t("formatview-data-views-luot-xem", [formatView(data.views)])
            }}</template>
            <template v-if="data.time_release !== undefined">
              &bull;
              <template v-if="data.time_release">{{
                t("cong-chieu-trong-_ago", [dayjs(data.time_release).fromNow()])
              }}</template
              ><template v-else>{{
                t("ngay-cong-chieu-chua-xac-dinh")
              }}</template></template
            >
          </h4>

          <div class="flex items-center mt-3">
            <Quality v-if="data.quality" class="!mr-0">{{
              data.quality
            }}</Quality>
            <span class="hr-vertical" />
            {{ data.year }}
            <span class="hr-vertical" />
            <Star :label="data.rate" class="inline-flex" />
            <span class="hr-vertical" />
            {{ data.process }}
          </div>
          <!-- <div class="text-gray-400 mt-2">{{ t("cap-nhat-toi-tap-_duration", [data.process]) }}</div> -->

          <div class="mt-2 text-[#eee] font-weight-medium">
            {{ t("gioi-thieu") }}
          </div>
          <p class="text-gray-400">{{ data.description }}</p>

          <div class="tags mt-2">
            <router-link
              v-for="item in data.genre"
              :key="item.name"
              :to="item.path"
              class="text-[rgb(28,199,73)]"
            >
              {{ t("tag-_val", [item.name.replace(/ /, "_")]) }}
            </router-link>
          </div>
        </q-card-section>
      </q-card>
    </q-menu>
  </router-link>
</template>

<script lang="ts" setup>
import type { MaybeComputedRef } from "@vueuse/core"
import { useElementHover } from "@vueuse/core"
import BottomBlur from "components/BottomBlur.vue"
import QImgCustom from "components/QImgCustom"
import type { QImg } from "quasar"
import { debounce, QCard, QCardSection, QMenu } from "quasar"
import type { TPost } from "src/apis/parser/__helpers__/getInfoTPost"
import dayjs from "src/logic/dayjs"
import { forceHttp2 } from "src/logic/forceHttp2"
import { formatView } from "src/logic/formatView"
import ranks from "src/logic/ranks"
import { ref, watch } from "vue"
import { useI18n } from "vue-i18n"
import { RouterLink } from "vue-router"

import Quality from "./Quality.vue"
import Star from "./Star.vue"

const { t } = useI18n()

const props = defineProps<{
  data: TPost
  trending?: number
  qualityFloatRight?: boolean
}>()

const menuRef = ref<QMenu>()

const imgRef = ref<QImg>()
const cardMenuRef = ref<QCard>()

const eventMouseoverCard = ref<Event | null>(null)

if (props.data.description) {
  const mouseInCard = useElementHover(
    imgRef as unknown as MaybeComputedRef<EventTarget>
  )
  const mouseInCardMenu = useElementHover(
    cardMenuRef as unknown as MaybeComputedRef<EventTarget>
  )

  const showMenu = debounce(() => {
    if (eventMouseoverCard.value) menuRef.value?.show(eventMouseoverCard.value)
  }, 700)
  watch(
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    [mouseInCard, mouseInCardMenu] as unknown as any,
    debounce(([outsideCard, outsideCardMenu]) => {
      showMenu.cancel()
      if (outsideCard || outsideCardMenu) showMenu()
      else menuRef.value?.hide()
    }, 10)
  )
}
</script>

<style lang="scss" scoped>
.a {
  text-decoration: none;
  user-select: none;
  color: rgb(255, 255, 255);

  min-height: 42px;
  position: relative;
  padding: 0.1rem 0px 0px;
  padding: {
    left: 4px;
    right: 4px;
  }
  font-size: 14px;
  transition: color 0.3s ease 0s;
  line-height: 1.5;

  font-weight: 500;

  @media (max-width: 720px) {
    font-weight: 400;
  }
}

.update-info-layer {
  span {
    color: rgb(255, 255, 255);
    letter-spacing: 0px;
  }
  .star {
    position: absolute;
    right: 8px;
    // right: 10px;
    bottom: 10px;
  }
}
.card-title {
  color: rgb(255, 255, 255);
  font-weight: 500;
  font-size: 14px;
}

.hr-vertical {
  margin: 0px 10px;
  height: 10px;
  width: 2px;
  display: inline-block;
  background: rgba(255, 255, 255, 0.2);
}

.tags {
  > * {
    @apply mr-3 inline-block;
  }

  @media (max-width: 767px) {
    font-size: 13px;

    > * {
      @apply mr-1 mt-1;
    }
  }
}
</style>
