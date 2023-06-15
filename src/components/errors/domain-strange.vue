<template>
  <div>
    <transition-group
      v-if="!noImage"
      name="q-transition--fade"
      tag="div"
      class="flex items-center justify-center"
    >
      <Icon v-if="rand === 0" icon="twemoji:joker" width="84" height="84" />

      <Icon
        v-else-if="rand === 1"
        icon="noto:strawberry"
        width="84"
        height="84"
      />

      <Icon v-else icon="logos:strapi-icon" width="84" height="84" />
    </transition-group>

    <div class="text-h4 mt-4">{{ t("ten-mien-chua-duoc-cap-phep") }}</div>

    <div class="text-h6 text-weight-regular mt-4">
      <p class="text-gray-400">
        {{ t("ten-mien") }} <span class="text-blue">{{ hostName }}</span>
        {{ t("chua-duoc-extension-cap-phep-truy-cap-co-the") }}
        <a
          href="https://github.com/anime-vsub/extension-web-helper"
          target="_blank"
          class="text-blue-500"
          >{{ t("tien-ich-animevsub-helper") }}</a
        >
        {{ t("chua-duoc-cap-nhat-hoac-trang-web-nay-dang-gia-mao") }}
        <a href="https://animevsub.eu.org" class="text-blue-500">AnimeVsub</a>.
      </p>

      <p class="mt-3 text-gray-400 text-weight-medium">
        {{
          t(
            "vui-long-thu-cap-nhat-extension-neu-su-co-khong-duoc-khac-phuc-vui-long-gui-email-den"
          )
        }}
        <a
          :href="`mailto:ogmo2r3q@duck.com?subject=Phản hồi ứng dụng web ${hostName}`"
          class="text-blue-500"
          >ogmo2r3q@duck.com</a
        >
      </p>

      <div>
        <span class="text-weight-medium text-gray-500"
          >{{ t("phien-ban-extension") }}
        </span>
        {{ Http.version }}
      </div>
    </div>

    <q-btn
      class="q-mt-md"
      no-caps
      outline
      rounded
      @click="retry"
      style="color: #00be06"
      >{{ t("thu-lai") }}</q-btn
    >
  </div>
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"
import { useIntervalFn } from "@vueuse/core"
import { Http } from "client-ext-animevsub-helper"
import { ref } from "vue"
import { useI18n } from "vue-i18n"

defineProps<{
  noImage?: boolean
  retry: () => void
}>()

const { t } = useI18n()

const rand = ref(Math.round(Math.random() * 2))
useIntervalFn(() => {
  rand.value = Math.round(Math.random() * 3)
}, 3_000)
const hostName = location.hostname
</script>
