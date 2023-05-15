<template>
  <div>
    <img
      v-if="!noImage"
      src="~assets/img_tips_error_not_loin.png"
      width="240"
      class="mx-auto"
    />
    <div class="text-h4 mt-4">Bạn là Robot?</div>

    <div
      class="text-h6 text-[16px] px-2 leading-normal text-gray-200 text-weight-regular mt-4"
    >
      Yêu cầu gửi tới máy chủ bị nghi ngờ là robot vui lòng xác thực bạn là con
      người và thử lại.
      <div class="mt-2">
        <q-btn rounded outline no-caps color="blue" @click="openAnimeVietsub"
          >Mở AnimeVietsub để xác thực</q-btn
        >
      </div>
      <small class="mt-3 text-[12px] text-gray-400"
        >Sau khi xác thực hãy nhấn vào nút bên dưới</small
      >
      <br>
      <small class="mt-3 text-[12px] text-gray-400">Nếu sự cố vẫn tiếp diễn bạn có thể cần một VPN trỏ tới Việt Nam</small>
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
import { Browser } from "@capacitor/browser"
import { C_URL } from "src/constants"
import { useI18n } from "vue-i18n"

defineProps<{
  noImage?: boolean
  retry: () => void
}>()

const { t } = useI18n()

function openAnimeVietsub() {
  Browser.open({ url: C_URL })
}
</script>

<style></style>
