<template>
  <div
    v-if="loading"
    class="absolute w-full h-[calc(100%+50px)] overflow-hidden loader"
  >
    <div class="swiper-hot">
      <q-responsive :ratio="aspectRatio" class="poster">
        <q-skeleton type="rect" width="100%" height="100%" />
      </q-responsive>
    </div>

    <div class="row text-grey text-[14px] mx-4 text-center mb-4">
      <div class="col-4 relative py-2">
        <q-skeleton type="circle" size="40px" class="mx-auto mb-2" />
        <q-skeleton
          type="text"
          width="3.5rem"
          height="1rem"
          class="mt-2 mx-auto"
        />
      </div>
      <div class="col-4 relative py-2">
        <q-skeleton type="circle" size="40px" class="mx-auto mb-2" />
        <q-skeleton
          type="text"
          width="3.5rem"
          height="1rem"
          class="mt-2 mx-auto"
        />
      </div>
      <div class="col-4 relative py-2">
        <q-skeleton type="circle" size="40px" class="mx-auto mb-2" />
        <q-skeleton
          type="text"
          width="3.5rem"
          height="1rem"
          class="mt-2 mx-auto"
        />
      </div>
    </div>

    <div class="px-4 mt-4">
      <div class="wpa-grid">
        <div class="ctnr">
          <SkeletonCard
            v-for="item in 12"
            :key="item"
            class="card-wrap inline-block"
          />
        </div>
      </div>
    </div>

    <div class="px-4 mt-4">
      <q-skeleton type="text" width="7rem" class="text-h6" />

      <SkeletonGridCard :count="6" />
    </div>

    <div class="px-4 mt-4">
      <q-skeleton type="text" width="7rem" class="text-h6" />

      <div class="wpa-grid">
        <div class="ctnr">
          <SkeletonCard
            v-for="item in 12"
            :key="item"
            class="card-wrap inline-block"
          />
        </div>
      </div>
    </div>

    <div class="px-4 mt-4">
      <q-skeleton type="text" width="7rem" class="text-h6" />

      <div class="wpa-grid">
        <q-skeleton type="text" width="100%" />
        <div class="ctnr">
          <SkeletonCard
            v-for="item in 12"
            :key="item"
            class="card-wrap inline-block"
          />
        </div>
      </div>
    </div>

    <div class="px-4 mt-4">
      <q-skeleton type="text" width="7rem" class="text-h6" />

      <SkeletonGridCard :count="6" />
    </div>
  </div>
  <q-pull-to-refresh v-else-if="data" @refresh="refresh">
    <div class="row text-grey text-[12px] mx-4 text-center mb-4">
      <router-link to="/muc-luc" class="col-4 relative py-2" v-ripple>
        <img src="~assets/icon_tool_alp.png" width="30" class="mx-auto mb-2" />
        <span class="mt-2">Mục lục</span>
      </router-link>
      <router-link to="/lich-chieu-phim" class="col-4 relative py-2" v-ripple>
        <img src="~assets/icon_tool_calc.png" width="30" class="mx-auto mb-2" />
        <span>Lịch chiếu</span>
      </router-link>
      <router-link to="/bang-xep-hang" class="col-4 relative py-2" v-ripple>
        <img src="~assets/icon_tool_rank.png" width="30" class="mx-auto mb-2" />
        <span>Bảng xếp hạng</span>
      </router-link>
    </div>

    <Component
      v-for="(item, i) in data"
      :key="i"
      :is="components[item.name]"
      v-bind="item.props"
    />
  </q-pull-to-refresh>
  <ScreenError
    v-else
    class="absolute"
    @click:retry="refreshAsync"
    :error="error"
  />
</template>

<script setup lang="ts">
// eslint-disable-next-line import/order
import { Index } from "src/apis/runs/index"
// eslint-disable-next-line import/order
import { useRequest } from "vue-request"

import ScreenError from "components/ScreenError.vue"
import SkeletonCard from "components/SkeletonCard.vue"
import SkeletonGridCard from "components/SkeletonGridCard.vue"
import CarouselTop from "components/index/CarouselTop.vue"
import GridCard from "components/index/GridCard.vue"
import List from "components/index/List.vue"
import ListRelease from "components/index/ListRelease.vue"
import type { CardData } from "src/components/Card.types"
import { useAliveScrollBehavior } from "src/composibles/useAliveScrollBehavior"

useAliveScrollBehavior()

const aspectRatio = 622 / 350

const components = {
  CarouselTop,
  GridCard,
  List,
  ListRelease,
}

const { data, loading, refreshAsync, error } = useRequest(() => Index())
async function refresh(done: () => void) {
  await refreshAsync()
  done()
}
</script>

<style lang="scss" scoped src="components/index/List.styles.scss"></style>
