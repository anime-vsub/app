<template>
  <q-menu @update:model-value="show = $event" class="bg-dark-page rounded-xl">
    <q-card class="transparent max-w-550px">
      <div v-if="loading">Loading</div>
      <q-card-section v-else-if="data" class="flex flex-row flex-nowrap">
        <div>
          <q-img :src="data.image" class="w-150px" />
        </div>

        <div class="min-w-0 pl-4">
          <h3
            class="text-subtitle1 font-weight-medium line-clamp-3 leading-normal"
          >
            {{ data.name }}
          </h3>
          <h4 class="text-grey-5 text-[13px] leading-normal">
            <template v-if="data.views">{{
              t("formatview-data-views-luot-xem", [formatView(data.views)])
            }}</template>
          </h4>

          <div class="flex items-center mt-3">
            <Quality v-if="data.quality" class="!mr-0">{{
              data.quality
            }}</Quality>
            <span class="hr-vertical" />
            {{ data.yearOf }}
            <span class="hr-vertical" />
            <Star :label="data.rate" class="inline-flex" />
            <span class="hr-vertical" />
            {{ data.duration }}
          </div>
          <!-- <div class="text-gray-400 mt-2">{{ t("cap-nhat-toi-tap-_duration", [data.process]) }}</div> -->

          <div class="mt-2 text-[#eee] font-weight-medium">
            {{ t("gioi-thieu") }}
          </div>
          <p class="text-gray-400">{{ data.description }}</p>

          <div class="tags mt-2">
            <router-link
              v-for="item in data.genre"
              :key="item.name"
              :to="item.path"
              class="text-[rgb(28,199,73)]"
            >
              {{ t("tag-_val", [item.name.replace(/ /, "_")]) }}
            </router-link>
          </div>
        </div>
      </q-card-section>
      <div v-else>{{ error }}</div>
    </q-card>
  </q-menu>
</template>

<script lang="ts" setup>
import Quality from "components/Quality.vue"
import Star from "components/Star.vue"
import { QCard, QCardSection, QImg, QMenu } from "quasar"
import type { PhimId } from "src/apis/runs/phim/[id]"
import { WARN } from "src/constants"
import { formatView } from "src/logic/formatView"
import { getDataIDB } from "src/logic/get-data-IDB"
import { getRealSeasonId } from "src/logic/getRealSeasonId"
import { useI18n } from "vue-i18n"

const props = defineProps<{
  url: string
}>()

const router = useRouter()
const { t } = useI18n()

const show = ref(false)

const data = shallowRef<Awaited<ReturnType<typeof PhimId>>>()
const loading = ref(false)
const error = shallowRef<unknown>()

watch(
  () => props.url,
  () => (data.value = undefined)
)
watch(show, async (show) => {
  if (!show && !data.value) {
    loading.value = true
    try {
      data.value = await getSeasonData(props.url)
    } catch (err) {
      error.value = err
    } finally {
      loading.value = false
    }
  }
})

async function getSeasonData(url: string) {
  const {
    params: { season },
    name
  } = await router.resolve(new URL(url).pathname)

  if (name !== "watch-anime" || !season) {
    return WARN("This tab not is watch-anime route")
  }
  const realId = getRealSeasonId(season as string)

  const raw = await getDataIDB<string>(`data-${realId}`)

  if (!raw) throw new Error("Not found data")

  return JSON.parse(raw)
}
</script>
