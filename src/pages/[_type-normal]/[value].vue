<template>
  <q-header class="bg-dark-page">
    <q-toolbar v-if="!data" class="relative">
      <q-skeleton type="QBtn" flat dense round />

      <q-space />
      <q-skeleton
        type="text"
        class="absolute left-[50%] top-[50%] transform translate-x-[-50%] translate-y-[-50%] text-[16px]"
        width="120px"
      />

      <q-skeleton type="QBtn" flat dense round />
    </q-toolbar>

    <template v-else>
      <q-toolbar class="relative">
        <q-btn flat dense round class="mr-2" @click.stop="router.back()">
          <Icon icon="fluent:chevron-left-24-regular" width="25" height="25" />
        </q-btn>

        <q-space />

        <q-toolbar-title
          class="absolute left-[50%] top-[50%] transform translate-x-[-50%] translate-y-[-50%] text-[16px] max-w-[calc(100%-34px*2)] line-clamp-1"
          >{{
            route.params.type_normal === "danh-sach" &&
            route.params.value === "all"
              ? "Mục lục"
              : title
          }}</q-toolbar-title
        >

        <q-btn flat dense round @click="showDialogSorter = true">
          <Icon icon="fluent:filter-24-regular" width="24" height="24" />
        </q-btn>
      </q-toolbar>

      <div class="row text-subtitle1 text-[16px] py-4">
        <div class="col-4 px-1">
          <div
            class="bg-dark-700 px-4 text-zinc-400 py-1 h-[45px] flex items-center justify-between"
            @click="showDialogGener = true"
          >
            Thể loại
            <Icon icon="fluent:chevron-down-24-regular" />
          </div>
        </div>
        <div v-if="defaultsOptions.typer" class="col-4 px-1">
          <div
            class="bg-dark-700 px-4 text-zinc-400 py-1 h-[45px] flex items-center justify-between"
            @click="showDialogSeaser = true"
          >
            Mùa
            <Icon icon="fluent:chevron-down-24-regular" />
          </div>
        </div>
        <div v-else class="col-4 px-1">
          <div
            class="bg-dark-700 px-4 text-zinc-400 py-1 h-[45px] flex items-center justify-between"
            @click="showDialogTyper = true"
          >
            Loại
            <Icon icon="fluent:chevron-down-24-regular" />
          </div>
        </div>
        <div class="col-4 px-1">
          <div
            class="bg-dark-700 px-4 text-zinc-400 py-1 h-[45px] flex items-center justify-between"
            @click="showDialogSorter = true"
          >
            Sắp xếp
            <Icon icon="fluent:chevron-down-24-regular" />
          </div>
        </div>
      </div>

      <div v-if="textFilter" class="text-center text-gray-500 mb-1">
        Đã chọn:
        {{ textFilter }}
      </div>
    </template>
  </q-header>

  <!-- main -->

  <div v-if="data?.items.length === 0" class="text-center py-20">
    <img
      src="~assets/img_tips_error_not_foud.png"
      width="186"
      height="174"
      class="mx-auto"
    />

    <div class="text-subtitle1 mt-1">Không tìm thấy gì cả.</div>
  </div>

  <template v-else>
    <SkeletonGridCard v-if="loading || !data" :count="12" />

    <q-infinite-scroll v-else @load="onLoad" :offset="250">
      <GridCard :items="data.items" />

      <template v-slot:loading>
        <div class="row justify-center q-my-md">
          <q-spinner class="c--main" size="40px" />
        </div>
      </template>
    </q-infinite-scroll>
  </template>

  <template v-if="data?.filter">
    <!-- gener -->
    <q-dialog
      v-model="showDialogGener"
      position="bottom"
      no-route-dismiss
      full-width
    >
      <q-card flat class="w-full pt-3 !max-h-[60vh]">
        <q-card-section class="py-0 flex items-center justify-between">
          <div class="text-subtitle1 mx-1">Loại</div>

          <q-btn
            dense
            flat
            no-caps
            class="text-weight-normal"
            @click="genres.splice(0)"
            >Đặt lại</q-btn
          >
        </q-card-section>

        <q-card-section class="row">
          <div
            v-for="item in data?.filter.gener"
            :key="item.value"
            class="col-4 col-sm-3 col-md-4 px-1 py-1"
          >
            <q-btn
              dense
              no-caps
              flat
              class="py-2 bg-[#292929] text-weight-normal w-full ease-bg"
              :disable="defaultsOptions.gener === item.value"
              @click="toggleGenres(item.value)"
              :class="{
                'bg-main':
                  defaultsOptions.gener === item.value ||
                  indexInGenres(item.value) > -1,
              }"
              >{{ item.text }}</q-btn
            >
          </div>
          <!-- -->
        </q-card-section>

        <q-btn flat no-caps class="w-full py-2 mb-2" v-close-popup>Hủy</q-btn>
      </q-card>
    </q-dialog>

    <!-- seaser -->
    <q-dialog
      v-model="showDialogSeaser"
      position="bottom"
      no-route-dismiss
      full-width
    >
      <q-card flat class="w-full pt-3">
        <q-card-section class="py-0 flex items-center justify-between">
          <div class="text-subtitle1 mx-1">Mùa</div>

          <q-btn
            dense
            flat
            no-caps
            class="text-weight-normal"
            @click="seaser = null"
            >Đặt lại</q-btn
          >
        </q-card-section>

        <q-card-section class="row">
          <div
            v-for="item in data?.filter.seaser"
            :key="item.value"
            class="col-4 col-sm-3 col-md-4 px-1 py-1"
          >
            <q-btn
              dense
              no-caps
              flat
              class="py-2 bg-[#292929] text-weight-normal w-full ease-bg"
              :disable="!!defaultsOptions.seaser"
              @click="seaser = item.value"
              :class="{
                'bg-main':
                  defaultsOptions.seaser === item.value ||
                  seaser === item.value,
              }"
              >{{ item.text }}</q-btn
            >
          </div>
          <!-- -->
        </q-card-section>

        <q-btn flat no-caps class="w-full py-2 mb-2" v-close-popup>Hủy</q-btn>
      </q-card>
    </q-dialog>

    <!-- typer -->
    <q-dialog
      v-model="showDialogTyper"
      position="bottom"
      no-route-dismiss
      full-width
    >
      <q-card flat class="w-full pt-3">
        <q-card-section class="py-0 flex items-center justify-between">
          <div class="text-subtitle1 mx-1">Loại</div>

          <q-btn
            dense
            flat
            no-caps
            class="text-weight-normal"
            @click="typer = null"
            >Đặt lại</q-btn
          >
        </q-card-section>

        <q-card-section class="row">
          <div
            v-for="item in data?.filter.typer"
            :key="item.value"
            class="col-4 col-sm-3 col-md-4 px-1 py-1"
          >
            <q-btn
              dense
              no-caps
              flat
              class="py-2 bg-[#292929] text-weight-normal w-full ease-bg"
              :disable="!!defaultsOptions.typer"
              @click="typer = item.value"
              :class="{
                'bg-main':
                  defaultsOptions.typer === item.value || typer === item.value,
              }"
              >{{ item.text }}</q-btn
            >
          </div>
          <!-- -->
        </q-card-section>

        <q-btn flat no-caps class="w-full py-2 mb-2" v-close-popup>Hủy</q-btn>
      </q-card>
    </q-dialog>

    <!-- sorter -->
    <q-dialog
      v-model="showDialogSorter"
      position="bottom"
      no-route-dismiss
      full-width
    >
      <q-card flat class="w-full pt-3">
        <q-card-section class="py-0 flex items-center justify-between">
          <div class="text-subtitle1 mx-1">Sắp xếp</div>

          <q-btn
            dense
            flat
            no-caps
            class="text-weight-normal"
            @click="sorter = null"
            >Đặt lại</q-btn
          >
        </q-card-section>

        <q-card-section class="row">
          <div
            v-for="item in data?.filter.sorter"
            :key="item.value"
            class="col-4 col-sm-3 col-md-4 px-1 py-1"
          >
            <q-btn
              dense
              no-caps
              flat
              class="py-2 bg-[#292929] text-weight-normal w-full ease-bg"
              @click="sorter = item.value"
              :class="{
                'bg-main': sorter === item.value,
              }"
              >{{ item.text }}</q-btn
            >
          </div>
          <!-- -->
        </q-card-section>

        <q-btn flat no-caps class="w-full py-2 mb-2" v-close-popup>Hủy</q-btn>
      </q-card>
    </q-dialog>

    <!-- filter -->
    <q-dialog
      v-model="showDialogFilter"
      position="bottom"
      no-route-dismiss
      full-width
    >
      <q-card flat class="w-full pt-3 !max-h-[60vh]">
        <div class="relative">
          <!-- sorter -->
          <q-card-section class="py-0 flex items-center justify-between">
            <div class="text-subtitle1 mx-1">Sắp xếp theo</div>

            <q-btn
              dense
              flat
              no-caps
              class="text-weight-normal"
              @click="resetFilter"
              >Đặt lại</q-btn
            >
          </q-card-section>

          <q-card-section class="row">
            <div
              v-for="item in data?.filter.sorter"
              :key="item.value"
              class="col-4 col-sm-3 col-md-4 px-1 py-1"
            >
              <q-btn
                dense
                no-caps
                flat
                class="py-2 bg-[#292929] text-weight-normal w-full ease-bg"
                @click="sorter = item.value"
                :class="{
                  'bg-main': sorter === item.value,
                }"
                >{{ item.text }}</q-btn
              >
            </div>
            <!-- -->
          </q-card-section>

          <!-- gener -->
          <q-card-section class="py-0">
            <div class="text-subtitle1 mx-1">Thể loại</div>
          </q-card-section>

          <q-card-section class="row">
            <div
              v-for="item in showFullGener
                ? data?.filter.gener
                : data?.filter.gener.slice(0, 11)"
              :key="item.value"
              class="col-4 col-sm-3 col-md-4 px-1 py-1"
            >
              <q-btn
                dense
                no-caps
                flat
                class="py-2 bg-[#292929] text-weight-normal w-full min-h-10 ease-bg"
                :disable="defaultsOptions.gener === item.value"
                @click="toggleGenres(item.value)"
                :class="{
                  'bg-main':
                    defaultsOptions.gener === item.value ||
                    indexInGenres(item.value) > -1,
                }"
                >{{ item.text }}</q-btn
              >
            </div>

            <div
              v-if="!showFullGener"
              @click="showFullGener = true"
              class="col-4 col-sm-3 col-md-4 px-1 py-1"
            >
              <q-btn
                dense
                no-caps
                flat
                class="py-2 bg-[#292929] text-weight-normal w-full ease-bg min-h-10"
              >
                <Icon icon="fluent:chevron-down-24-regular" />
              </q-btn>
            </div>
          </q-card-section>

          <!-- typer -->
          <q-card-section class="py-0">
            <div class="text-subtitle1 mx-1">Loại</div>
          </q-card-section>

          <q-card-section class="row">
            <div
              v-for="item in data?.filter.typer"
              :key="item.value"
              class="col-4 col-sm-3 col-md-4 px-1 py-1"
            >
              <q-btn
                dense
                no-caps
                flat
                class="py-2 bg-[#292929] text-weight-normal w-full ease-bg"
                :disable="!!defaultsOptions.typer"
                @click="typer = item.value"
                :class="{
                  'bg-main':
                    defaultsOptions.typer === item.value ||
                    typer === item.value,
                }"
                >{{ item.text }}</q-btn
              >
            </div>
          </q-card-section>

          <!-- seaser -->
          <q-card-section class="py-0">
            <div class="text-subtitle1 mx-1">Mùa</div>
          </q-card-section>

          <q-card-section class="row">
            <div
              v-for="item in data?.filter.seaser"
              :key="item.value"
              class="col-4 col-sm-3 col-md-4 px-1 py-1"
            >
              <q-btn
                dense
                no-caps
                flat
                class="py-2 bg-[#292929] text-weight-normal w-full ease-bg"
                :disable="!!defaultsOptions.seaser"
                @click="seaser = item.value"
                :class="{
                  'bg-main':
                    defaultsOptions.seaser === item.value ||
                    seaser === item.value,
                }"
                >{{ item.text }}</q-btn
              >
            </div>
          </q-card-section>

          <!-- year -->
          <q-card-section class="py-0">
            <div class="text-subtitle1 mx-1">Năm</div>
          </q-card-section>

          <q-card-section class="row">
            <div
              v-for="item in data?.filter.year"
              :key="item.value"
              class="col-4 col-sm-3 col-md-4 px-1 py-1"
            >
              <q-btn
                dense
                no-caps
                flat
                class="py-2 bg-[#292929] text-weight-normal w-full ease-bg"
                :disable="!!defaultsOptions.year"
                @click="year = item.value"
                :class="{
                  'bg-main':
                    defaultsOptions.year === item.value || year === item.value,
                }"
                >{{ item.text }}</q-btn
              >
            </div>
          </q-card-section>
        </div>

        <q-btn flat no-caps class="w-full py-2 mb-2" v-close-popup>Hủy</q-btn>
      </q-card>
    </q-dialog>
  </template>
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue"
import GridCard from "components/GridCard.vue"
import SkeletonGridCard from "components/SkeletonGridCard.vue"
import { TypeNormalValue } from "src/apis/[type_normal]/[value]"
import { computed, reactive, ref, watch } from "vue"
import { useRequest } from "vue-request"
import { useRoute, useRouter } from "vue-router"

