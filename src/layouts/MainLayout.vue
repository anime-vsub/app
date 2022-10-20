<template>
  <q-layout view="lHh Lpr lFf">
    <q-page-container>
      <q-page>
        <router-view v-slot="{ Component }">
          <keep-alive exclude="watch-anime">
            <component :is="Component" />
          </keep-alive>
        </router-view>
      </q-page>
    </q-page-container>

    <q-footer v-if="route.meta?.footer" class="bg-dark-page">
      <q-tabs
        indicator-color="transparent"
        exact-active-color="white"
        class="bg-transparent text-grey-5 shadow-2 text-[12px] tabs-main"
        no-caps
      >
        <q-route-tab
          replace
          exact-active-class="tab-active"
          class="pt-1"
          to="/"
        >
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
        <q-route-tab
          replace
          exact-active-class="tab-active"
          class="pt-1"
          to="/tim-kiem"
        >
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
        <q-route-tab
          replace
          exact-active-class="tab-active"
          class="pt-1"
          to="/news"
        >
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
          replace
          exact-active-class="tab-active"
          class="pt-1"
          to="/notification"
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
          <q-badge v-if="notificationStore.max" color="red" floating>{{
            notificationStore.max
          }}</q-badge>
        </q-route-tab>
        <q-route-tab
          replace
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

  <!-- dialog notify update app -->
  <q-dialog
    :model-value="newVersion"
    @update:model-value="newVersion = undefined"
    full-width
  >
    <q-card class="">
      <q-card-section>
        <div class="text-subtitle1 text-weight-medium">Có bản cập nhật mới</div>

        <div class="text-grey">
          {{ newVersion.tag_name }} (current: {{ appInfos.version }})
        </div>

        <div class="overflow-y-auto">
          <p class="whitespace-pre-wrap">{{ newVersion.body }}</p>
        </div>
      </q-card-section>

      <q-card-actions class="justify-end">
        <q-btn
          flat
          no-caps
          color="grey"
          label="Bỏ qua"
          @click="ignoreUpdateVersion(newVersion.tag_name)"
        />
        <q-btn
          flat
          no-caps
          color="main"
          label="Cập nhật"
          target="_blank"
          href="https://anime-vsub.github.io/changelog"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"
import { useNotificationStore } from "stores/notification"
import { useRoute } from "vue-router"
import { shallowRef } from "vue"
import { App } from "@capacitor/app"
import semverGt from "semver/functions/gt"
import semverEq from "semver/functions/eq"

const route = useRoute()
const notificationStore = useNotificationStore()

const newVersion = shallowRef()
const appInfos = shallowRef()

Promise.all([
  fetch("https://api.github.com/repos/anime-vsub/app/releases").then((res) =>
    res.json()
  ),

  App.getInfo(),
])
  .then((results) => {
    const ignoreUpdateVersion = localStorage.getItem("ignore-update-version")

    if (ignoreUpdateVersion) {
      if (semverEq(ignoreUpdateVersion, results[0][0].tag_name)) {
        return
      }
    }

    if (!semverGt(results[0][0].tag_name, results[1].version)) {
      return
    }

    ;[newVersion.value, appInfos.value] = [results[0][0], results[1]]
  })
  .catch((err) => {
    console.error(err)
  })

function ignoreUpdateVersion(version: string) {
  localStorage.setItem("ignore-update-version", version)
  newVersion.value = undefined
}
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

.tabs-main .q-tab__content {
  max-width: 0 !important;
  width: (100% / 5) !important;
}
</style>
