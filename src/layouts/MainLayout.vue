<template>
  <q-layout view="hHh Lpr lFf">
    <q-header
      class="bg-dark-page py-1 px-2"
      :class="{
        transparent: route.meta?.transparentHeader,
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

        <div class="relative">
          <q-input
            v-model="query"
            dense
            square
            filled
            class="font-weight-normal input-search bg-[rgba(255,255,255,0)] text-[rgba(255,255,255,0.6)] w-[340px]"
            input-style="background-color: transparent"
            placeholder="Tìm kiếm"
            @focus="focusing = true"
            @blur="focusing = false"
            @keypress.enter="router.push(`/tim-kiem/${query}`)"
          />

          <transition name="q-transition--fade">
            <ul
              class="absolute w-full bg-dark-page left-0 max-h-[80vh] overflow-y-auto border border-[rgba(255,255,255,0.1)] pb-4"
              v-show="focusing"
            >
              <li
                v-for="item in searchResult"
                :key="item.path"
                class="relative"
                v-ripple
              >
                <router-link :to="item.path" class="flex flex-nowrap mt-5 mx-4">
                  <img :src="item.image" width="90" />

                  <div class="ml-2">
                    <div class="text-subtitle1 text-weight-medium">
                      {{ item.name }}
                    </div>
                    <div class="text-gray-500">{{ item.status }}</div>
                  </div>
                </router-link>
              </li>
            </ul>
          </transition>
        </div>

        <q-btn flat stack no-caps class="font-weight-normal">
          <Icon icon="fluent:history-24-regular" width="20" height="20" />
          Lịch sử
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
      v-model="showDrawer"
      :width="250"
      :breakpoint="500"
      overlay
      behavior="mobile"
      class="bg-dark-page"
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
            <q-item-section avatar class="pr-0 min-w-0 mr-5">
              <Icon
                v-if="router.resolve(path!).fullPath !== route.fullPath"
                :icon="icon"
                width="23"
                height="23"
              />
              <Icon v-else :icon="active" width="23" height="23" />
            </q-item-section>
            <q-item-section>
              <q-item-label class="text-[16px]">{{ name }}</q-item-label>
            </q-item-section>
          </q-item>
        </template>
      </q-list>

      <div class="absolute bottom-0 left-0 w-full text-gray-500">
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
import { ref, watch } from "vue"
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

const query = ref("")
const { data: searchResult, run } = useRequest(() => PreSearch(query.value), {
  manual: true,
})
watch(query, debounce(run, 300))

const focusing = ref(false)

const showDrawer = ref(false)

watch(
  () => route.meta?.hideDrawer === true,
  (hideDrawer) => {
    if (hideDrawer) showDrawer.value = false
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
</script>

<style lang="scss">
.input-search {
  .q-field__control {
    height: 36px !important;
    input,
    input:focus {
      border: none;
      outline: none;
      box-shadow: none;
    }
  }

  .q-field__control:after,
  .q-field__control:before {
    display: none !important;
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