const route = useRoute()
const router = useRouter()

const showDialogGener = ref(false)
const showDialogSeaser = ref(false)
const showDialogTyper = ref(false)
const showDialogSorter = ref(false)
const showDialogFilter = ref(false)

const genres = reactive<string[]>([])
const seaser = ref<string | null>(null)
const sorter = ref<string | null>(null)
const typer = ref<string | null>(null)
const year = ref<string | null>(null)

setup() // call if free memory
function setup() {
  const {
    genres: qGenres,
    seaser: qSeaser,
    sorter: qSorter,
    typer: qTyper,
    year: qYear,
  } = route.query

  if (qGenres) {
    const _qgen = (Array.isArray(qGenres) ? qGenres : [qGenres]).filter(
      Boolean
    ) as string[]

    if (
      genres.length !== _qgen.length ||
      genres.some((item) => !_qgen.includes(item))
    ) {
      genres.splice(0)
      genres.push(..._qgen)
    }
  }
  if (qSeaser) seaser.value = qSeaser as string
  if (qSorter) sorter.value = qSorter as string
  if (qTyper) typer.value = qTyper as string
  if (qYear) year.value = qYear as string
}
watch(
  [genres, seaser, sorter, typer, year],
  ([genres, seaser, sorter, typer, year]) => {
    router.replace({
      ...route,
      query: {
        genres,
        seaser,
        sorter,
        typer,
        year,
      },
    })
  },
  { deep: true }
)

