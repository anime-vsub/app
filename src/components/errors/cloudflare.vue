<template>
  <div>
    <transition-group
      v-if="!noImage"
      name="q-transition--fade"
      tag="div"
      class="flex items-center justify-center"
    >
      <Icon
        v-if="rand === 0"
        icon="vscode-icons:file-type-aurelia"
        width="84"
        height="84"
      />

      <Icon
        v-else-if="rand === 1"
        icon="vscode-icons:file-type-playwright"
        width="84"
        height="84"
      />

      <Icon v-else icon="vscode-icons:file-type-ovpn" width="84" height="84" />
    </transition-group>

    <div class="text-h4 mt-4">
      {{ t("ip-cua-ban-toi-tu-quoc-gia-chua-duoc-ho-tro") }}
    </div>

    <div class="text-h6 text-weight-regular mt-4">
      <p class="opacity-40">
        {{ t("msg-caption-country-not-support") }}
      </p>

      <p class="mt-3 text-gray-400 text-weight-medium">
        {{ t("ban-co-the-su-dung") }}
        <a
          href="https://chrome.google.com/webstore/detail/urban-vpn-proxy/eppiocemhmnlbhjplcgkofciiegomcon"
          target="_blank"
          class="text-blue"
          >Urban VPN</a
        >
        {{ t("la-mot-vpn-mien-phi") }}
      </p>

      <div v-if="myip">
        <span class="text-weight-medium text-gray-500"
          >{{ t("ip-cua-ban") }}
        </span>
        {{ myip.ip }} ({{ myip.country }}
        <img
          :src="`https://flagsapi.com/${myip.cc.toUpperCase()}/flat/64.png`"
          :ratio="1"
          class="inline h-7"
        />)
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
import { computedAsync, useIntervalFn } from "@vueuse/core"
import { get } from "src/logic/http"
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

const myip = computedAsync<{
  ip: string
  country: string
  cc: string
} | null>(() => {
  return fetch("https://api.myip.com/")
    .then((res) => {
      if (res.ok) return res.json()
      return null
    })
    .catch(() => {
      return get({
        url: "https://api.myip.com/",
      // eslint-disable-next-line promise/no-nesting
      }).then((res) => JSON.parse(res.data))
    })
}, null)
</script>

<style></style>
