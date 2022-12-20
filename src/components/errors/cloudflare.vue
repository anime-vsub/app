<template>
  <div>

    <transition-group v-if="!noImage" name="q-transition--fade" tag="div" class="flex items-center justify-center">
      <Icon v-if="rand === 0" icon="vscode-icons:file-type-aurelia" width="84" height="84" />

      <Icon v-else-if="rand === 1" icon="vscode-icons:file-type-playwright" width="84" height="84" />

      <Icon v-else icon="vscode-icons:file-type-ovpn" width="84" height="84" />
    </transition-group>


    <div class="text-h4 mt-4">
      IP của bạn tới từ quốc gia chưa được hỗ trợ
    </div>

    <div class="text-h6 text-weight-regular mt-4">
      <p class="opacity-40">
        Hiện tại AnimeVsub chỉ hỗ trợ các địa chỉ IP tới từ Việt Nam. Nếu bạn
        đang không ở Việt Nam hãy sử dụng các dịch vụ VPN để có được địa chỉ IP
        từ Việt Nam
      </p>

      <p class="mt-3 text-gray-400 text-weight-medium">
        Bạn có thể sử dụng <a
          href="https://chrome.google.com/webstore/detail/urban-vpn-proxy/eppiocemhmnlbhjplcgkofciiegomcon"
          target="_blank" class="text-blue">Urban VPN</a>
        là một VPN miễn phí
      </p>

      <div v-if="myip">
        <span class="text-weight-medium text-gray-500">IP của bạn: </span>
        {{ myip.ip }} ({{ myip.country }} <img :src="`https://flagsapi.com/${myip.cc.toUpperCase()}/flat/64.png`" :ratio="1" class="inline h-7" />)
      </div>
    </div>

    <q-btn class="q-mt-md" no-caps outline rounded @click="retry" style="color: #00be06">{{ t("thu-lai") }}</q-btn>
  </div>

</template>

<script lang="ts" setup>
  import {
    useIntervalFn, computedAsync
  } from "@vueuse/core"
  import { Icon } from "@iconify/vue"
import { useI18n } from "vue-i18n"
import { ref } from "vue"
import { get } from "src/logic/http"

  defineProps < {

    noImage?: boolean
    retry: () => void
  } > ()

  const {t}= useI18n()

  const rand = ref(Math.round(Math.random() * 2))
  useIntervalFn(() => {
    rand.value = Math.round(Math.random() * 3)
  }, 3_000)


  const myip = computedAsync<{
    ip: string
    country: string
    cc: string
  } | null>(() => {
    return fetch("https://api.myip.com/").then(res => {
      if (res.ok) return res.json()
      return null
    })
    .catch(() => {
      return get({
        url: "https://api.myip.com/",
        responseType: "json",
      }).then(res=>res.data)
    })
  }, null)
</script>

<style>
</style>
