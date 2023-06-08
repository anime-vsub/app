<template>
  <swiper
    :slides-per-view="1"
    :space-between="0"
    :modules="[Autoplay]"
    :autoplay="{
      delay: 5000,
      disableOnInteraction: false,
    }"
    class="swiper-hot"
    :class="{
      'swiper-hoz': hoz,
    }"
  >
    <swiper-slide
      v-for="(item, index) in items"
      :key="index"
      v-ripple
      @click="router.push(item.path)"
    >
      <q-img-custom
        no-spinner
        :ratio="aspectRatio"
        :src="forceHttp2(item.image)"
        referrerpolicy="no-referrer"
        class="poster"
      />
      <div class="drop-left"></div>
      <div class="drop-center"></div>
      <div class="drop-right"></div>
      <div v-if="hoz" class="bg-blur">
        <q-img-custom
          no-spinner
          :ratio="aspectRatio"
          :src="forceHttp2(item.image)"
          referrerpolicy="no-referrer"
        />
      </div>
      <div v-if="!hoz" class="info">
        <div class="flex line-clamp-2 items-center">
          <Quality>{{ item.quality }}</Quality>
          <div class="text-weight-medium">{{ item.name }}</div>
        </div>
        <div class="focus-item-info">
          <span class="focus-item-score">
            <Star />
            {{ item.rate }}
          </span>
          <span class="focus-item-year">{{ item.year?.name }}</span>
          <span class="focus-item-update">
            {{ item.process }}
          </span>
        </div>
        <div class="focus-item-tags" v-if="item.genre && item.genre.length > 0">
          <template v-for="(tag, index) in item.genre" :key="tag.name">
            <router-link class="c--main" :to="tag.path">{{
              tag.name
            }}</router-link>
            <template v-if="index < item.genre.length - 1">, </template>
          </template>
        </div>
        <div class="focus-item-desc">
          {{ item.description }}
        </div>
      </div>
      <div v-else class="info text-center pb-3">
        <h2 class="font-weight-medium text-[18px] leading-normal">
          {{ item.name }}
        </h2>
        <h3 v-if="item.originName" class="text-gray-300 mb-2 leading-normal">
          {{ item.originName }}
        </h3>

        <p v-if="item.isTrailer">
          <Quality>Trailer</Quality>
        </p>
        <p v-else>Tập {{ item.process }}</p>
      </div>
    </swiper-slide>
  </swiper>
</template>

<script setup lang="ts">
// eslint-disable-next-line import/order
import Star from "components/Star.vue"
import "dayjs/locale/vi"
import QImgCustom from "components/QImgCustom"
import Quality from "components/Quality.vue"
import { forceHttp2 } from "src/logic/forceHttp2"
import { Autoplay } from "swiper"
// eslint-disable-next-line import/order
import { Swiper, SwiperSlide } from "swiper/vue"

// Import Swiper styles
import "swiper/css"
import "swiper/css/pagination"
import "swiper/css/autoplay"
import "swiper/css/grid"
import { useRouter } from "vue-router"

import type { CarouselTopProps } from "./props.types"

const props = defineProps<CarouselTopProps>()

const router = useRouter()

// eslint-disable-next-line vue/no-setup-props-destructure
const aspectRatio =props.aspectRatio
</script>

<style lang="scss" scoped src="./CarouselTop.styles.scss"></style>
