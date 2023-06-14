<template>
  <header class="fixed w-full top-0 left-0 z-200 bg-dark-page">
    <q-toolbar class="relative">
      <q-btn flat dense round class="mr-2" @click.stop="router.back()">
        <Icon icon="fluent:chevron-left-24-regular" width="25" height="25" />
      </q-btn>
      <q-toolbar-title
        class="absolute left-[50%] top-[50%] transform translate-x-[-50%] translate-y-[-50%] text-[16px] max-w-[calc(100%-34px*2)] line-clamp-1"
      >
        {{ t("tai-khoan") }}
      </q-toolbar-title>
    </q-toolbar>
  </header>

  <template v-if="authStore.isLogged">
    <div class="py-15 text-center pt-[47px]">
      <q-avatar size="80px">
        <q-img-custom
          v-if="authStore.user?.avatar"
          :src="forceHttp2(authStore.user.avatar)"
          referrerpolicy="no-referrer"
        />
        <Icon
          v-else
          icon="fluent:person-circle-20-filled"
          width="80"
          height="80"
        />

        <q-btn
          round
          rounded
          icon="camera"
          class="absolute bottom-0 right-0 bg-[rgba(0,0,0,0.5)]"
          size="sm"
        />
      </q-avatar>
    </div>

    <q-list class="text-[15px]">
      <q-item>
        <q-item-section>
          <q-item-label>{{ t("ten") }}</q-item-label>
        </q-item-section>
        <q-item-section side>
          <div class="flex items-center flex-nowrap">
            {{ authStore.user!.name }}
          </div>
        </q-item-section>
      </q-item>

      <q-item>
        <q-item-section>
          <q-item-label>{{ t("email") }}</q-item-label>
        </q-item-section>
        <q-item-section side>
          <div class="flex items-center flex-nowrap">
            {{ authStore.user!.email }}
          </div>
        </q-item-section>
      </q-item>
      <q-item clickable v-ripple @click="showDialogChangePassword = true">
        <q-item-section>
          <q-item-label>{{ t("doi-mat-khau") }}</q-item-label>
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
      <q-btn
        outline
        rounded
        color="red-5"
        class="w-full mt-10"
        @click="logout"
        >{{ t("dang-xuat") }}</q-btn
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

            <div class="flex-1 text-center text-subtitle1">
              {{ t("doi-mat-khau") }}
            </div>

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
                placeholder="{{ t('mat-khau-moi') }}"
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
              {{
                t(
                  "quan-trong-hay-nho-mat-khau-ban-thay-doi-vi-no-kho-co-the-khoi-phuc-lai-duoc-neu-bi-mat"
                )
              }}
            </div>

            <q-btn
              type="submit"
              rounded
              no-caps
              class="text-main w-full"
              outline
              :disable="!new_password"
              >{{ t("doi-mat-khau") }}</q-btn
            >
          </form>
        </q-card-section>
      </q-card>
    </q-dialog>
  </template>
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"
import { useHead } from "@vueuse/head"
import QImgCustom from "components/QImgCustom"
import { useQuasar } from "quasar"
import { isNative } from "src/constants"
import { forceHttp2 } from "src/logic/forceHttp2"
import { useAuthStore } from "stores/auth"
import { computed, ref } from "vue"
import { useI18n } from "vue-i18n"
import { useRouter } from "vue-router"

const { t } = useI18n()

if (!isNative)
useHead(
  computed(() => {
    const title = t("thong-tin-tai-khoan")
    const description = title

    return {
      title,
      description,
      meta: [
        { property: "og:title", content: title },
        { property: "og:description", content: description },
        { property: "og:url" },
      ],
      link: [
        {
          rel: "canonical",
        },
      ],
    }
  })
)

const $q = useQuasar()
const router = useRouter()
const { t } = useI18n()

const authStore = useAuthStore()

const showDialogChangePassword = ref(false)

const showPassword = ref(false)

// eslint-disable-next-line camelcase
const new_password = ref("")

async function changePassword() {
  const loader = $q.loading.show({
    message: t("dang-xac-thuc-vui-long-doi"),
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
      message: t("da-doi-mat-khau"),
    })
    // eslint-disable-next-line camelcase
    new_password.value = ""
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
  } catch (err: any) {
    console.error(err)
    $q.notify({
      position: "bottom-right",
      message: t("doi-mat-khau-that-bai"),
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
    message: t("da-dang-xuat"),
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
