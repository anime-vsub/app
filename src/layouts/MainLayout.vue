<template>
  <q-layout view="hHh Lpr lFf">
    <q-header
      class="bg-dark-page py-1 px-2"
      :class="{
        '!bg-transparent': route.meta?.transparentHeader,
      }"
    >
      <q-toolbar>
        <q-btn
          dense
          flat
          round
          icon="menu"
          class="mr-5"
          @click="showDrawer = !showDrawer"
        />

        <router-link to="/" class="flex items-end">
          <img src="~assets/app_icon.svg" width="35" height="35" />
          <span style="font-family: Caveat" class="text-[25px]">nimeVsub</span>
        </router-link>

        <q-space />

        <form
          @submit.prevent="router.push(`/tim-kiem/${query}`)"
          class="relative min-w-[164px] w-full max-w-[598px]"
        >
          <q-input
            v-model="query"
            dense
            rounded
            outlined
            class="font-weight-normal input-search bg-[rgba(255,255,255,0)] w-full"
            input-style="background-color: transparent"
            placeholder="Tìm kiếm"
            @focus="focusing = true"
            @blur="focusing = false"
            @keydown.stop
          >
            <template v-slot:append>
              <q-separator vertical inset class="bg-[rgba(153,153,153,0.3)]" />
              <button
                type="submit"
                class="flex items-center"
                @click.stop.prevent="router.push(`/tim-kiem/${query}`)"
                @mousedown.stop.prevent
              >
                <q-icon name="search" class="pl-6 pr-4 cursor-pointer" />
              </button>
            </template>
          </q-input>

          <transition name="q-transition--fade">
            <ul
              class="absolute w-full bg-dark-page left-0 max-h-[80vh] overflow-y-auto scrollbar-custom pb-4 top-[calc(100%+8px)] shadow-8"
              v-show="focusing"
              @mousedown.stop.prevent
              @click.stop.prevent
            >
              <li
                v-if="query"
                class="px-4 mt-1 py-[0.5rem] flex items-center w-full justify-between"
              >
                <div>
                  <span class="text-gray-400 mr-1">Tìm kiếm: </span>
                  <span class="font-bold truncate">{{ query }}</span>
                </div>

                <button class="key-enter" type="submit">
                  <span>Enter</span>
                </button>
              </li>
              <li
              
                v-if="searchLoading"
                v-for="i in 12"
                :key="i"
                class="flex mt-5 mx-4"
              >
                <q-responsive :ratio="267 / 400" class="w-[90px] rounded">
                  <q-skeleton type="rect" class="absolute w-full h-full" />
                </q-responsive>

                <div class="ml-2 flex-1">
                  <q-skeleton type="text" width="60%" />
                  <q-skeleton type="text" width="100px" height="15px" />
                </div>
              </li>
              <li
                v-else-if="searchResult?.length"
                v-for="item in searchResult"
                :key="item.path"
                class="relative"
                v-ripple
              >
                <router-link :to="item.path" class="flex flex-nowrap mt-5 mx-4">
                  <div>
                    <q-img
                      :ratio="267 / 400"
                      :src="item.image"
                      width="90px"
                      class="rounded"
                    />
                  </div>

                  <div class="ml-2">
                    <div class="text-subtitle1 text-weight-medium">
                      {{ item.name }}
                    </div>
                    <div class="text-gray-500">{{ item.status }}</div>
                  </div>
                </router-link>
              </li>
              <li v-else class="px-4 py-5 text-center text-gray-400 w-full">
                {{ query ? "Không tìm thấy" : "Nhập để tìm kiếm" }}
              </li>
            </ul>
          </transition>
        </form>

        <q-space />

        <q-btn v-if="authStore.isLogged" round class="mr-2">
          <Icon
            :icon="
              showMenuFollow
                ? 'majesticons:bookmark-plus'
                : 'majesticons:bookmark-plus-line'
            "
            width="24"
            height="24"
          />

          <q-menu
            v-model="showMenuFollow"
            class="flex flex-nowrap column bg-dark-page"
          >
            <q-card
              class="transparent shadow-none w-[415px] scrollbar-custom overflow-y-auto"
            >
              <q-card-section>
                <SkeletonCardVertical
                  v-if="loadingFavorites"
                  v-for="i in 12"
                  :key="i"
                  class="mb-4"
                />
                <CardVertical
                  v-else-if="favorites"
                  v-for="item in favorites?.items"
                  :key="item.path"
                  :data="item"
                  class="mb-4"
                />
                <div v-else class="text-center">
                  <div class="text-subtitle1 font-weight-medium">
                    Lỗi không xác định
                  </div>
                  <q-btn outline color="main" @click="refreshFavorites"
                    >Thử lại</q-btn
                  >
                </div>
              </q-card-section>
            </q-card>

            <router-link to="/tai-khoan/follow" class="block py-2 text-center"
              >Xem tất cả</router-link
            >
          </q-menu>
        </q-btn>

        <q-btn v-if="authStore.isLogged" round class="mr-2">
          <Icon
            :icon="
              showMenuHistory
                ? 'fluent:clock-24-filled'
                : 'fluent:clock-24-regular'
            "
            width="24"
            height="24"
          />

          <q-menu
            v-model="showMenuHistory"
            class="flex flex-nowrap column bg-dark-page"
          >
            <q-card
              class="transparent shadow-none w-[415px] scrollbar-custom overflow-y-auto"
            >
              <q-card-section>
                <div
                  v-if="loadingHistories"
                  v-for="i in 12"
                  :key="i"
                  class="flex mt-1 mb-4 flex-nowrap"
                >
                  <q-responsive :ratio="1920 / 1080" class="w-[149px]">
                    <q-skeleton class="!rounded-[4px] absolute w-full h-full" />
                  </q-responsive>

                  <div class="pl-2 flex-1">
                    <q-skeleton type="text" class="mt-1" width="60%" />
                    <q-skeleton
                      type="text"
                      class="mt-1"
                      width="40px"
                      height="15px"
                    />

                    <div class="text-grey mt-1">
                      <q-skeleton
                        type="text"
                        class="mt-1"
                        width="60px"
                        height="15px"
                      />
                    </div>
                    <div class="text-grey mt-2">
                      <q-skeleton
                        type="text"
                        class="mt-1"
                        width="120px"
                        height="15px"
                      />
                    </div>
                  </div>
                </div>

                <template
                  v-else-if="histories"
                  v-for="(item, index) in histories"
                  :key="item.id"
                >
                  <div
                    v-if="
                      !histories![index - 1] ||
                      !histories![index - 1].timestamp.isSame(
                        item.timestamp,
                        'day'
                      )
                    "
                    class="text-subtitle2 text-weight-normal"
                  >
                    {{
                      item.timestamp.isToday()
                        ? "Hôm nay"
                        : item.timestamp.isYesterday()
                        ? "Hôm qua"
                        : item.timestamp.get("date") +
                          " thg " +
                          (item.timestamp.get("month") + 1)
                    }}
                  </div>
                  <router-link
                    class="bg-transparent flex mt-1 mb-4 flex-nowrap"
                    style="white-space: initial"
                    :to="`/phim/${item.id}/${item.last.chap}`"
                  >
                    <div class="w-[149px]">
                      <q-img
                        no-spinner
                        :src="item.poster"
                        :ratio="1920 / 1080"
                        class="!rounded-[4px]"
                      >
                        <BottomBlur class="px-0 h-[40%]">
                          <div
                            class="absolute bottom-0 left-0 z-10 w-full min-h-0 !py-0 !px-0"
                          >
                            <q-linear-progress
                              :value="item.last.cur / item.last.dur"
                              rounded
                              color="main"
                              class="!h-[3px]"
                            />
                          </div>
                        </BottomBlur>
                        <span
                          class="absolute text-white z-10 text-[12px] bottom-2 right-2"
                          >{{ parseTime(item.last.cur) }}</span
                        >
                      </q-img>
                    </div>

                    <div class="pl-2 flex-1">
                      <span class="line-clamp-3 mt-1">{{ item.name }}</span>
                      <div class="text-grey mt-1">
                        {{ item.seasonName }} tập {{ item.last.name }}
                      </div>
                      <div class="text-grey mt-2">
                        Xem lúc
                        {{
                          item.timestamp.format(
                            item.timestamp.isToday() ? "HH:mm" : "DD/MM/YYYY"
                          )
                        }}
                      </div>
                    </div>
                  </router-link>
                </template>

                <div v-else class="text-center">
                  <div class="text-subtitle1 font-weight-medium">
                    Lỗi không xác định
                  </div>
                  <q-btn outline color="main" @click="refreshHistories"
                    >Thử lại</q-btn
                  >
                </div>
              </q-card-section>
            </q-card>

            <router-link to="/tai-khoan/history" class="block py-2 text-center"
              >Xem tất cả</router-link
            >
          </q-menu>
        </q-btn>

        <q-btn v-if="authStore.isLogged" round class="mr-2">
          <Icon
            :icon="
              showMenuNotify
                ? 'clarity:notification-solid'
                : 'clarity:notification-line'
            "
            width="24"
            height="24"
          />

          <q-badge
            floating
            rounded
            transparent
            class="top-0"
            :label="notificationStore.max"
          />

          <q-menu v-model="showMenuNotify" class="scrollbar-custom">
            <q-card class="bg-dark-page max-w-[435px]">
              <q-card-section>
                <q-list v-if="notificationStore.loading" class="bg-transparent">
                  <q-item v-for="item in 12" :key="item">
                    <q-item-section>
                      <q-item-label class="text-subtitle1 text-weight-normal">
                        <q-skeleton type="text" width="40%" />
                        <q-skeleton type="text" width="60%" />
                      </q-item-label>
                      <q-item-label>
                        <q-skeleton type="text" width="100" height="15px" />
                      </q-item-label>
                    </q-item-section>
                    <q-item-section side>
                      <q-responsive
                        :ratio="120 / 81"
                        class="w-[120px] rounded-sm"
                      >
                        <q-skeleton
                          type="rect"
                          class="absolute w-full h-full"
                        />
                      </q-responsive>
                    </q-item-section>
                  </q-item>
                </q-list>

                <q-list v-else class="bg-transparent">
                  <transition-group name="notify">
                    <q-item
                      v-for="item in notificationStore.items"
                      :key="item.id"
                    >
                      <q-item-section>
                        <q-item-label class="text-subtitle1 text-weight-normal"
                          >{{ item.name }}
                          <span class="text-grey"> đã cập nhật </span>
                          {{ item.chap }}</q-item-label
                        >
                        <q-item-label class="text-grey">{{
                          item.time
                        }}</q-item-label>
                      </q-item-section>
                      <q-item-section side>
                        <div class="flex flex-nowrap">
                          <q-img
                            no-spinner
                            :src="item.image"
                            width="128px"
                            :ratio="120 / 81"
                            class="rounded-sm"
                          />
                          <div class="mr-[-32px]">
                            <q-btn
                              round
                              dense
                              icon="close"
                              @click="notificationStore.remove(item.id)"
                            />
                          </div>
                        </div>
                      </q-item-section>
                    </q-item>
                  </transition-group>
                </q-list>

                <div
                  v-if="notificationStore.items.length < notificationStore.max"
                  class="text-grey text-center mt-3 mx-2 mb-3"
                >
                  Do API server không đầy đủ bạn phải xóa những thông báo mới để
                  xem những thông báo cũ
                </div>
              </q-card-section>
            </q-card>
          </q-menu>
        </q-btn>

        <q-btn v-if="authStore.isLogged" flat round>
          <q-avatar size="35px">
            <img
              v-if="authStore.user_data?.avatar"
              :src="authStore.user_data.avatar"
            />
            <Icon
              v-else
              icon="fluent:person-circle-24-filled"
              width="30"
              height="30"
            />
          </q-avatar>

          <q-menu v-model="showMenuAccount" class="bg-dark-page">
            <q-card class="transparent w-[280px] px-2 pb-3">
              <q-list v-if="tabMenuAccountActive === 'normal'">
                <q-item>
                  <q-item-section avatar>
                    <q-avatar size="45px">
                      <img
                        v-if="authStore.user_data?.avatar"
                        :src="authStore.user_data.avatar"
                      />
                      <Icon
                        v-else
                        icon="fluent:person-circle-24-filled"
                        width="45"
                        height="45"
                      />
                    </q-avatar>
                  </q-item-section>
                  <q-item-section>
                    <q-item-label class="font-weight-medium text-subtitle1">{{
                      authStore.user_data!.name
                    }}</q-item-label>
                  </q-item-section>
                </q-item>

                <q-separator class="bg-[rgba(255,255,255,0.1)]" />

                <q-item
                  clickable
                  v-ripple
                  to="/tai-khoan/edit-profile"
                  active-class=""
                >
                  <q-item-section avatar class="min-w-0">
                    <Icon
                      icon="fluent:info-24-regular"
                      width="20"
                      height="20"
                    />
                  </q-item-section>
                  <q-item-section>
                    <q-item-label>Trung tâm cá nhân</q-item-label>
                  </q-item-section>
                </q-item>

                <q-item
                  clickable
                  v-ripple
                  @click="tabMenuAccountActive = 'locale'"
                >
                  <q-item-section avatar class="min-w-0">
                    <Icon icon="carbon:translate" width="20" height="20" />
                  </q-item-section>
                  <q-item-section>
                    <q-item-label>Ngôn ngữ</q-item-label>
                  </q-item-section>
                  <q-item-section side>
                    <Icon icon="fluent:chevron-right-24-regular" />
                  </q-item-section>
                </q-item>

                <q-item clickable v-ripple @click="logout">
                  <q-item-section avatar class="min-w-0">
                    <Icon icon="fa:sign-out" width="20" height="20" />
                  </q-item-section>
                  <q-item-section>
                    <q-item-label>Thoát</q-item-label>
                  </q-item-section>
                </q-item>
              </q-list>

              <q-list v-if="tabMenuAccountActive === 'locale'">
                <q-item
                  clickable
                  v-ripple
                  @click="tabMenuAccountActive = 'normal'"
                >
                  <q-item-section avatar class="min-w-0">
                    <Icon
                      icon="fluent:ios-arrow-ltr-24-regular"
                      width="20"
                      height="20"
                    />
                  </q-item-section>
                  <q-item-section> Chọn ngôn ngữ của bạn </q-item-section>
                </q-item>

                <q-separator class="bg-[rgba(255,255,255,0.1)]" />

                <q-item v-for="i in 3" :key="i" clickable v-ripple>
                  <q-item-section avatar class="min-w-0">
                    <Icon
                      icon="fluent:checkmark-24-regular"
                      width="20"
                      height="20"
                    />
                  </q-item-section>
                  <q-item-section>Tiếng Việt</q-item-section>
                </q-item>
              </q-list>
            </q-card>
          </q-menu>
        </q-btn>
        <q-btn
          v-else
          flat
          stack
          no-caps
          class="font-weight-normal"
          @click="showDialogLogin = true"
        >
          <Icon icon="fluent:person-24-regular" width="20" height="20" />
          Đăng nhập
        </q-btn>

        <q-btn
          flat
          no-caps
          class="font-weight-normal"
          href="https://anime-vsub.github.io"
          target="_blank"
        >
          <Icon icon="fluent:phone-24-regular" width="20" height="20" />
          App
        </q-btn>
      </q-toolbar>
    </q-header>

    <q-drawer
      :model-value="hideDrawer ? showDrawer : true"
      @update:model-value="hideDrawer ? (showDrawer = $event) : undefined"
      :mini="hideDrawer ? false : !showDrawer"
      :width="250"
      :breakpoint="500"
      :overlay="hideDrawer"
      :behavior="hideDrawer ? 'mobile' : undefined"
      class="bg-dark-page"
    >
      <q-toolbar v-if="hideDrawer">
        <q-btn
          dense
          flat
          round
          icon="menu"
          class="mr-5"
          @click="showDrawer = !showDrawer"
        />

        <router-link to="/" class="flex items-end">
          <img src="~assets/app_icon.svg" width="35" height="35" />
          <span style="font-family: Caveat" class="text-[25px]">nimeVsub</span>
        </router-link>
      </q-toolbar>

      <q-list>
        <template
          v-for="{ icon, active, name, path, divider } in drawers"
          :key="name"
        >
          <q-separator
            v-if="divider"
            class="bg-[rgba(255,255,255,0.1)] my-6 mr-2"
          />
          <q-item
            v-else
            clickable
            v-ripple
            class="min-h-0 px-4 my-2"
            :to="path"
            active-class=""
            exact-active-class="bg-[rgba(255,255,255,0.1)] text-main"
          >
            <q-item-section avatar class="pr-0 min-w-0">
              <Icon
                v-if="router.resolve(path!).fullPath !== route.fullPath"
                :icon="icon"
                width="23"
                height="23"
              />
              <Icon v-else :icon="active" width="23" height="23" />
            </q-item-section>
            <q-item-section class="ml-5">
              <q-item-label class="text-[16px]">{{ name }}</q-item-label>
            </q-item-section>
          </q-item>
        </template>
      </q-list>

      <div
        v-if="hideDrawer ? true : showDrawer"
        class="absolute bottom-0 left-0 w-full text-gray-500"
      >
        <a
          v-for="item in drawersBottom"
          :key="item.name"
          class="py-2 px-4 block"
          :href="item.href"
          target="_blank"
          >{{ item.name }}</a
        >
      </div>
    </q-drawer>

    <q-page-container>
      <q-page>
        <router-view v-slot="{ Component }">
          <keep-alive exclude="watch-anime">
            <component :is="Component" />
          </keep-alive>
        </router-view>
      </q-page>
    </q-page-container>
  </q-layout>

  <q-dialog v-model="showDialogLogin">
    <q-card class="h-[60vh] bg-dark-500">
      <q-card-section>
        <div class="flex justify-between">
          <q-btn dense round />

          <div class="flex-1 text-center text-subtitle1">
            Đăng nhập để đồng bộ dữ liệu
          </div>

          <q-btn dense round icon="close" v-close-popup />
        </div>
      </q-card-section>

      <q-card-section>
        <form @submit.prevent="login">
          <div>
            <input
              v-model="email"
              required
              type="email"
              name="email"
              class="input w-full"
              placeholder="E-mail"
              @keydown.stop
            />
          </div>
          <div class="mt-4 relative flex items-center flex-nowrap input-wrap">
            <input
              v-model="password"
              required
              :type="showPassword ? 'text' : 'password'"
              name="password"
              class="input w-full"
              placeholder="Mật khẩu mới"
              @keydown.stop
            />
            <q-btn
              dense
              round
              class="mr-1"
              @click="showPassword = !showPassword"
            >
              <Icon
                v-if="showPassword"
                icon="fluent:eye-24-regular"
                width="22"
                height="22"
              />
              <Icon v-else icon="fluent:eye-off-24-regular" />
            </q-btn>
          </div>

          <div class="text-grey text-center mt-5 mb-4">Tìm lại mật khẩu</div>

          <q-btn
            type="submit"
            no-caps
            class="bg-main w-full"
            :disable="!email || !password"
            >Đăng nhập</q-btn
          >
        </form>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script lang="ts" setup>
