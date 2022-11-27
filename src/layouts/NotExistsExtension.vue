<template>
  <div class="text-center h-full absolute w-full">
    <div class="pt-[140px]">
      <Icon
        icon="logos:airflow-icon"
        width="75"
        height="75"
        class="airflow-icon mx-auto"
      />

      <h3 class="text-[24px] mt-10">
        {{ t("can-cai-dat-extension-tro-giup") }}
      </h3>
      <p class="text-[14px] mt-3 text-gray-400 leading-normal">
        {{
          t(
            "ung-dung-nay-khong-the-hoat-dong-ma-khong-co-extension-animevsub-helper"
          )
        }}
        <br />
        {{
          t(
            "extension-animevsub-helper-la-cau-noi-quan-trong-de-ung-dung-gui-cac-yeu-cau-toi-may-chu"
          )
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
          v-for="{ icon, text } in browsers"
          :key="text"
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
import { useHead } from "@vueuse/head"
import UAParser from "ua-parser-js"
import { computed } from "vue"
import { useI18n } from "vue-i18n"

const { t } = useI18n()

const title = computed(() => t("loi-can-cai-dat-extension-tro-giup"))
const description = computed(() =>
  t("ung-dung-nay-can-cai-dat-extension-tro-giup-de-hoat-dong-binh-thuong")
)
useHead(
  computed(() => ({
    title,
    description,
    meta: [
      { property: "og:title", content: title },
      { property: "og:description", content: description },
      {
        property: "og:url",
      },
    ],
    link: [
      {
        rel: "canonical",
      },
    ],
  }))
)

const browsers = [
  {
    icon: "logos:chrome",
    text: "Chrome",
    name: "Chrome",
    href: "https://github.com/anime-vsub/extension-animevsub-helper/blob/main/install-on-chrome.md",
  },
  {
    icon: "logos:firefox",
    text: "Firefox",
    name: "Firefox",
    href: "https://addons.mozilla.org/vi/firefox/addon/animevsub-helper/",
  },
  {
    icon: "logos:opera",
    text: "Opera",
    name: "Opera",
    href: "https://github.com/anime-vsub/extension-animevsub-helper/blob/main/install-on-chrome.md",
  },
  {
    icon: "logos:microsoft-edge",
    text: "Edge",
    name: "Edge",
    href: "https://microsoftedge.microsoft.com/addons/detail/endghpbficnpbadbdalhbpecpgdcojig",
  },
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
