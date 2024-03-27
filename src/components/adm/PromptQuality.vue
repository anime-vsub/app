<template>
  <q-dialog v-model="modelValue">
    <q-card
      class="bg-dark-page min-w-[300px] max-h-[647px] max-w-[664px] px-6 rounded-xl !max-h-[80%] flex flex-col flex-nowrap"
    >
      <q-card-section class="py-2 flex items-center justify-between px-0">
        <div class="text-[16px]">{{ $t("chat-luong-tai-xuong") }}</div>
        <q-btn round flat icon="close" v-close-popup />
      </q-card-section>

      <q-card-section class="px-0 pt-0 min-h-0 flex flex-col flex-nowrap">
        <div v-if="loading" class="py-6 px-5 flex items-center justify-center">
          <q-spinner size="40px" />
        </div>
        <div v-else-if="!data" class="py-6 px-3 text-center">
          {{ error ?? "Unknown error" }}
        </div>
        <q-list v-else class="children:px-0">
          <q-item v-for="(source, index) in data" :key="index">
            <q-item-section side class="pr-0">
              <q-radio v-model="sourceIndex" :val="index" />
            </q-item-section>
            <q-item-section class="text-gray-300">
              {{ source.labele }} ({{ source.qualityCode }} -
              {{ source.label }})
            </q-item-section>
            <q-item-section side>
              {{
                sizes?.[index]
                  ? sizes?.[index]?.value !== undefined
                    ? filesize(sizes[index].value, { standard: "jedec" })
                    : "calc..."
                  : "no info"
              }}
            </q-item-section>
          </q-item>
        </q-list>
      </q-card-section>

      <q-card-actions align="right" class="pr-0 mr-[-8px] mt-2">
        <q-btn color="white" rounded flat no-caps @click="modelValue = false">{{
          t("huy")
        }}</q-btn>
        <q-btn
          color="main"
          rounded
          flat
          no-caps
          :disable="sourceIndex === null || !data?.[sourceIndex]"
          @click="download"
          >{{ $t("tai-xuong") }}</q-btn
        >
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script lang="ts" setup>
import { computedAsync } from "@vueuse/core"
import { retry, type SeasonInfo } from "animevsub-download-manager/src/main"
import { filesize } from "filesize"
import { useQuasar } from "quasar"
import { PlayerFB } from "src/apis/runs/ajax/player-fb"
import { PlayerLink } from "src/apis/runs/ajax/player-link"
import type { PhimId } from "src/apis/runs/phim/[id]"
import { PhimIdChap } from "src/apis/runs/phim/[id]/[chap]"

const props = defineProps<{
  data: SeasonInfo | Awaited<ReturnType<typeof PhimId>>
  seasonId: string
  path: string
  uniqueEpisode: string

  currentEpisode: {
    id: string
    play: string
    hash: string
    name: string
  }
}>()

const admStore = useADM()
const { t } = useI18n()
const $q = useQuasar()

const modelValue = defineModel<boolean>("modelValue", { required: true })

const { data, loading, error } = useRequest(() => {
  return Promise.all([
    retry(() => PlayerLink(props.currentEpisode), {
      delay: 300,
      repeat: 5
    }).then((res) => {
      return {
        ...res,
        link: res.link.map((item) => {
          return Object.assign(item, { labele: t("cao-den-full-hd") })
        })
      }
    }),
    retry(
      () =>
        PlayerFB(props.currentEpisode.id).catch((err) => {
          if (err?.not_found) return null
          throw err
        }),
      { delay: 300, repeat: 5 }
    ).then((res) => {
      if (!res) return null
      return {
        ...res,
        link: res.link.map((item) => {
          return Object.assign(item, { labele: t("trung-binh-hoac-thap") })
        })
      }
    })
  ]).then((all) =>
    all
      .filter((item) => item?.playTech === "api" && item.link)
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      .map((item) => item!.link)
      .flat(1)
  )
})
const sizes = computed(() => {
  if (!data.value) return

  return data.value.map((item) => {
    switch (item.type) {
      case "mp4":
      case "mp3":
      case "f4a":
      case "m4v":
      case "mov":
      case "mpeg":
        return computedAsync(() => admStore.adm.getFileSize(item.file))
      case "hls":
      case "m3u8":
      case "m3u":
        return computedAsync(() => admStore.adm.getHlsSize(item.file, 20))
      default:
        return { value: -1 }
    }
  })
})

const sourceIndex = ref<number | null>(null)

// =========== download ==============
async function download() {
  if (!data.value || sourceIndex.value === null) return

  const {
    image,
    poster,
    name,
    othername,
    description,
    pathToView,
    yearOf,
    genre,
    quality,
    authors,
    contries,
    language,
    studio,
    seasonOf,
    trailer,
    views,
    rate,
    // eslint-disable-next-line camelcase
    count_rate,
    duration,
    season,
    follows
  } = props.data

  modelValue.value = false

  void $q.notify({
    message: t("dang-tai-xuong"),
    caption: t("msg-dont-close-tab-dlg"),
    position: "bottom-left",
    timeout: 3_000
  })

  const chaps = await PhimIdChap(props.seasonId)

  await admStore.adm.downloadEpisode(
    {
      seasonId: props.seasonId,
      image,
      poster,
      name,
      othername,
      description,
      pathToView: pathToView ?? undefined,
      yearOf,
      genre,
      quality,
      authors,
      contries,
      language,
      studio,
      seasonOf,
      trailer,
      views,
      rate,
      // eslint-disable-next-line camelcase
      count_rate,
      duration,
      season,
      follows
    },
    { chaps: chaps.chaps, image: chaps.image, poster: chaps.poster },
    props.currentEpisode.id,
    props.uniqueEpisode,
    data.value[sourceIndex.value],
    () => {
      throw new Error(t("msg-app-not-sprt-dl-mpl"))
    }
  )

  localStorage.setItem(`offline-${props.uniqueEpisode}`, "")
}
</script>
