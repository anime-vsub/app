<template>
  <q-header class="bg-dark-page">
    <q-toolbar class="relative">
      <q-btn flat dense round class="mr-2" @click.stop="router.back()">
        <Icon icon="fluent:chevron-left-24-regular" width="25" height="25" />
      </q-btn>
      <q-toolbar-title
        class="absolute left-[50%] top-[50%] transform translate-x-[-50%] translate-y-[-50%] text-[16px] max-w-[calc(100%-34px*2)] line-clamp-1">
        Cài đặt chung
      </q-toolbar-title>
    </q-toolbar>
  </q-header>

</template>

<script lang="ts" setup>
  import { Icon } from "@iconify/vue"
  import { useRouter} from "vue-router"
  
  const router = useRouter()
  </script>