const textFilter = computed(() => {
  return (
    data.value &&
    data.value.filter &&
    [
      data.value.filter.gener
        .filter((item) => genres.includes(item.value))
        .map((item) => item.text)
        .join(", "),
      data.value.filter.seaser.find((item) => item.value === seaser.value)
        ?.text,
      data.value.filter.sorter.find((item) => item.value === sorter.value)
        ?.text,
      data.value.filter.typer.find((item) => item.value === typer.value)?.text,
      data.value.filter.year.find((item) => item.value === year.value)?.text,
    ]
      .filter(Boolean)
      .join(" • ")
  )
})

const defaultsOptions = computed<{
  typer?: string
  gener?: string
  seaser?: string
  year?: string
}>(() => {
  // eslint-disable-next-line camelcase
  const { type_normal, value } = route.params as Record<string, string>

  // ":type_normal(quoc-gia|tag)/:value",
  // [":type_normal(season)/:value+"
  // eslint-disable-next-line camelcase
  switch (type_normal) {
    case "danh-sach":
      return { typer: value }
    case "the-loai":
      return { gener: value }
    case "season": {
      const [season, year] = value

      if (year) {
        return { seaser: season, year }
      }

      return { seaser: season }
    }
    default:
      return {}
  }
})

const showFullGener = ref(false)

