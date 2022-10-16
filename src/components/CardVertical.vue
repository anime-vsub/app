<template>
  <router-link :to="data.path" v-ripple class="relative flex flex-nowrap">
    <div>
      <q-img
        no-spinner
        :src="data.image"
        :ratio="280 / 400"
        width="110px"
        class="rounded-lg"
      >
        <slot name="img-content" />
      </q-img>
    </div>

    <div class="flex-1 h-full overflow-hidden pl-3 py-[3px] text-[#9a9a9a]">
      <div class="text-[16px] line-clamp-2 text-[#eee] leading-snug">
        {{ data.name }}
      </div>

      <div class="mt-2">
        <template v-if="data.year"
          >{{ data.year }} <span class="mx-1">|</span> </template
        >Tập
        {{ data.process }}
      </div>

      <p v-if="data.description" class="text-grey mt-2 line-clamp-2">
        {{ data.description }}
      </p>

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

        Yêu thích
      </q-btn>
    </div>
  </router-link>
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"

defineProps<{
  data: {
    path: string
    image: string
    name: string
    year?: string | number
    process: string
    description?: string
  }
}>()

// function addToFavorite() {}
</script>
