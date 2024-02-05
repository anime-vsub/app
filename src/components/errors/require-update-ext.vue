<template>
  <div class="text-center h-full absolute w-full">
    <div class="pt-[140px]">
      <Icon
        icon="logos:airflow-icon"
        width="75"
        height="75"
        class="airflow-icon mx-auto"
      />

      <h3 class="text-[24px] mt-10">Cần cập nhật extension trợ giúp</h3>
      <p class="text-[14px] mt-3 text-gray-400 leading-normal">
        {{
          t("phien-ban-extension-cua-ban-da-qua-cu-hay-cap-nhat-no-de-tiep-tuc")
        }}
        <br />
        <q-btn
          no-caps
          color="main"
          outline
          rounded
          class="mt-4"
          target="_blank"
          :href="currentBrowser.href"
        >
          <Icon
            :icon="currentBrowser.icon"
            width="35"
            height="35"
            class="mr-1 my-1"
          />
          {{ t("cai-dat-extension-animevsub-helper") }}
          {{ t("cho") }} {{ currentBrowser.text }}
        </q-btn>
      </p>

      <div class="mt-5 children:px-3">
        <q-btn
          rounded
          stack
          no-caps
          v-for="{ icon, text, href } in browsers"
          :key="text"
          :href="href"
          target="_blank"
        >
          <Icon :icon="icon" width="55" height="55" />
          <span class="mt-[5px] text-white text-[16px] text-weight-regular">{{
            text
          }}</span>
        </q-btn>
      </div>
      <!-- <img src="~assets/ic_question_exit.png" width="278" /> -->
    </div>
  </div>
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"
import UAParser from "ua-parser-js"
import { computed } from "vue"
import { useI18n } from "vue-i18n"

const { t } = useI18n()

const browsers = [
  {
    icon: "logos:chrome",
    text: "Chrome",
    name: "Chrome",
    href: "https://github.com/anime-vsub/extension-animevsub-helper/blob/main/install-on-chrome.md"
  },
  {
    icon: "logos:firefox",
    text: "Firefox",
    name: "Firefox",
    href: "https://addons.mozilla.org/vi/firefox/addon/animevsub-helper/"
  },
  {
    icon: "logos:opera",
    text: "Opera",
    name: "Opera",
    href: "https://github.com/anime-vsub/extension-animevsub-helper/blob/main/install-on-chrome.md"
  },
  {
    icon: "logos:microsoft-edge",
    text: "Edge",
    name: "Edge",
    href: "https://microsoftedge.microsoft.com/addons/detail/endghpbficnpbadbdalhbpecpgdcojig"
  }
]
const currentBrowser = computed(() => {
  const currentBrowserName = new UAParser().getBrowser().name

  return (
    browsers.find((item) => item.name === currentBrowserName) ?? browsers[0]
  )
})
</script>

<style lang="scss" scoped>
.airflow-icon {
  animation: rotate 19s linear infinite;
  @keyframes rotate {
    from {
      transform: rotate(0deg);
    }
    to {
      transform: rotate(360deg);
    }
  }
}
</style>