// eslint-disable-next-line import/order
import { Icon } from "@iconify/vue"

import "@fontsource/caveat"

// =========== suth

import BottomBlur from "components/BottomBlur.vue"
import CardVertical from "components/CardVertical.vue"
import SkeletonCardVertical from "components/SkeletonCardVertical.vue"
import { debounce, useQuasar } from "quasar"
import { History } from "src/apis/runs/history"
import { PreSearch } from "src/apis/runs/pre-search"
import { TuPhim } from "src/apis/runs/tu-phim"
import { parseTime } from "src/logic/parseTime"
import { useAuthStore } from "stores/auth"
import { useNotificationStore } from "stores/notification"
import { computed, ref, watch } from "vue"
import { useRequest } from "vue-request"
import { useRoute, useRouter } from "vue-router"

const drawers = [
  {
    icon: "fluent:home-24-regular",
    active: "fluent:home-24-filled",
    name: "Trang chủ",
    path: "/",
  },
  {
    icon: "ant-design:fire-outlined",
    active: "ant-design:fire-filled",
    name: "Thịnh hành",
    path: "/bang-xep-hang",
  },
  {
    icon: "ic:outline-filter-alt",
    active: "ic:round-filter-alt",
    name: "Mục lục",
    path: "/danh-sach/all",
  },
  {
    icon: "fluent:calendar-clock-24-regular",
    active: "fluent:calendar-clock-24-filled",
    name: "Lịch chiếu",
    path: "/lich-chieu-phim",
  },

  { divider: true },

  {
    icon: "material-symbols:favorite-outline-rounded",
    active: "material-symbols:favorite-rounded",
    name: "Theo dõi",
    path: "/tai-khoan/follow",
  },
  {
    icon: "fluent:history-24-regular",
    active: "fluent:history-24-filled",
    name: "Lịch sử",
    path: "/tai-khoan/history",
  },
]
const drawersBottom = [
  {
    name: "Về chúng tôi",
    href: "https://anime-vsub.github.io/about",
  },
  {
    name: "Liên hệ chúng tôi",
    href: "mailto:tachibshin@duck.com?subject=Phản hồi ứng dụng git.shin.animevsub",
  },
  {
    name: "Tải ứng dụng",
    href: "https://anime-vsub.github.io",
  },
  {
    name: "Điều khoản sử dụng",
    href: "https://anime-vsub.github.io/tems-of-use",
  },
  {
    name: "Chính sách riêng tư",
    href: "https://anime-vsub.github.io/privacy-police",
  },
  {
    name: "Khiếu nại vi phạm",
    href: "https://anime-vsub.github.io/disclaimer",
  },
]

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const notificationStore = useNotificationStore()

