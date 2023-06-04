<template>
  <div class="block px-4 mt-4">
    <router-link
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

    <div class="wpa-grid">
      <div class="ctnr">
        <div v-for="item in items" :key="item.name" class="relative card-wrap">
          <div class="coming_soon-timeline absolute top-0 left-0">
            <div class="coming_soon-line"></div>
            <div class="coming_soon-dot"></div>
            <div class="coming_soon-time-wrapper">
              <div
                v-if="
                  (tmp = item.time_release ? dayjs(item.time_release) : null)
                "
              >
                <!-- if in today or tomorrow -->
                <template v-if="(isTodayF = tmp.isToday()) || tmp.isTomorrow()">
                  <div class="coming_soon-text-date">
                    {{ tmp.format("HH:mm") }}
                  </div>
                  <div class="coming_soon-text-day">
                    <template v-if="isTodayF"> Hôm nay </template>
                    <template v-else> Ngày mai </template>
                  </div>
                </template>
                <template v-else>
                  <div class="coming_soon-text-date">
                    {{
                      tmp.format(
                        tmp.year() === new Date().getFullYear()
                          ? "M-DD"
                          : "YYYY-MM-DD"
                      )
                    }}
                  </div>
                  <div class="coming_soon-text-day capitalize">
                    {{ tmp.locale("vi").format("dddd") }}
                  </div>
                </template>

                <!-- <template v-else>{{ tmp.format("DD/MM") }}</template> -->
              </div>
              <span v-else class="coming_soon-text-unknown">Sắp chiếu</span>
            </div>
          </div>

          <Card :data="item" />
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"
import dayjs from "src/logic/dayjs"

import Card from "../Card.vue"

import type { ListReleaseProps } from "./props.types";
// eslint-disable-next-line functional/no-let, @typescript-eslint/no-explicit-any
let tmp: any
// eslint-disable-next-line functional/no-let, prefer-const
let isTodayF = false


defineProps<ListReleaseProps>()
</script>

<style lang="scss" scoped src="./ListRelease.styles.scss" />
