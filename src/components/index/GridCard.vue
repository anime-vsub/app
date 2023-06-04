<template>
  <div class="block px-4 mt-4">
    <router-link
    v-if=to
      :to="to"
      class="text-subtitle1 text-weight-normal mx-2 flex items-center justify-between"
    >
      {{ name }}

      <Icon
        icon="fluent:chevron-right-24-regular"
        class="text-grey"
        width="18"
        height="18"
      />
    </router-link>

    <GridCard :items="items" />
  </div>
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"

import GridCard from "../GridCard.vue"

import type { GridCardProps } from "./props.types";

defineProps<GridCardProps>()
</script>