const query = ref("")
const {
  data: searchResult,
  loading: searchLoading,
  run,
} = useRequest(() => PreSearch(query.value), {
  manual: true,
})
watch(query, debounce(run, 300))

const focusing = ref(false)

const showDrawer = ref(false)

const hideDrawer = computed(() => route.meta?.hideDrawer === true)
watch(
  hideDrawer,
  (hideDrawer) => {
    if (hideDrawer) showDrawer.value = false
    else showDrawer.value = true
  },
  { immediate: true }
)
// import QrScanner from "qr-scanner"

const showDialogLogin = ref(false)

const showPassword = ref(false)

const email = ref("")
const password = ref("")

const $q = useQuasar()

async function login() {
  const loader = $q.loading.show({
    message: "Đang xác thực. Vui lòng đợi...",
    boxClass: "bg-dark text-light-9",
    spinnerColor: "main",
    delay: Infinity,
  })

  try {
    const data = await authStore.login(email.value, password.value)

    showDialogLogin.value = false
    email.value = ""
    password.value = ""
    $q.notify({
      position: "bottom-right",
      message: `Đã đăng nhập với tư cách ${data.name}`,
    })
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
  } catch (err: any) {
    console.error(err)
    $q.notify({
      position: "bottom-right",
      message: "Đăng nhập thất bại",
      caption: err.message,
    })
  } finally {
    loader()
  }
}
async function logout() {
  authStore.logout()
  $q.notify({
    position: "bottom-right",
    message: "Đã đăng xuất",
  })
}

