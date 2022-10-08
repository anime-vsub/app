<template>
  <q-item clickable @click="authStore.user ? gotoEditProfile() : (showDialogLogin = true)">
    <q-item-section avatar>
      <q-avatar size="55px">
        <img v-if="authStore.user?.avatar" :src="authStore.user.avatar">
        <Icon v-else icon="fluent:person-circle-20-filled" width="55" height="55" />
      </q-avatar>
    </q-item-section>
    <q-item-section>
      <template v-if="authStore.user">
        <q-item-label class="text-subtitle1 text-weight-normal">{{ authStore.user.name }}</q-item-label>
        <q-item-label caption class="text-grey">{{ authStore.user.username }}</q-item-label>
      </template>
      <q-item-label v-else class="text-subtitle1 text-weight-normal">Đăng nhập/Đăng ký</q-item-label>
    </q-item-section>
    <q-item-section side>
      <div class="flex items-center flex-nowrap">
        <q-btn dense round class="mr-1">
          <Icon icon="fluent:scan-dash-24-regular" width="25" height="25" />
        </q-btn>

        <Icon v-if="authStore.user" icon="fluent:chevron-right-24-regular" width="25" height="25" />
      </div>
    </q-item-section>
  </q-item>

  <q-list>
    <q-item clickable v-ripple to="/tai-khoan/setting">
      <q-item-section avatar>
        <Icon icon="fluent:settings-24-regular" width="25" height="25" />
      </q-item-section>
      <q-item-section>
        <q-item-label>Cài đặt</q-item-label>
      </q-item-section>
    </q-item>
    <q-item clickable v-ripple href="mailto:tachibshin@duck.com?subject=Phản hồi ứng dụng git.shin.animevsub">
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

  <!-- dialogs -->
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
            <input v-model="email" required type="email" name="email" class="input" placeholder="E-mail" />
          </div>
          <div class="mt-4 relative flex items-center flex-nowrap input-wrap">
            <input v-model="password" required :type="showPassword ? 'text' : 'password'" name="password" class="input"
              placeholder="Mật khẩu mới" />
            <q-btn dense round class="mr-1" @click="showPassword = !showPassword">
              <Icon v-if="showPassword" icon="fluent:eye-24-regular" width="22" height="22" />
              <Icon v-else icon="fluent:eye-off-24-regular" />
            </q-btn>
          </div>

          <div class="text-grey text-center mt-5 mb-4">Tìm lại mật khẩu</div>

          <q-btn type="submit" no-caps class="bg-main w-full" :disable="!email || !password">Đăng nhập</q-btn>
        </form>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script lang="ts" setup>
  import {
    Icon
  } from "@iconify/vue"
  import {
    ref
  } from "vue"
  import {
    DangNhap
  } from "src/apis/runs/dang-nhap"


  const data = {
    avatar: "http://cdn.animevietsub.cc/data/avatar/user-35738.jpg",
    name: "しん",
    email: "oppoepgergh@gmail.com",
    username: "tachib.shin",
    sex: "male"
  }

  const showDialogLogin = ref(false)

  const showPassword = ref(false)

  const email = ref("")
  const password = ref('')

  import {
    useQuasar
  } from "quasar"
  import {
    useAuthStore
  } from "stores/auth"

  const $q = useQuasar()
  const authStore = useAuthStore()

  async function login() {
    const loader = $q.loading.show({
      message: 'Đang xác thực. Vui lòng đợi...',
      boxClass: 'bg-dark text-light-9',
      spinnerColor: 'main',
      thickness: 2,
      delay: Infinity,

    })

    try {
      const data = await authStore.login(email.value, password.value)

      showDialogLogin.value = false
      $q.notify({
        position: 'bottom-right',
        message: `Đã đăng nhập với tư cách ${data.name}`
      })
    } catch (err) {
      console.error(err)
      $q.notify({
        position: 'bottom-right',
        message: "Đăng nhập thất bại",
        caption: err.message,
      })
    } finally {
      loader()
    }
  }


  import {
    useRouter
  } from "vue-router"

  const router = useRouter()

  function gotoEditProfile() {
    router.push("/tai-khoan/edit-profile")
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
</style>
