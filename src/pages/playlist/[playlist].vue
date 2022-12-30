<template>
  <div v-if="metaPlaylist && movies" class="row">
    <div class="w-[360px]">
      <div class="fixed h-[calc(100vh-58px)] w-[360px] py-4">
        <div
          class="relative h-full bg-[#594d42] px-5 py-6 overflow-hidden rounded-[15px]"
        >
          <div class="absolute top-0 left-0 right-0 bottom-0">
            <div
              class="w-[200%] opacity-[0.7] transform translate-x-[-25%] filter blur-[30px]"
            >
              <img
                v-if="metaPlaylist.poster"
                class="h-auto w-full"
                :src="metaPlaylist.poster"
              />
            </div>
            <div
              class="absolute top-0 left-0 right-0 bottom-0"
              :style="{
                background: `linear-gradient(
                  to bottom,
                  rgba(89, 77, 66, 0.8) 0%,
                  rgba(89, 77, 66, 0.298) 33%,
                  var(--q-dark-page) 100%
                )`,
              }"
            />
          </div>

          <div class="relative z-2">
            <q-img
              v-if="metaPlaylist.poster"
              class="rounded-xl"
              :src="metaPlaylist.poster"
            />
            <div>
              <div
                v-if="!editingName"
                class="text-[28px] text-weight-medium py-6 leading-normal flex items-center justify-between"
              >
                {{ metaPlaylist.name }}
                <q-btn
                  round
                  unelevated
                  dense
                  flat
                  class="text-[14px]"
                  @click="editingName = true"
                >
                  <Icon icon="fluent:edit-24-regular" width="22" height="22" />
                </q-btn>
              </div>
              <form @submit.prevent="updateNewName" v-else class="py-6">
                <q-input
                  v-model="newName"
                  bottom-slots
                  :placeholder="t('tieu-de')"
                  counter
                  maxlength="150"
                  dense
                  class="fix-input"
                  stack-label
                  :input-style="{
                    fontSize: '28px',
                    fontWeight: '500',
                    paddingLeft: '0px',
                  }"
                  color="white"
                  :rules="[(val) => !!val || 'Bắt buộc']"
                />
                <div
                  class="flex items-center justify-end mt-1 mr-[-16px] text-[16px]"
                >
                  <q-btn
                    rounded
                    unelevated
                    no-caps
                    @click="editingName = false"
                    >{{ t("huy") }}</q-btn
                  >
                  <q-btn rounded unelevated no-caps type="submit">{{
                    t("luu")
                  }}</q-btn>
                </div>
              </form>

              <div class="text-[13px] text-[rgba(255,255,255,0.7)]">
                {{ metaPlaylist.size }} video Cập nhật
                {{
                  dayjs(
                    (metaPlaylist.updated ?? metaPlaylist.created).toDate()
                  ).fromNow()
                }}
              </div>
            </div>

            <q-btn
              round
              unelevated
              dense
              class="mt-7 bg-[rgba(255,255,255,0.1)] w-[36px] h-[36px]"
            >
              <Icon
                icon="fluent:more-vertical-24-regular"
                width="25"
                height="25"
              />

              <q-menu class="rounded-xl bg-dark-menu">
                <q-card class="transparent">
                  <q-list class="transparent">
                    <q-item clickable @click="removePlaylist">
                      <q-item-section avatar class="min-w-0">
                        <Icon
                          icon="fluent:delete-24-regular"
                          width="20"
                          height="20"
                        />
                      </q-item-section>
                      <q-item-section>
                        <q-item-label>{{
                          t("xoa-danh-sach-phat")
                        }}</q-item-label>
                      </q-item-section>
                    </q-item>
                  </q-list>
                </q-card>
              </q-menu>
            </q-btn>

            <!-- <div class="row mt-4">
            <div class="col-6 pr-1">
              <q-btn rounded unelevated class="w-full bg-[rgba(255,255,255,0.9)] text-dark" no-caps>
                <div class="w-full line-clamp-1 text-left px-5">
                  <Icon icon="fluent:play-24-filled" width="20" height="20" class="inline-block mr-2" />
                  Phát tất cả
                </div>
              </q-btn>
            </div>
            <div class="col-6 pl-1">
              <q-btn rounded unelevated class="w-full bg-[rgba(255,255,255,0.1)]" no-caps>
                <div class="w-full line-clamp-1 text-left px-5">
                  <Icon icon="ps:random" width="22" height="22" class="inline-block mr-2" />
                  Phát ngẫu nhiên
                </div>
              </q-btn>
            </div>
          </div> -->

            <div v-if="!editingDescription" class="mt-6 flex">
              <p class="flex-1">
                {{ metaPlaylist.description ?? "Không có thông tin mô tả" }}
              </p>
              <q-btn
                round
                unelevated
                dense
                flat
                class="text-[14px]"
                @click="editingDescription = true"
              >
                <Icon icon="fluent:edit-24-regular" width="22" height="22" />
              </q-btn>
            </div>
            <form v-else @submit.prevent="updateNewDescription" class="pt-6">
              <q-input
                v-model="newDescription"
                bottom-slots
                :placeholder="t('mo-ta')"
                counter
                maxlength="5000"
                dense
                class="fix-input"
                stack-label
                :input-style="{
                  paddingLeft: '0px',
                }"
                color="white"
              />
              <div
                class="flex items-center justify-end mt-1 mr-[-16px] text-[16px]"
              >
                <q-btn
                  rounded
                  unelevated
                  no-caps
                  @click="editingDescription = false"
                  >{{ t("huy") }}</q-btn
                >
                <q-btn rounded unelevated no-caps type="submit">{{
                  t("luu")
                }}</q-btn>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <div v-if="movies" class="col pl-4 pt-4">
      <q-infinite-scroll
        v-if="movies.length > 0"
        ref="infiniteScrollRef"
        @load="onLoad"
        :offset="250"
      >
        <q-btn rounded no-caps class="mb-2">
          <Icon
            icon="fluent:text-align-left-24-regular"
            width="23"
            height="23"
            class="mr-2"
          />
          <span class="text-[16px]">{{ t("sap-xep") }}</span>

          <q-menu class="rounded-xl bg-dark-menu" auto-close>
            <q-card class="transparent">
              <q-list class="transparent">
                <q-item
                  clickable
                  :focused="sorter === 'asc'"
                  @click="sorter = 'asc'"
                >
                  <q-item-section>
                    <q-item-label>{{ t("ngay-them-moi-nhat") }}</q-item-label>
                  </q-item-section>
                </q-item>
                <q-item
                  clickable
                  :focused="sorter === 'desc'"
                  @click="sorter = 'desc'"
                >
                  <q-item-section>
                    <q-item-label>{{ t("ngay-them-cu-nhat") }}</q-item-label>
                  </q-item-section>
                </q-item>
              </q-list>
            </q-card>
          </q-menu>
        </q-btn>

        <router-link
          v-for="item in movies"
          :key="item.season"
          class="bg-transparent flex flex-nowrap mb-5 group"
          style="white-space: initial"
          :to="`/phim/${item.season}/${parseChapName(item.nameChap)}-${
            item.chap
          }`"
        >
          <div>
            <q-img
              no-spinner
              :src="item.poster"
              :ratio="1920 / 1080"
              class="!rounded-xl w-[150px]"
            />
          </div>

          <div class="pl-2 flex-1">
            <span
              class="line-clamp-3 mt-1 font-weight-medium leading-normal text-[16px]"
              >{{ item.name }}</span
            >
            <div class="text-grey mt-1">
              <template v-if="item.nameSeason"
                >{{ t("_season-tap", [item.nameSeason]) }}
              </template>
              <template v-else>{{ t("Tap") }}</template>
              {{ item.nameChap }}
            </div>
          </div>

          <div class="items-center invisible flex group-hover:!visible md:pr-6">
            <q-btn flat round @click.prevent>
              <Icon
                icon="fluent:more-vertical-24-regular"
                width="25"
                height="25"
              />

              <q-menu class="rounded-xl bg-dark-menu">
                <q-card class="transparent">
                  <q-list class="transparent">
                    <q-item
                      clickable
                      @click="seasonWantsToBeAddedToOtherPlaylist = item"
                    >
                      <q-item-section avatar class="min-w-0">
                        <Icon
                          icon="fluent:add-square-multiple-16-regular"
                          width="20"
                          height="20"
                        />
                      </q-item-section>
                      <q-item-section>
                        <q-item-label>{{
                          t("them-vao-danh-sach-phat")
                        }}</q-item-label>
                      </q-item-section>
                    </q-item>
                    <q-item
                      clickable
                      @click="removeAnimePlaylist(undefined, item.season)"
                    >
                      <q-item-section avatar class="min-w-0">
                        <Icon
                          icon="fluent:delete-24-regular"
                          width="20"
                          height="20"
                        />
                      </q-item-section>
                      <q-item-section>
                        <q-item-label>{{
                          t("xoa-khoi-danh-sach-phat")
                        }}</q-item-label>
                      </q-item-section>
                    </q-item>
                  </q-list>
                </q-card>
              </q-menu>
            </q-btn>
          </div>
        </router-link>

        <template v-slot:loading>
          <div class="row justify-center q-my-md">
            <q-spinner class="c--main" size="40px" />
          </div>
        </template>
      </q-infinite-scroll>
      <div v-else class="text-center pt-4">
        {{ t("chua-co-video-nao-trong-danh-sach-phat-nay") }}
      </div>
    </div>
  </div>

  <AddToPlaylist
    :model-value="seasonWantsToBeAddedToOtherPlaylist !== null"
    @update:model-value="
      (event: boolean) => {
        if (!event) seasonWantsToBeAddedToOtherPlaylist = null
      }
    "
    :exists="fnExists"
    @action:add="addAnimePlaylist"
    @action:del="removeAnimePlaylist"
    @after-create-playlist="addAnimePlaylist"
  />
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"
import { useHead } from "@vueuse/head"
import AddToPlaylist from "components/AddToPlaylist.vue"
import { QInfiniteScroll, useQuasar } from "quasar"
import dayjs from "src/logic/dayjs"
import { parseChapName } from "src/logic/parseChapName"
import { usePlaylistStore } from "stores/playlist"
import { computed, ref, watch } from "vue"
import { useI18n } from "vue-i18n"
import { useRequest } from "vue-request"
import { useRoute, useRouter } from "vue-router"

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const $q = useQuasar()
const playlistStore = usePlaylistStore()

const infiniteScrollRef = ref<QInfiniteScroll>()

const {
  data: metaPlaylist,
  run: runGetMetaPlaylist,
  error: errorMetaPlaylist,
} = useRequest(
  async () => {
    const { playlists } = playlistStore
    if (!playlists) return playlists

    const meta = playlists.find((item) => item.id === route.params.playlist)
    // eslint-disable-next-line functional/no-throw-statement
    if (!meta) throw new Error("NOT_FOUND")

    const poster = await playlistStore.getPosterPlaylist(meta.id)

    return { ...meta, poster }
  },
  {
    refreshDeps: [() => playlistStore.playlists, () => route.params.playlist],
    refreshDepsAction() {
      runGetMetaPlaylist()
    },
  }
)
watch(errorMetaPlaylist, (err) => {
  if (err?.message === "NOT_FOUND") {
    $q.notify({
      message: t("danh-sach-phat-khong-ton-tai"),
    })
    router.push({
      name: "not_found",
      params: {
        catchAll: route.path.split("/").slice(1),
      },
      query: route.query,
      hash: route.hash,
    })
  }
})
const sorter = ref<"asc" | "desc">("asc")
const { data: movies, run: runGetMovies } = useRequest(
  () => {
    console.log("larger movies")
    infiniteScrollRef.value?.reset()

    return playlistStore.getAnimesFromPlaylist(
      route.params.playlist as string,
      undefined,
      sorter.value
    )
  },
  {
    refreshDeps: [() => route.params.playlist, sorter],
    refreshDepsAction() {
      runGetMovies()
    },
  }
)

useHead(
  computed(() => {
    if (!metaPlaylist.value) return {}

    const title = `(${metaPlaylist.value.size}) ${metaPlaylist.value.name}`

    const description = metaPlaylist.value.description ?? title

    return {
      title,
      description,
      meta: [
        {
          property: "og:title",
          content: title,
        },
        {
          property: "og:description",
          content: description,
        },
        {
          property: "og:url",
          content: process.env.APP_URL + `playlist/${route.params.playlist}`,
        },
      ],
      link: [
        {
          rel: "canonical",
          href: process.env.APP_URL + `playlist/${route.params.playlist}`,
        },
      ],
    }
  })
)

async function onLoad(index: number, done: (end: boolean) => void) {
  const { playlists } = playlistStore
  if (!playlists) return done(true)

  const meta = playlists.find((item) => item.id === route.params.playlist)
  // eslint-disable-next-line functional/no-throw-statement
  if (!meta) throw new Error(t("danh-sach-phat-khong-ton-tai"))

  const moviesMore = await playlistStore.getAnimesFromPlaylist(
    meta.id,
    movies.value?.[movies.value.length - 1]?.$doc,
    sorter.value
  )
  movies.value = [...(movies.value ?? []), ...moviesMore]

  done(moviesMore.length === 0)
}

const editingName = ref(false)
const editingDescription = ref(false)
const newName = ref("")
watch(editingName, (editing) => {
  if (editing) {
    newName.value = metaPlaylist.value?.name ?? ""
  } else {
    newName.value = ""
  }
})
const newDescription = ref("")
watch(editingDescription, (editing) => {
  if (editing) {
    newDescription.value = metaPlaylist.value?.description ?? ""
  } else {
    newDescription.value = ""
  }
})

async function updateNewName() {
  try {
    // eslint-disable-next-line functional/no-throw-statement
    if (!metaPlaylist.value) throw new Error(t("danh-sach-phat-khong-ton-tai"))
    await playlistStore.renamePlaylist(metaPlaylist.value.id, newName.value)
    editingName.value = false
    $q.notify({
      position: "bottom-right",
      message: t("da-cap-nhat-ten-danh-sach-phat"),
    })
  } catch (err) {
    $q.notify({
      position: "bottom-right",
      message: (err as Error).message,
    })
  }
}
async function updateNewDescription() {
  try {
    // eslint-disable-next-line functional/no-throw-statement
    if (!metaPlaylist.value) throw new Error(t("danh-sach-phat-khong-ton-tai"))
    await playlistStore.setDescriptionPlaylist(
      metaPlaylist.value.id,
      newDescription.value
    )
    editingDescription.value = false
    $q.notify({
      position: "bottom-right",
      message: t("da-cap-nhat-mo-ta-danh-sach-phat"),
    })
  } catch (err) {
    $q.notify({
      position: "bottom-right",
      message: (err as Error).message,
    })
  }
}

async function removePlaylist() {
  try {
    // eslint-disable-next-line functional/no-throw-statement
    if (!metaPlaylist.value) throw new Error(t("danh-sach-phat-khong-ton-tai"))
    await playlistStore.deletePlaylist(metaPlaylist.value.id)
    editingDescription.value = false
    router.push("/tai-khoan/follow")
    $q.notify({
      position: "bottom-right",
      message: t("da-xoa-danh-sach-phat"),
    })
  } catch (err) {
    $q.notify({
      position: "bottom-right",
      message: (err as Error).message,
    })
  }
}

const seasonWantsToBeAddedToOtherPlaylist = ref<
  Exclude<typeof movies.value, undefined>[0] | null
>(null)
const fnExists = (idPlaylist: string) => {
  if (!seasonWantsToBeAddedToOtherPlaylist.value) return false
  return playlistStore.hasAnimeOfPlaylist(
    idPlaylist,
    seasonWantsToBeAddedToOtherPlaylist.value.season
  )
}

async function addAnimePlaylist(idPlaylist: string) {
  if (!seasonWantsToBeAddedToOtherPlaylist.value) return
  try {
    await playlistStore.addAnimeToPlaylist(
      idPlaylist,
      seasonWantsToBeAddedToOtherPlaylist.value.season,
      seasonWantsToBeAddedToOtherPlaylist.value.$doc.data()
    )
    $q.notify({
      position: "bottom-right",
      message: t("da-theo-vao-danh-sach-phat"),
    })
  } catch (err) {
    $q.notify({
      position: "bottom-right",
      message: (err as Error).message,
    })
  }
}
async function removeAnimePlaylist(
  idPlaylist = metaPlaylist.value?.id,
  season = seasonWantsToBeAddedToOtherPlaylist.value?.season
) {
  if (!idPlaylist || !season) return
  try {
    await playlistStore.deleteAnimeFromPlaylist(idPlaylist, season)
    $q.notify({
      position: "bottom-right",
      message: t("da-xoa-khoi-danh-sach-phat"),
    })
    movies.value = movies.value?.filter((item) => item.season !== season)
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
