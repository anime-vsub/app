<template>
  <q-card flat dense class="bg-transparent" @click="router.push(data.path)">
    <q-img
      :src="data.image"
      :ratio="280 / 400"
      :initial-ratio="744 / 530"
      class="!rounded-[4px]"
    >
      <div class="update-info-layer">
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
          <template v-else>
            Tập {{ data.process[0] }} / {{ data.process[1] ?? "??" }}
          </template>
        </span>
        <span v-else>Tập {{ data.chap }}</span>
      </div>
      <Quality
        v-if="data.quality"
        :class="trending ? 'right-0 absolute' : undefined"
        >{{ data.quality }}</Quality
      >
      <img
        v-if="trending"
        :src="ranks[trending]"
        class="h-[1.5rem]"
      />
    </q-img>
    <a v-if="!trending" href="#" class="line-clamp-2 min-h-10 mt-1">{{
      data.name
    }}</a>
    <div v-else class="flex items-center text-weight-medium">
      {{ data.rate }}

      <Star class="inline" />
    </div>
  </q-card>
</template>

<script lang="ts" setup>
import type { TPost } from "src/apis/__helpers__/getInfoTPost"
import { useRouter } from "vue-router"

import Quality from "./Quality.vue"
import Star from "./Star.vue"
import ranks from "src/logic/ranks"

const router = useRouter()

defineProps<{
  data: TPost
  trending?: number
}>()
</script>

<style lang="scss" scoped>
a {
  text-decoration: none;
  user-select: none;
  color: rgb(255, 255, 255);

  height: 40px;
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
  background-image: linear-gradient(
    0deg,
    rgba(10, 12, 15, 0.8) 0%,
    rgba(10, 12, 15, 0.74) 4%,
    rgba(10, 12, 15, 0.59) 17%,
    rgba(10, 12, 15, 0.4) 34%,
    rgba(10, 12, 15, 0.21) 55%,
    rgba(10, 12, 15, 0.06) 78%,
    rgba(10, 12, 15, 0) 100%
  );
  background-color: transparent;
  min-height: 60px;
  position: absolute;
  padding: {
    left: 8px;
    right: 10px;
    bottom: 10px;
    top: 40px;
  }
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 2;

  font-size: 14px;
  font-weight: 500;
  @media screen and (max-width: 1680px) {
    font-size: 12px;
  }
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
