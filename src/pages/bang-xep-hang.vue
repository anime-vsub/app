<template>
  <q-header class="bg-dark-page">
    <q-toolbar class="relative">
      <q-btn flat dense round class="mr-2" @click.stop="router.back()">
        <Icon icon="fluent:chevron-left-24-regular" width="25" height="25" />
      </q-btn>
      <q-toolbar-title
        class="absolute left-[50%] top-[50%] transform translate-x-[-50%] translate-y-[-50%] text-[16px] max-w-[calc(100%-34px*2)] line-clamp-1"
        >Bảng xếp hạng</q-toolbar-title
      >
    </q-toolbar>
  </q-header>
  <q-page v-if="loading" class="flex items-center">
    <LaodingAnim />
  </q-page>
  <q-page
  v-else
  >

  </q-page>
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"
import Card from "components/Card.vue"
import CardVertical from "components/CardVertical.vue"
import GridCard from "components/GridCard.vue"
import LaodingAnim from "components/LaodingAnim.vue"
import { LichChieuPhim } from "src/apis/lich-chieu-phim"
import { dayTextToNum } from "src/logic/dayTextToNum"
import { watch } from "vue"
import { useRequest } from "vue-request"
import { useRoute, useRouter } from "vue-router"

const route = useRoute()
const router = useRouter()

const { data, loading, error } = useRequest(() => LichChieuPhim())
watch(error, (error) => {
  if (error)
    router.push({
      name: "not_found",
      params: { pathMatch: route.path },
      query: {
        message: error.message,
        cause: error.cause + "",
      },
    })
})

</script>