function resetFilter() {
  if (!textFilter.value) return

  genres.splice(0)
  seaser.value = sorter.value = null
  typer.value = null
  year.value = null
}

function indexInGenres(item: string) {
  return genres.indexOf(item)
}
function toggleGenres(item: string) {
  const index = indexInGenres(item)
  if (index > -1) {
    // remove
    genres.splice(index, 1)
  } else {
    genres.push(item)
  }
}

function fetchTypeNormalValue(page: number, onlyItems: boolean) {
  return TypeNormalValue(
    route.params.type_normal as string,
    route.params.value,
    page,
    onlyItems,
    {
      genres,
      seaser: seaser.value,
      sorter: sorter.value,
      typer: typer.value,
      year: year.value,
    },
    defaultsOptions.value
  )
}

const { data, error, run, loading } = useRequest(() =>
  fetchTypeNormalValue(1, false)
)
watch(error, (error) => {
  if (error)
    router.push({
      name: "not_found",
      params: { pathMatch: route.path },
      query: {
        message: error.message,
        cause: error.cause + "",
      },
    })
})

const title = ref("")
const watcherData = watch(data, (data) => {
  if (!data) return

  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  title.value = data.title!
  watcherData()
})
watch(
  [genres, seaser, sorter, typer, year, defaultsOptions],
  () => {
    console.log("try refresh")
    run()
  },
  { deep: true }
)

// eslint-disable-next-line functional/no-let
let nextPage = 2
async function onLoad(_index: number, done: (stop: boolean) => void) {
  const { curPage, maxPage, items } = await fetchTypeNormalValue(
    nextPage++,
    true
  )

  data.value = Object.assign(data.value ?? {}, {
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    items: [...data.value!.items, ...items],
    curPage,
    maxPage,
  })
  done(curPage === maxPage)
}
</script>

<style lang="scss" scoped>
.ease-bg {
  transition: background-color 0.33s ease;
}
</style>