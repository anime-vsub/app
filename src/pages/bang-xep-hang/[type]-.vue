<template>
  <q-page-sticky position="top" class="children:w-full bg-dark-page z-10">
    <div class="w-full">
      <div class="flex mx-2">
        <q-avatar size="60px">
          <img src="~/assets/trending_avatar.png" />
        </q-avatar>
        <div class="flex items-center">
          <div class="text-subtitle1 text-weight-medium text-[20px] ml-3">
            {{ t("thinh-hanh") }}
          </div>
        </div>
      </div>
      <div class="text-[#9a9a9a] mt-3 mb-2 text-[16px]">
        <router-link
          v-for="[name, value] in types"
          :key="value"
          class="relative inline-block px-4 py-1"
          v-ripple
          active-class="c--main text-weight-medium children:before:block"
          :to="`/bang-xep-hang/${value}`"
        >
          <span
            class="relative inline-block before:content-DEFAULT before:hidden before:absolute before:h-[2px] before:w-full before:bg-[currentColor] before:bottom-[-4px] pb-[2px] before:rounded"
            >{{ name }}</span
          >
        </router-link>
      </div>
    </div>
  </q-page-sticky>

  <div class="pt-[100px]">
    <ScreenLoading v-if="loading" class="absolute" />
    <CardVertical
      v-else-if="data"
      v-for="(item, index) in data"
      :key="item.name"
      :data="{
        ...item,
        description: item.othername,
        process: item.process.replace('Tập ', '')
      }"
      class="mt-4 mx-3"
    >
      <template v-slot:img-content>
        <BottomBlur />
        <img v-if="index < 10" :src="ranks[index]" class="h-[1.5rem]" />
      </template>
    </CardVertical>
    <ScreenError v-else @click:retry="run" class="absolute" :error="error" />
  </div>
</template>

<script lang="ts" setup>
import { useHead } from "@vueuse/head"
import BottomBlur from "components/BottomBlur.vue"
import CardVertical from "components/CardVertical.vue"
import ScreenError from "components/ScreenError.vue"
import ScreenLoading from "components/ScreenLoading.vue"
import { BangXepHangType } from "src/apis/runs/bang-xep-hang/[type]"
import ranks from "src/logic/ranks"
import { computed } from "vue"
import { useI18n } from "vue-i18n"
import { useRequest } from "vue-request"
import { useRoute } from "vue-router"

const { t } = useI18n()
const route = useRoute()

const types = [
  [t("mac-dinh"), ""],
  [t("ngay"), "day"],
  [t("thang"), "month"],
  [t("nam"), "year"],
  [t("danh-gia"), "voted"],
  [t("mua"), "season"]
]
useHead(
  computed(() => {
    const title = t("bang-xep-hang-anime-theo-_type", [
      (types.find((item) => item[1] === route.params.type) ?? types[0])[0]
    ])

    const description = title

    return {
      title,
      description,
      meta: [
        { property: "og:title", content: title },
        { property: "og:description", content: description },
        {
          property: "og:url",
          content: process.env.APP_URL + `bang-xep-hang/${route.params.type}`
        }
      ],
      link: [
        {
          rel: "canonical",
          href: process.env.APP_URL + `bang-xep-hang/${route.params.type}`
        }
      ]
    }
  })
)

const { data, loading, run, error } = useRequest(
  () => BangXepHangType(route.params.type as string),
  {
    refreshDeps: [() => route.params.type],
    refreshDepsAction() {
      run()
    }
  }
)
</script>
