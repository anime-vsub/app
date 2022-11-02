<template>
  <div class="text-center h-full absolute w-full">
<div class="pt-[140px]">

    <Icon icon="logos:airflow-icon" width="75" height="75" class="airflow-icon mx-auto"/>

    <h3 class="text-[24px] mt-10">Cần cài đặt Extension trợ giúp</h3>
    <p class="text-[14px] mt-3 text-gray-400 leading-normal">
      Ứng dụng này không thể hoạt động mà không có extension AnimeVsub Helper
<br>
Extension AnimeVsub Helper là cầu nối quan trọng để ứng dụng gửi các yêu cầu tới máy chủ.
<br>
<q-btn no-caps color='main' outline rounded class="mt-4" target="_blank" href="https://github.com/anime-vsub/extension-animevsub-helper">Cài đặt extension AnimeVsub Helper</q-btn>
    </p>
    <!-- <img src="~assets/ic_question_exit.png" width="278" /> -->
</div>
  </div>
</template>

<script  lang="ts" setup>
import { useHead } from "@vueuse/head"
import { Icon } from "@iconify/vue"

const title = "Lỗi • Cần cài đặt extension trợ giúp"
const description = "Ứng dụng này cần cài đặt extension trợ giúp để hoạt động bình thường."

useHead({
  title,
 description,
 meta: [
   { property: "og:title", content: title},
   { property: "og:description", content: description },
   {
     property: "og:url",
   },
 ],
 link: [
   {
     rel: "canonical",
   },
 ]
})
</script>


<style lang="scss" scoped>
  .airflow-icon {
    animation: rotate 19s linear infinite;
    @keyframes rotate {
      from {
        transform: rotate(0deg)
      }
      to {
        transform: rotate(360deg)
      }
    }
  }
  </style>
