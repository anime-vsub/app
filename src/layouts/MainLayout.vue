<template>
  <q-layout view="lHh Lpr lFf">
    <q-page-container>
      <q-page>
        <router-view v-slot="{ Component }">
          <component :is="Component" />
        </router-view>
      </q-page>
    </q-page-container>

    <q-footer v-if="route.meta?.footer" class="bg-dark-page">
      <q-tabs
        indicator-color="transparent"
        exact-active-color="white"
        class="bg-transparent text-grey-5 shadow-2 text-[12px]"
        no-caps
      >
        <q-route-tab exact-active-class="tab-active" class="pt-1" to="/">
          <Icon
            icon="fluent:home-24-regular"
            width="24"
            height="24"
            class="mb-1 regular"
          />
          <Icon
            icon="fluent:home-24-filled"
            width="24"
            height="24"
            class="mb-1 filled"
          />
          Trang chủ
        </q-route-tab>
        <q-route-tab active-class="tab-active" class="pt-1" to="/tim-kiem">
          <Icon
            icon="fluent:search-24-regular"
            width="24"
            height="24"
            class="mb-1 regular"
          />
          <Icon
            icon="fluent:search-24-filled"
            width="24"
            height="24"
            class="mb-1 filled"
          />
          Tìm kiếm
        </q-route-tab>
        <q-route-tab exact-active-class="tab-active" class="pt-1" to="/news">
          <Icon
            icon="fluent:news-24-regular"
            width="24"
            height="24"
            class="mb-1 regular"
          />
          <Icon
            icon="fluent:news-24-filled"
            width="24"
            height="24"
            class="mb-1 filled"
          />
          Tin tức
        </q-route-tab>
        <q-route-tab
          exact-active-class="tab-active"
          class="pt-1"
          to="/thong-bao"
        >
          <Icon
            icon="carbon:notification"
            width="24"
            height="24"
            class="mb-1 regular"
          />
          <Icon
            icon="carbon:notification-filled"
            width="24"
            height="24"
            class="mb-1 filled"
          />
          Thông báo
        </q-route-tab>
        <q-route-tab
          exact-active-class="tab-active"
          class="pt-1"
          to="/tai-khoan"
        >
          <Icon
            icon="fluent:person-24-regular"
            width="24"
            height="24"
            class="mb-1 regular"
          />
          <Icon
            icon="fluent:person-24-filled"
            width="24"
            height="24"
            class="mb-1 filled"
          />
          Tôi
        </q-route-tab>
      </q-tabs>
    </q-footer>
  </q-layout>
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"
import { useRoute } from "vue-router"

const route = useRoute()
</script>

<style lang="scss">
.filled {
  display: none;
}
.tab-active {
  color: #fff;
  .regular {
    display: none;
  }
  .filled {
    display: inline-block;
  }
}
</style>