// ============= states ===============
const showMenuHistory = ref(false)
const showMenuFollow = ref(false)
const showMenuNotify = ref(false)
const showMenuAccount = ref(false)
// history
const {
  data: histories,
  loading: loadingHistories,
  refreshAsync: refreshHistories,
} = useRequest(() => History(), {
  manual: true,
  cacheKey: "histories",
  cacheTime: 60 * 60 * 1000,
})
watch(
  showMenuHistory,
  (state) => {
    if (state) refreshHistories()
  },
  { immediate: true }
)
// follow
const {
  data: favorites,
  loading: loadingFavorites,
  refreshAsync: refreshFavorites,
} = useRequest(() => TuPhim(1), {
  manual: true,
  cacheKey: "favorites",
  staleTime: 60 * 60 * 1000,
})
watch(
  showMenuFollow,
  (state) => {
    if (state) refreshFavorites()
  },
  { immediate: true }
)

// account
// showMenuAccount
const tabMenuAccountActive = ref<"normal" | "locale">("normal")
watch(showMenuAccount, (val) => {
  if (val) tabMenuAccountActive.value = "normal"
})
</script>

<style lang="scss">
.input-search {
  .q-field__control {
    height: 40px !important;
    input,
    input:focus {
      border: none;
      outline: none;
      box-shadow: none;
    }
  }

  .q-field__control:before {
    border-color: rgba(153, 153, 153, 0.3) !important;
  }
  .q-field__control:after {
    border-width: 1px !important;
  }
}

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

.only-router-active {
  display: none;
}
</style>

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

<style lang="scss" scoped>
.key-enter {
  color: #f6f6f7;
  forced-color-adjust: none;
  height: 23px;
  width: auto;
  overflow: hidden;
  font-size: 12px;
  line-height: 1;
  text-transform: uppercase;

  &:hover {
    @apply pt-1;
  }

  span {
    background-color: #727d74;
    box-shadow: inset 0 -4px #202225;
    border: 1px solid hsl(220deg, 7.7%, 22.9%);
    padding: 3px 6px 4px;
    border-radius: 4px;
    min-width: 14px;
    min-height: 14px;
    height: 23px;
    color: #b9bbbe;
  }
}
</style>
