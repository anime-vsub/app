<template>
  <q-dialog>
    <q-card
      class="bg-dark-page min-w-[300px] max-h-[647px] max-w-[664px] px-6 rounded-xl !max-h-[80%] flex flex-col flex-nowrap"
    >
      <q-card-section class="py-2 flex items-center justify-between px-0">
        <div class="text-[16px]">{{ t("luu-vao") }}</div>
        <q-btn round flat icon="close" v-close-popup />
      </q-card-section>

      <q-card-section class="px-0 pt-0 min-h-0 flex flex-col flex-nowrap">
        <div
          v-if="playlists === undefined"
          class="flex pt-3 pb-9 items-center justify-center px-0"
        >
          <q-spinner color="main" size="40px" />
        </div>
        <div v-else-if="playlists === null" class="pt-3 pb-9 text-center px-0">
          {{ t("khong-co-danh-sach-phat-nao") }}
        </div>
        <q-list v-else class="px-0 overflow-y-auto scrollbar-custom">
          <q-item v-for="item in playlists" :key="item.id" class="px-0">
            <q-item-section avatar class="min-w-0">
              <q-checkbox
                v-model="item.added"
                @update:model-value="
                  $event
                    ? emit('action:add', item.id)
                    : emit('action:del', item.id)
                "
              />
            </q-item-section>
            <q-item-section>
              <q-item-label>{{ item.name }}</q-item-label>
              <q-item-label caption>{{
                t("_size-anime", [item.size])
              }}</q-item-label>
            </q-item-section>
          </q-item>
        </q-list>

        <!-- creator -->
        <q-item
          v-if="!creatingPlaylist"
          class="mx-[-8px] mt-4 rounded-xl"
          clickable
          @click="creatingPlaylist = true"
        >
          <q-item-section avatar class="min-w-0">
            <Icon icon="fluent:add-24-regular" width="20" height="20" />
          </q-item-section>
          <q-item-section>
            <q-item-label>{{ t("tao-danh-sach-phat-moi") }}</q-item-label>
          </q-item-section>
        </q-item>
        <div v-else class="mt-4">
          <q-input
            v-model="nameNewPlaylist"
            bottom-slots
            label=""
            :placeholder="t('nhap-ten-danh-sach-phat')"
            counter
            maxlength="150"
            dense
            class="fix-input"
            stack-label
            :input-style="{
              fontSize: '14px',
              paddingLeft: '0px',
            }"
            color="white"
            :rules="[(val) => !!val || 'Bắt buộc']"
            @keydown.stop
            @keypress.enter="createNewPlaylist"
          >
            <template v-slot:label>
              <div class="text-[18px] text-white mb-3">{{ t("ten") }}</div>
            </template>
          </q-input>

          <q-card-actions align="right" class="pr-0 mr-[-8px] mt-2">
            <q-btn
              color="blue"
              rounded
              flat
              no-caps
              @click="creatingPlaylist = false"
              >{{ t("huy") }}</q-btn
            >
            <q-btn
              color="blue"
              rounded
              flat
              no-caps
              @click="createNewPlaylist"
              >{{ t("tao") }}</q-btn
            >
          </q-card-actions>
        </div>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"
import { computedAsync } from "@vueuse/core"
import { useQuasar } from "quasar"
import { usePlaylistStore } from "stores/playlist"
import { ref } from "vue"
import { useI18n } from "vue-i18n"

const props = defineProps<{
  exists: (id: string) => Promise<boolean> | boolean
}>()
const emit = defineEmits<{
  (name: "action:add", idPlaylist: string): void
  (name: "action:del", idPlaylist: string): void

  (name: "after-create-playlist", idPlaylist: string): Promise<void> | void
}>()
const $q = useQuasar()
const { t } = useI18n()

const playlistStore = usePlaylistStore()

const playlists = computedAsync(
  () => {
    const { playlists } = playlistStore

    if (!playlists) return playlists

    return Promise.all(
      playlists.map(async (item) => {
        return {
          ...item,
          added: await props.exists(item.id),
        }
      })
    )
  },
  undefined,
  {
    onError(err) {
      console.error(err)
    },
  }
)

const creatingPlaylist = ref(false)
const nameNewPlaylist = ref("")

async function createNewPlaylist() {
  try {
    const { id } = await playlistStore.createPlaylist(
      nameNewPlaylist.value,
      false
    )
    await emit("after-create-playlist", id)
    $q.notify({
      position: "bottom-right",
      message: t("da-tao-danh-sach-phat-_name", [nameNewPlaylist.value]),
    })

    creatingPlaylist.value = false
    nameNewPlaylist.value = ""
  } catch (err) {
    $q.notify({
      position: "bottom-right",
      message: (err as Error).message,
    })
  }
}
</script>

<style lang="scss" scoped>
.fix-input :deep(.q-field__native) {
  background-color: transparent !important;

  &,
  &:focus,
  &:focus-visible {
    outline: none !important;
    border: none !important;
    box-shadow: none !important;
  }
}
.fix-input :deep(.q-field__control),
.fix-input :deep(.q-field__append) {
  height: 45px !important;
}
</style>
