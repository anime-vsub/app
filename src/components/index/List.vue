<template>
  <div class="block px-4 mt-4">
    <router-link
      v-if="to"
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
    <span v-else-if="name">{{ name }}</span>

    <div class="wpa-grid">
      <div class="ctnr">
        <Card
          v-for="(item, index) in items"
          :key="item.name"
          class="card-wrap"
          :data="item"
          :quality-float-right="qualityFloatRight"
          :trending="rank ? index + 1 : undefined"
        />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"

import Card from "../Card.vue"

import type { ListProps } from "./props.types";

defineProps<ListProps>()
</script>

<style lang="scss" scoped src="./List.styles.scss"></style>
