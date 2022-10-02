<template>
  <router-link :to="data.path" v-ripple class="relative flex flex-nowrap">
    <div>
      <q-img
        :src="data.image"
        :ratio="280 / 400"
        width="110px"
        class="rounded-lg"
      />
    </div>

    <div class="flex-1 h-full overflow-hidden pl-3 py-[3px] text-[#9a9a9a]">
      <div class="text-[16px] line-clamp-2 text-[#eee] leading-snug">
        {{ data.name }}
      </div>

      <div class="mt-2">
        {{ data.year }} <span class="mx-1">|</span> Tập
        {{ data.process.join("/") }}
      </div>

      <p class="text-grey mt-2 line-clamp-2">{{ data.description }}</p>

      <q-btn
        dense
        color="primary"
        no-caps
        class="px-2 mt-1"
        @click.stop.prevent
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
  data: ReturnType<typeof getInfoTPost>
}>()
</script>
