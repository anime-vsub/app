<template>
  <q-layout view="lHh Lpr lFf">
    <q-page-container>
      <q-page v-if="!isNative || appAllowWork">
        <router-view v-if="isNative || Http.version" v-slot="{ Component }">
          <keep-alive exclude="_season">
            <component :is="Component" />
          </keep-alive>
        </router-view>
        <NotExistsExtension v-else />
      </q-page>
      <AllowWork v-else :loading="appAllowWork === undefined" />
    </q-page-container>

    <q-footer v-if="route.meta?.footer" class="bg-dark-page">
      <q-tabs
        indicator-color="transparent"
        exact-active-color="white"
        class="bg-transparent text-grey-5 !shadow-2 text-[12px] tabs-main"
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
          {{ t("trang-chu") }}
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
          {{ t("tim-kiem") }}
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
          {{ t("tin-tuc") }}
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
          {{ t("thong-bao") }}
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
          <q-circular-progress
            v-if="updatingCache && installedSW"
            indeterminate
            rounded
            show-value
            size="35px"
            color="main"
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
          </q-circular-progress>
          <template v-else>
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
          </template>

          {{ t("toi") }}
        </q-route-tab>
      </q-tabs>
    </q-footer>
  </q-layout>

  <!-- dialog notify update app -->
  <q-dialog
    :model-value="!!newVersion"
    @update:model-value="newVersion = undefined"
    full-width
  >
    <q-card class="">
      <q-card-section>
        <div class="text-subtitle1 text-weight-medium">
          {{ t("da-co-ban-cap-nhat-moi") }}
        </div>

        <div class="text-grey">
          {{ newVersion.tag_name }} (current: {{ appInfos.version }})
        </div>

        <div class="overflow-y-auto">
          <p
            class="whitespace-pre-wrap"
            v-html="parseMdBasic(newVersion.body)"
          />
        </div>
      </q-card-section>

      <q-card-actions class="justify-end">
        <q-btn
          flat
          no-caps
          color="grey"
          :label="t('bo-qua')"
          @click="ignoreUpdateVersion(newVersion.tag_name)"
        />
        <q-btn
          flat
          no-caps
          color="main"
          :label="t('cap-nhat')"
          target="_blank"
          href="https://anime-vsub.github.io/changelog"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script lang="ts" setup>
import { App } from "@capacitor/app"
import { Icon } from "@iconify/vue"
import { computedAsync } from "@vueuse/core"
import { name, version } from "app/package.json"
import { Http } from "client-ext-animevsub-helper"
import semverGt from "semver/functions/gt"
import { isNative } from "src/constants"
import { parseMdBasic } from "src/logic/parseMdBasic"
import { installedSW, updatingCache } from "src/logic/state-sw"
import { useNotificationStore } from "stores/notification"
import { computed, shallowRef } from "vue"
import { useI18n } from "vue-i18n"
import { useRoute } from "vue-router"

import AllowWork from "./AllowWork.vue"
import NotExistsExtension from "./NotExistsExtension.vue"

const route = useRoute()
const { t } = useI18n()
const notificationStore = useNotificationStore()

const newVersion = shallowRef()
const appInfos = shallowRef()

if (isNative)
  Promise.all([
    fetch("https://api.github.com/repos/anime-vsub/app/releases").then(
      (res) =>
        res.json() as Promise<
          {
            name: string
            tag_name: string
            body: string
          }[]
        >
    ),
    !isNative ? { version } : App.getInfo(),
  ])
    .then((results) => {
      const ignoreUpdateVersion = localStorage.getItem("ignore-update-version")
      if (isNative) {
        results[0] = results[0].filter(
          (item) => !item.tag_name.startsWith("pwa-")
        )
      } else {
        results[0] = results[0].filter((item) =>
          item.tag_name.startsWith("pwa-")
        )
      }

      const lastVersion = results[0][0]

      if (ignoreUpdateVersion) {
        if (ignoreUpdateVersion === lastVersion.tag_name) {
          return
        }
      }

      // eslint-disable-next-line promise/always-return
      if (
        !semverGt(lastVersion.tag_name.replace(/^.+-v/, ""), results[1].version)
      )
        return
      ;[newVersion.value, appInfos.value] = [results[0][0], results[1]]
    })
    .catch((err) => {
      console.error(err)
    })

function ignoreUpdateVersion(version: string) {
  localStorage.setItem("ignore-update-version", version)
  newVersion.value = undefined
}

const parse = (str: string) => {
  try {
    return JSON.parse(str)
  } catch {
    return null
  }
}
const appAllowWork = isNative
  ? computedAsync<boolean>(async () => {
      const activeCache = parse(localStorage.getItem("active") ?? "")

      if (activeCache && Date.now() - activeCache.now <= 1000 * 3600 * 24) {
        return true
      }
      const [res, { id }] = await Promise.all([
        fetch(
          "https://raw.githubusercontent.com/anime-vsub/app/main/native-active"
        ),
        App.getInfo(),
      ])

      const active =
        res.status !== 404 &&
        (res.status === 200 || res.status === 201) &&
        id === name
      if (!active) return false

      localStorage.setItem(
        "active",
        JSON.stringify({
          now: Date.now(),
        })
      )
      return true
    })
  : computed(() => true)
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
  min-width: 0 !important;
}
.tabs-main .q-tabs__content {
  width: 100% !important;
  > .q-tab {
    width: (100% / 5);
  }
}
</style>
