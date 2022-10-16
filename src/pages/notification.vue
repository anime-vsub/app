<template>
  <header class="fixed top-0 z-1000 w-full bg-dark-page">
    <q-toolbar class="relative">
      <q-toolbar-title class="text-center text-[16px] w-full line-clamp-1">
        Thông báo
      </q-toolbar-title>
    </q-toolbar>
  </header>

  <div v-if="authStore.isLogged">
    <ScreenLoading
      v-if="notificationStore.loading && notificationStore.items.length > 0"
      class="absolute top-0 h-full w-full pt-[50px]"
    />

    <q-pull-to-refresh v-else-if="notificationStore.max > 0" @refresh="notificationStore.refresh" >
      <q-list class="bg-transparent mt-[50px]">
        <transition-group name="notify">
          <q-slide-item
            v-for="item in notificationStore.items"
            :key="item.id"
            @left="onDelete(item)"
            @right="onDelete(item)"
            left-color="red"
            right-color="red"
            clickable
            v-ripple
            @click="router.push(item.path)"
            class="bg-transparent"
          >
            <template v-slot:left>
              <Icon icon="fluent:delete-24-regular" width="25" height="25" />
            </template>
            <template v-slot:right>
              <Icon icon="fluent:delete-24-regular" width="25" height="25" />
            </template>

            <q-item>
              <q-item-section>
                <q-item-label class="text-subtitle1 text-weight-normal"
                  >{{ item.name }} <span class="text-grey"> đã cập nhật </span>
                  {{ item.chap }}</q-item-label
                >
                <q-item-label class="text-grey">{{ item.time }}</q-item-label>
              </q-item-section>
              <q-item-section side>
                <q-img
                  no-spinner
                  :src="item.image"
                  width="120px"
                  :ratio="120 / 81"
                  class="rounded-sm"
                />
              </q-item-section>
            </q-item>
          </q-slide-item>
        </transition-group>
      </q-list>

      <div
        v-if="notificationStore.items.length < notificationStore.max"
        class="text-grey text-center mt-3 mx-2 mb-3"
      >
        Do API server không đầy đủ bạn phải xóa những thông báo mới để xem những
        thông báo cũ
      </div>
    </q-pull-to-refresh>

    <div
      v-else
      class="absolute top-0 h-full w-full pt-[50px] text-subtitle1 flex items-center justify-center"
    >
      <div class="text-center">
        <img
          src="~/assets/img_holder_empty_style1.png"
          width="250"
          class="mx-auto mb-1"
        />
        Không có thông báo mới nào
      </div>
    </div>
  </div>
  <div
    v-else
    class="absolute top-0 h-full w-full pt-[50px] text-subtitle1 flex items-center justify-center"
  >
    <div class="text-center">
      <img
        src="~/assets/img_holder_error_style2.png"
        width="250"
        class="mx-auto mb-1"
      />
      Đăng nhập để xem các thông báo anime mới.
    </div>
  </div>
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"
import ScreenLoading from "src/components/ScreenLoading.vue"
import { useAliveScrollBehavior } from "src/composibles/useAliveScrollBehavior"
import { useAuthStore } from "stores/auth"
import { useNotificationStore } from "stores/notification"
import { useRouter } from "vue-router"

// Import Swiper Vue.js components
useAliveScrollBehavior()

const router = useRouter()
const notificationStore = useNotificationStore()
const authStore = useAuthStore()

async function onDelete(item: typeof notificationStore.items[0]) {
  notificationStore.items.splice(notificationStore.items.indexOf(item) >>> 0, 1)
  await notificationStore.remove(item.id)
  // reset()
}
</script>

<style lang="scss" scoped>
.notify {
  &-move,
  &-enter-active,
  &-leave-active {
    transition: all 0.22s ease;
  }

  &-enter-from,
  &-leave-to {
    opacity: 0;
    transform: translateX(30px);
  }

  &-leave-active {
    position: absolute;
  }
}
</style>
