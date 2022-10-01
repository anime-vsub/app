<template>
  <q-header class="bg-dark-page">
    <q-toolbar v-if="!data" class="relative">
      <q-skeleton type="QBtn" flat dense round />

      <q-space />
      <q-skeleton
        type="text"
        class="absolute left-[50%] top-[50%] transform translate-x-[-50%] translate-y-[-50%] text-[16px]"
        width="120px"
      />

      <q-skeleton type="QBtn" flat dense round />
    </q-toolbar>
  </q-header>

  <q-page>
    {{ data }}
  </q-page>
</template>

<script lang="ts" setup>
import { LichChieuPhim } from "src/apis/lich-chieu-phim"
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
