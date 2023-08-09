<template>
  <div class="text-center h-full absolute w-full">
    <div v-if="loading" class="pt-[140px]">
      <h3 class="text-[24px] mt-10">Đang kiểm tra quyền khởi chạy</h3>
      <p class="text-[14px] mt-3 text-gray-400 leading-normal">
        <q-spinner color="main" size="40px" class="mx-auto" />
      </p>
    </div>
    <div v-else class="pt-[140px]">
      <img src="~/assets/bili_2233_fail.png" />
      <h3 class="text-[24px] mt-10">Ứng dụng đã dừng lại</h3>
      <p class="text-[14px] mt-3 text-red-400 leading-normal">
        <span class="ml-2"
          >Phiên bản ứng dụng này đã bị sửa đổi hoặc không được ủy quyền vui
          lòng tải xuống ứng dụng tại

          <a href="https://app.animevsub.eu.org" target="_blank" class="text-blue-400"
            >https://app.animevsub.eu.org</a
          >
        </span>
      </p>
    </div>
  </div>
</template>

<script lang="ts" setup>
defineProps<{
  loading: boolean
}>()
</script>
