<template>
  <router-link :to="data.path" v-ripple class="relative flex flex-nowrap">
    <div>
      <q-img-custom
        no-spinner
        :src="forceHttp2(data.image)"
        referrerpolicy="no-referrer"
        :ratio="280 / 400"
        width="110px"
        class="rounded-lg"
      >
        <slot name="img-content" />
      </q-img-custom>
    </div>

    <div class="flex-1 h-full overflow-hidden pl-3 py-[3px] text-[#9a9a9a]">
      <div
        class="text-[16px] text-[#eee] leading-snug"
        :class="{
          'line-clamp-3': threeLine,
          'line-clamp-2': !threeLine
        }"
      >
        {{ data.name }}
      </div>

      <div v-if="data.process" class="mt-2">
        <template v-if="data.year"
          >{{ data.year }} <span class="mx-1">|</span>
        </template>
        {{ t("tap-_chap", [data.process]) }}
      </div>

      <p v-if="data.description" class="text-grey mt-2 line-clamp-2">
        {{ data.description }}
      </p>

      <div
        v-if="showStar"
        class="flex items-center text-white font-weight-medium mt-2"
      >
        <span class="font-weight-normal text-gray-400"
          >{{ t("danh-gia") }}
        </span>
        <Star class="mr-1 ml-1" :label="data.rate" />
      </div>

      <q-btn
        dense
        color="primary"
        no-caps
        class="px-2 mt-1"
        @click.stop.prevent="addToFavorite"
        v-if="false"
      >
        <Icon icon="bi:bookmark-plus" />
        <!-- <Icon icon="bi:bookmark-check" /> -->

        {{ t("yeu-thich") }}
      </q-btn>
    </div>
  </router-link>
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"
import QImgCustom from "components/QImgCustom"
import { forceHttp2 } from "src/logic/forceHttp2"
import { useI18n } from "vue-i18n"

import Star from "./Star.vue"

const { t } = useI18n()
defineProps<{
  data: {
    path: string
    image: string
    name: string
    year?: string | number
    process: string
    description?: string
    rate?: number
  }
  threeLine?: boolean
  showStar?: boolean
}>()

// eslint-disable-next-line @typescript-eslint/no-empty-function
function addToFavorite() {}
</script>
