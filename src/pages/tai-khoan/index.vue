<template>
  <header class="fixed top-0 bg-dark-page bg-dark-page z-1000 w-full">
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
          <q-btn
            v-if="authStore.isLogged"
            dense
            round
            class="mr-1"
            @click.stop="showDialogQR = true"
          >
            <Icon icon="fluent:qr-code-24-regular" width="25" height="25" />
          </q-btn>
          <q-btn dense round class="mr-1" @click.stop="startScanQR">
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
  </header>

  <q-pull-to-refresh @refresh="refresh" class="pt-[72px]">
    <div class="mt-4">
      <router-link
        class="text-subtitle1 text-weight-normal px-4 py-1 relative flex items-center justify-between"
        to="/tai-khoan/history"
      >
        Lịch sử

        <Icon
          icon="fluent:chevron-right-24-regular"
          class="text-grey"
          width="18"
          height="18"
        />
      </router-link>
      <div
        v-if="loadingHistories"
        class="h-[146px] mx-4 overflow-x-hidden whitespace-nowrap"
      >
        <q-card
          v-for="item in 12"
          :key="item"
          class="bg-transparent inline-block history-item mr-2"
          style="white-space: initial"
        >
          <q-responsive :ratio="1920 / 1080" class="!rounded-[4px]">
            <q-skeleton type="rect" class="absolute w-full h-full" />
          </q-responsive>

          <q-skeleton type="text" width="100%" class="line-clamp-2 mt-1" />
          <q-skeleton type="text" width="40%" />
        </q-card>
      </div>
      <div
        v-else-if="!errorHistories"
        class="mx-4 overflow-x-auto whitespace-nowrap"
      >
        <q-card
          v-for="item in histories"
          :key="item.id"
          class="bg-transparent inline-block history-item mr-2"
          style="white-space: initial"
          @click="router.push(`/phim/${item.id}/${item.last.chap}`)"
        >
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

          <span class="line-clamp-2 min-h-10 mt-1">{{ item.name }}</span>
          <div class="text-grey">
            {{ item.seasonName }} tập {{ item.last.name }}
          </div>
        </q-card>
      </div>
      <ScreenError
        v-else
        no-image
        class="h-[146px] px-4"
        @click:retry="runHistories"
      />
    </div>

    <div class="mt-4">
      <router-link
        class="text-subtitle1 text-weight-normal px-4 py-1 relative flex items-center justify-between"
        to="/tai-khoan/follow"
      >
        Theo dõi

        <Icon
          icon="fluent:chevron-right-24-regular"
          class="text-grey"
          width="18"
          height="18"
        />
      </router-link>
      <div
        v-if="loadingFavorites"
        class="mx-4 overflow-x-hidden whitespace-nowrap"
      >
        <SkeletonCard
          v-for="item in 20"
          :key="item"
          class="inline-block card-wrap"
        />
      </div>
      <div
        v-else-if="!errorFavorites"
        class="mx-4 overflow-x-auto whitespace-nowrap"
      >
        <Card
          v-for="item in favorites?.items"
          :key="item.name"
          :data="item"
          class="inline-block card-wrap"
        />
        <!-- {{ favorites.items }} -->
      </div>
      <ScreenError
        v-else
        no-image
        class="h-[203px] px-4"
        @click:retry="runFavorites"
      />
    </div>

    <q-list class="mt-4">
      <q-item clickable v-ripple to="/tai-khoan/settings">
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
          <Icon
            icon="fluent:person-feedback-24-regular"
            width="22"
            height="22"
          />
        </q-item-section>
        <q-item-section>
          <q-item-label>Phản hồi</q-item-label>
        </q-item-section>
      </q-item>
      <q-item clickable v-ripple to="/tai-khoan/about">
        <q-item-section avatar>
          <Icon icon="fluent:info-24-regular" width="25" height="25" />
        </q-item-section>
        <q-item-section>
          <q-item-label>Giới thiệu</q-item-label>
        </q-item-section>
      </q-item>
    </q-list>
  </q-pull-to-refresh>
  <!-- dialogs login -->
  <q-dialog
    v-model="showDialogLogin"
    position="bottom"
    class="children:!px-0"
    full-width
  >
    <q-card class="h-[60vh] bg-dark-500">
      <q-card-section>
        <div class="flex justify-between">
          <q-btn dense round unelevated/>

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


          <div class="text-center text-gray-300 my-3">
            Đăng nhập bằng tài khoản của bạn trên
            <a href="https://animevietsub.cc" target="_blank">AnimeVietsub</a>.
            Dữ liệu của bạn sẽ được đồng bộ cả ở đó và ở đây.
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

  <!-- dialogs my QR -->
  <q-dialog
    v-model="showDialogQR"
    position="bottom"
    class="children:!px-0"
    full-width
  >
    <q-card class="bg-dark-500 h-min-[500px] px-4 pb-2 pt-4">
      <div class="text-subtitle1 text-weight-normal">QR của bạn</div>
      <div class="text-grey">
        Quét QR Code này trên thiết bị khác để đăng nhập
      </div>

      <div class="mt-5">
        <canvas ref="qrRef" width="300" height="300" class="mx-auto" />
      </div>
    </q-card>
  </q-dialog>
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"
import BottomBlur from "components/BottomBlur.vue"
import Card from "components/Card.vue"
import ScreenError from "components/ScreenError.vue"
import SkeletonCard from "components/SkeletonCard.vue"
import { compressToBase64 } from "lz-string"
import QRCode from "qrcode"
import { useQuasar } from "quasar"
import { History } from "src/apis/runs/history"
import { TuPhim } from "src/apis/runs/tu-phim"
import { parseTime } from "src/logic/parseTime"
import { useAuthStore } from "stores/auth"
import { ref, watch, watchEffect } from "vue"
import { useRequest } from "vue-request"
import { useRouter } from "vue-router"
// import QrScanner from "qr-scanner"

