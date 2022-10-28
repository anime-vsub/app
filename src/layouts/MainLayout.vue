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

        <div class="relative min-w-[164px] w-full max-w-[598px]">
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
            @keypress.enter="router.push(`/tim-kiem/${query}`)"
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
            <li v-if="searchLoading" v-for="i in 12" :key="i" class="flex mt-5 mx-4">
              <q-responsive
                      :ratio="267 / 400" class="w-[90px] rounded">
                      <q-skeleton type="rect" class="absolute w-full h-full" />
                      </q-responsive>

                      <div class="ml-2 flex-1">
                        <q-skeleton type="text" width="60%" />
                        <q-skeleton type="text" width="100px" height="15px" />
                      </div>
            </li>
              <li v-else-if="searchResult?.length"
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
              <li v-else class="px-4 py-5">
                Không tìm thấy
              </li>
            </ul>
          </transition>
        </div>

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
                  v-else
                  v-for="item in favorites?.items"
                  :key="item.path"
                  :data="item"
                  class="mb-4"
                />
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
                  v-else
                  v-for="(item, index) in histories"
                  :key="item.id"
                >
                  <div
                    v-if="
                      !histories[index - 1] ||
                      !histories[index - 1].timestamp.isSame(
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
                  <div
                    class="bg-transparent flex mt-1 mb-4 flex-nowrap"
                    style="white-space: initial"
                    @click="router.push(`/phim/${item.id}/${item.last.chap}`)"
                  >
                  <div class=" w-[149px]">

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
                  </div>
                </template>
              </q-card-section>
            </q-card>
          </q-menu>
        </q-btn>

        <q-btn round class="mr-2">
          <Icon
            :icon="
              showMenuNotify
                ? 'clarity:notification-solid'
                : 'clarity:notification-line'
            "
            width="24"
            height="24"
          />

          <q-menu v-model="showMenuNotify" class="scrollbar-custom">
            <q-card class="bg-dark-page max-w-[435px]">
              <q-card-section>
                <q-list class="bg-transparent">
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
                        <q-img
                          no-spinner
                          :src="item.image"
                          width="120px"
                          :ratio="120 / 81"
                          class="rounded-sm"
                        />
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
            <img :src="authStore.user_data!.avatar" />
          </q-avatar>
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

        <q-btn flat no-caps class="font-weight-normal">
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

import { debounce, useQuasar } from "quasar"
import { PreSearch } from "src/apis/runs/pre-search"
import { useAuthStore } from "stores/auth"
import { ref, watch, computed } from "vue"
import { useRequest } from "vue-request"
import { useRoute, useRouter } from "vue-router"
import BottomBlur from "components/BottomBlur.vue"
import { parseTime } from "src/logic/parseTime"
import { useNotificationStore } from "stores/notification"
import Card from "components/Card.vue"
import CardVertical from "components/CardVertical.vue"

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
  },
  {
    name: "Liên hệ chúng tôi",
  },
  {
    name: "Tải ứng dụng",
  },
  {
    name: "Điều khoản sử dụng",
  },
  {
    name: "Chính sách riêng tư",
  },
  {
    name: "Khiếu nại vi phạm",
  },
]

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const notificationStore = useNotificationStore()

const query = ref("")
const { data: searchResult, run } = useRequest(() => PreSearch(query.value), {
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

// ============= states ===============
const showMenuHistory = ref(true)
const showMenuFollow = ref(false)
const showMenuNotify = ref(false)

import { History } from "src/apis/runs/history"
import { TuPhim } from "src/apis/runs/tu-phim"
// history
const {
  data: histories,
  loading: loadingHistories,
  run: runHistories,
  error: errorHistories,
  refreshAsync: refreshHistories,
} = useRequest(() => History())
// follow
const {
  data: favorites,
  loading: loadingFavorites,
  run: runFavorites,
  error: errorFavorites,
  refreshAsync: refreshFavorites,
} = useRequest(() => TuPhim(1))
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

  .q-field__control:after,
  .q-field__control:before {
    // display: none !important;
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
