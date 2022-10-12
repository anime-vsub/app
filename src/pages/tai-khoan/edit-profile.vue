<template>
  <q-header class="bg-dark-page">
    <q-toolbar class="relative">
      <q-btn flat dense round class="mr-2" @click.stop="router.back()">
        <Icon icon="fluent:chevron-left-24-regular" width="25" height="25" />
      </q-btn>
      <q-toolbar-title
        class="absolute left-[50%] top-[50%] transform translate-x-[-50%] translate-y-[-50%] text-[16px] max-w-[calc(100%-34px*2)] line-clamp-1"
      >
        Tài khoản
      </q-toolbar-title>
    </q-toolbar>
  </q-header>

  <template v-if="authStore.isLogged">
    <div class="py-15 text-center">
      <q-avatar size="80px">
        <img v-if="authStore.user?.avatar" :src="authStore.user.avatar" />
        <Icon
          v-else
          icon="fluent:person-circle-20-filled"
          width="80"
          height="80"
        />

        <q-btn
          round
          icon="camera"
          class="absolute bottom-0 right-0 bg-[rgba(0,0,0,0.5)]"
          size="sm"
        />
      </q-avatar>
    </div>

    <q-list class="text-[15px]">
      <q-item>
        <q-item-section>
          <q-item-label>Tên</q-item-label>
        </q-item-section>
        <q-item-section side>
          <div class="flex items-center flex-nowrap">
            {{ authStore.user!.name }}
          </div>
        </q-item-section>
      </q-item>

      <q-item>
        <q-item-section>
          <q-item-label>Email</q-item-label>
        </q-item-section>
        <q-item-section side>
          <div class="flex items-center flex-nowrap">
            {{ authStore.user!.email }}
          </div>
        </q-item-section>
      </q-item>
      <q-item clickable v-ripple @click="showDialogChangePassword = true">
        <q-item-section>
          <q-item-label>Đổi mật khẩu</q-item-label>
        </q-item-section>
        <q-item-section side>
          <div class="flex items-center flex-nowrap">
            <Icon
              icon="fluent:chevron-right-24-regular"
              width="18"
              height="18"
              class="ml-2"
            />
          </div>
        </q-item-section>
      </q-item>
    </q-list>

    <div class="mx-3">
      <q-btn outline color="red-5" class="w-full mt-10" @click="logout"
        >Đăng xuất</q-btn
      >
    </div>

    <!-- dialog change password -->
    <q-dialog
      v-model="showDialogChangePassword"
      position="bottom"
      class="children:!px-0"
      full-width
    >
      <q-card class="h-[60vh] bg-dark-500">
        <q-card-section>
          <div class="flex justify-between">
            <q-btn dense round />

            <div class="flex-1 text-center text-subtitle1">Đổi mật khẩu</div>

            <q-btn dense round icon="close" v-close-popup />
          </div>
        </q-card-section>

        <q-card-section>
          <form @submit.prevent="changePassword">
            <div
              class="mb-10 relative flex items-center flex-nowrap input-wrap"
            >
              <input
                v-model="new_password"
                required
                :type="showPassword ? 'text' : 'password'"
                name="new_password"
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

            <div class="text-grey py-1">
              Quan trọng! Hãy nhớ mật khẩu bạn thay đổi vì nó khó có thể khôi
              phục lại được nếu bị mất.
            </div>

            <q-btn
              type="submit"
              no-caps
              class="text-main w-full"
              outline
              :disable="!new_password"
              >Đổi mật khẩu</q-btn
            >
          </form>
        </q-card-section>
      </q-card>
    </q-dialog>
  </template>
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"
import { useQuasar } from "quasar"
import { useAuthStore } from "stores/auth"
import { ref } from "vue"
import { useRouter } from "vue-router"

const $q = useQuasar()
const router = useRouter()

const authStore = useAuthStore()

const showDialogChangePassword = ref(false)

const showPassword = ref(false)

// eslint-disable-next-line camelcase
const new_password = ref("")

async function changePassword() {
  const loader = $q.loading.show({
    message: "Đang xác thực. Vui lòng đợi...",
    boxClass: "bg-dark text-light-9",
    spinnerColor: "main",
    delay: Infinity,
  })

  try {
    // eslint-disable-next-line camelcase
    await authStore.changePassword(new_password.value)

    showDialogChangePassword.value = false
    $q.notify({
      position: "bottom-right",
      message: "Đã đổi mật khẩu",
    })
    // eslint-disable-next-line camelcase
    new_password.value = ""
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
  } catch (err: any) {
    console.error(err)
    $q.notify({
      position: "bottom-right",
      message: "Đổi mật khẩu thất bại",
      caption: err.message,
    })
  } finally {
    loader()
  }
}

async function logout() {
  await authStore.logout()
  router.back()

  $q.notify({
    position: "bottom-right",
    message: "Đã đăng xuất",
  })
}
</script>

<style lang="scss" scoped>
.input {
  border: none;
  outline: none;
  @apply bg-transparent w-full py-[14px] px-2 block;

  &:focus,
  &:focus-visible {
    // all: initial !important;
    box-shadow: none;
  }
}

.input-wrap {
  border-bottom: 1px solid;
  @apply border-dark-50;
}
</style>
