<template>
  <router-link :to="data.path">
    <q-card flat dense class="bg-transparent">
      <q-img
        no-spinner
        :src="forceHttp2(data.image)"
        :ratio="280 / 400"
        :initial-ratio="744 / 530"
        referrerpolicy="no-referrer"
        class="!rounded-[4px]"
      >
        <BottomBlur class="update-info-layer">
          <template v-if="trending">
            <!-- <div class="text-[30px]">#{{ trending }}</div> -->
            <div class="card-title line-clamp-2 h-[42px]">
              {{ data.name }}
            </div>
          </template>

          <span v-if="!data.chap">
            <template v-if="data.process[0] === data.process[1]">
              {{ data.process[0] }} tập
            </template>
            <template v-else> Tập {{ data.process }} </template>
          </span>
          <span v-else>Tập {{ data.chap }}</span>
        </BottomBlur>
        <Quality
          v-if="data.quality"
          :class="trending ? 'right-0 absolute' : undefined"
          >{{ data.quality }}</Quality
        >
        <img v-if="trending" :src="ranks[trending - 1]" class="h-[1.5rem]" />
      </q-img>
      <span v-if="!trending" class="a line-clamp-2 min-h-10 mt-1">{{
        data.name
      }}</span>
      <div v-else class="flex items-center text-weight-medium">
        {{ data.rate }}

        <Star class="inline" />
      </div>
    </q-card>
  </router-link>
</template>

<script lang="ts" setup>
import BottomBlur from "components/BottomBlur.vue"
import type { TPost } from "src/apis/parser/__helpers__/getInfoTPost"
import { forceHttp2 } from "src/logic/forceHttp2"
import ranks from "src/logic/ranks"

import Quality from "./Quality.vue"
import Star from "./Star.vue"

defineProps<{
  data: TPost
  trending?: number
}>()
</script>

<style lang="scss" scoped>
.a {
  text-decoration: none;
  user-select: none;
  color: rgb(255, 255, 255);

  // height: 40px;
  position: relative;
  padding: 0.1rem 0px 0px;
  padding: {
    left: 4px;
    right: 4px;
  }
  font-size: 14px;
  transition: color 0.3s ease 0s;

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
</style>
