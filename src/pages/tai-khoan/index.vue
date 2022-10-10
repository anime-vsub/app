<template>
  <q-header class="bg-dark-page">
    <q-item
      clickable
      @click="authStore.user ? gotoEditProfile() : (showDialogLogin = true)"
    >
      <q-item-section avatar>
        <q-avatar size="55px">
          <img v-if="authStore.user?.avatar" :src="authStore.user.avatar" />
          <Icon
            v-else
            icon="fluent:person-circle-20-filled"
            width="55"
            height="55"
          />
        </q-avatar>
      </q-item-section>
      <q-item-section>
        <template v-if="authStore.user">
          <q-item-label class="text-subtitle1 text-weight-normal">{{
            authStore.user.name
          }}</q-item-label>
          <q-item-label caption class="text-grey">{{
            authStore.user.username
          }}</q-item-label>
        </template>
        <q-item-label v-else class="text-subtitle1 text-weight-normal"
          >Đăng nhập/Đăng ký</q-item-label
        >
      </q-item-section>
      <q-item-section side>
        <div class="flex items-center flex-nowrap">
          <q-btn dense round class="mr-1">
            <Icon icon="fluent:scan-dash-24-regular" width="25" height="25" />
          </q-btn>

          <Icon
            v-if="authStore.user"
            icon="fluent:chevron-right-24-regular"
            width="25"
            height="25"
          />
        </div>
      </q-item-section>
    </q-item>
  </q-header>

  <div class="mx-4 mt-4" v-if="histories.length > 0">
    <div class="text-subtitle1 text-weight-normal py-1">Lịch sử</div>
    <div class="overflow-x-auto whitespace-nowrap">
      <q-card
        v-for="item in histories"
        :key="item.id"
        class="bg-transparent inline-block w-[140px] mr-2"
        style="white-space: initial"
        @click="router.push(`/phim/${item.id}/${item.last.chap}`)"
      >
        <q-img :src="item.poster" :ratio="1920 / 1080" class="!rounded-[4px]">
          <div class="absolute bottom-0 w-full min-h-0 !py-0 !px-0">
            <q-linear-progress
              :value="item.last.cur / item.last.dur"
              rounded
              color="main"
              class="!h-[4px]"
            />
          </div>
        </q-img>

        <span class="line-clamp-2 min-h-10 mt-1">{{ item.name }}</span>
        <div class="text-grey">
          {{ item.seasonName }} tập {{ item.last.name }}
        </div>
      </q-card>
    </div>
  </div>

  <div class="mx-4 mt-4" v-if="favorites && favorites?.items.length > 0">
    <div class="text-subtitle1 text-weight-normal py-1">Theo dõi</div>
    <div class="overflow-x-auto whitespace-nowrap">
      <Card
        v-for="item in favorites?.items"
        :key="item.name"
        :data="item"
        class="inline-block card-wrap"
      />
      <!-- {{ favorites.items }} -->
    </div>
  </div>

  <q-list class="mt-4">
    <q-item clickable v-ripple to="/tai-khoan/setting">
      <q-item-section avatar>
        <Icon icon="fluent:settings-24-regular" width="25" height="25" />
      </q-item-section>
      <q-item-section>
        <q-item-label>Cài đặt</q-item-label>
      </q-item-section>
    </q-item>
    <q-item
      clickable
      v-ripple
      href="mailto:tachibshin@duck.com?subject=Phản hồi ứng dụng git.shin.animevsub"
    >
      <q-item-section avatar>
        <Icon icon="fluent:person-feedback-24-regular" width="22" height="22" />
      </q-item-section>
      <q-item-section>
        <q-item-label>Phản hồi</q-item-label>
      </q-item-section>
    </q-item>
    <q-item clickable v-ripple href="https://shin.is-a.dev">
      <q-item-section avatar>
        <Icon icon="fluent:info-24-regular" width="25" height="25" />
      </q-item-section>
      <q-item-section>
        <q-item-label>Giới thiệu</q-item-label>
      </q-item-section>
    </q-item>
  </q-list>

  <!-- dialogs login -->
  <q-dialog v-model="showDialogLogin" position="bottom" full-width>
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
              class="input"
              placeholder="E-mail"
            />
          </div>
          <div class="mt-4 relative flex items-center flex-nowrap input-wrap">
            <input
              v-model="password"
              required
              :type="showPassword ? 'text' : 'password'"
              name="password"
              class="input"
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
import type { Timestamp } from "@firebase/firestore"
import {
  collection,
  getDocs,
  getFirestore,
  limit,
  orderBy,
  query,
  where,
} from "@firebase/firestore"
import { Icon } from "@iconify/vue"
import { app } from "boot/firebase"
import Card from "components/Card.vue"
import { useQuasar } from "quasar"
import { TuPhim } from "src/apis/runs/tu-phim"
import { useAuthStore } from "stores/auth"
import { ref, shallowReactive, watch } from "vue"
import { useRequest } from "vue-request"
import { useRouter } from "vue-router"
// ========== favorite =========

const db = getFirestore(app)

const showDialogLogin = ref(false)

const showPassword = ref(false)

const email = ref("")
const password = ref("")

const $q = useQuasar()
const authStore = useAuthStore()

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

const router = useRouter()

function gotoEditProfile() {
  router.push("/tai-khoan/edit-profile")
}

// ============ fetch history =============
const histories = shallowReactive<
  {
    id: string
    first: string
    name: string
    poster: string
    seasonName: string
    update: Timestamp
    last: {
      chap: string
      name: string
      cur: number
      dur: number
    }
  }[]
>([])
watch(
  () => authStore.user_data,
  // eslint-disable-next-line camelcase
  async (user_data) => {
    histories.splice(0)

    // eslint-disable-next-line camelcase
    if (!user_data) return

    // eslint-disable-next-line camelcase
    const historyRef = collection(db, "users", user_data.email, "history")
    const q = query(
      historyRef,
      where("timestamp", "!=", null),
      orderBy("timestamp", "desc"),
      limit(30)
    )

    const { docs } = await getDocs(q)

    histories.push(
      ...docs.map((item) => {
        return {
          id: item.id,
          ...item.data(),
        } as typeof histories[0]
      })
    )
  },
  { immediate: true }
)

const { data: favorites } = useRequest(() => TuPhim(1))
</script>

<style lang="scss" scoped>
.input {
  border: none;
  outline: none;
  border-bottom: 1px solid;
  @apply bg-transparent w-full py-[14px] px-2 border-dark-50 block;

  &:focus,
  &:focus-visible {
    // all: initial !important;
    box-shadow: none;
  }
}

.input-wrap {
  border-bottom: 1px solid;
  @apply border-dark-50;

  .input {
    border: none;
  }
}

.card-wrap {
  $offset: 0.1;
  display: inline-block;
  white-space: initial;

  width: 280px;
  // class="col-4 col-lg-3 col-xl-2 px-[5px] py-2"
  max-width: calc((100% - 16px) / #{3 + $offset});
  margin-right: 8px;

  @media (min-width: $breakpoint-lg-min) {
    max-width: calc((100% - 48px) / #{4 + $offset});
    margin-right: 24px;
  }
  @media (min-width: $breakpoint-xl-min) {
    max-width: calc((100% - 80px) / #{6 + $offset});
    margin-right: 40px;
  }
}
</style>