const showDialogLogin = ref(false)
const showDialogQR = ref(false)

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

const router = useRouter()

function gotoEditProfile() {
  router.push("/tai-khoan/edit-profile")
}

const qrcodeUrl = ref<string>()
watchEffect(() => {
  QRCode.toDataURL(
    JSON.stringify({
      token_name: authStore.token_name,
      token_value: authStore.token_value,
      user_data: authStore.user_data,
    })
  )
    .then((url) => (qrcodeUrl.value = url))
    .catch((err) => {
      $q.notify({
        position: "bottom-right",
        message: "Lỗi khi tạo QR Code",
        caption: err + "",
      })
    })
})

// ============ fetch history =============
const {
  data: histories,
  loading: loadingHistories,
  run: runHistories,
  error: errorHistories,
  refreshAsync: refreshHistories,
} = useRequest(() => History(), { manual: true })

// ========== favorite =========
const {
  data: favorites,
  loading: loadingFavorites,
  run: runFavorites,
  error: errorFavorites,
  refreshAsync: refreshFavorites,
} = useRequest(() => TuPhim(1), { manual: true })

watch(
  () => authStore.isLogged,
  (isLogged) => {
    if (isLogged) {
      runHistories()
      runFavorites()
    } else {
      histories.value = undefined
      loadingHistories.value = false
      errorHistories.value = undefined

      favorites.value = undefined
      loadingFavorites.value = false
      errorFavorites.value = undefined
    }
  }
)
if (authStore.isLogged) {
  runHistories()
  runFavorites()
}

async function refresh(done: () => void) {
  if (authStore.isLogged)
    await Promise.all([refreshHistories(), refreshFavorites()])
  done()
}

// ========== gen qr code =========
const qrRef = ref<HTMLCanvasElement>()
watch(qrRef, (qrRef) => {
  if (qrRef) {
    if (!authStore.isLogged) {
      console.warn("you need login to make qrcode")
      return
    }

    QRCode.toCanvas(
      qrRef,
      "anvs://login?token=" +
        compressToBase64(
          JSON.stringify({
            token_name: authStore.token_name,
            token_value: authStore.token_value,
            user_data: authStore.user_data,
          })
        )
    )
  }
})

// ========== scan qr code ==========
function startScanQR() {
  $q.notify({
    message: "Quét QR hiện đang bảo trì",
  })
}
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

.history-item {
  width: 30%;
  max-width: 230px;
  min-width: 140px;

  @media (min-width: $breakpoint-md-min) {
    width: 23%;
  }

  @media (min-width: $breakpoint-lg-min) {
    width: 19%;
  }

  @media (min-width: $breakpoint-xl-min) {
    width: 13%;
  }
}
</style>
