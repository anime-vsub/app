<template>
  <div v-intersection.once="onIntersection" />
  <section v-if="intersecting && data" class="relative mt-4">
    <!-- header -->
    <div class="text-h6 flex items-center">
      <h6>{{ $t("i-binh-luan", [data.comments.meta.totalCount]) }}</h6>

      <q-btn rounded unelevated no-caps class="ml-2">
        <i-bi-sort-up v-if="orderByReverse" class="text-1.2em mr-1.5" />
        <i-bi-sort-down v-else class="text-1.2em mr-1.5" />
        {{ $t("sap-xep-theo") }}

        <q-menu anchor="bottom end" self="top end" class="rounded-xl">
          <q-list class="min-w-150px">
            <q-item clickable v-close-popup @click="orderByReverse = true">
              <q-item-section avatar class="min-w-0 pr-2">
                <i-bi-sort-up class="text-1.2em" />
              </q-item-section>
              <q-item-section>{{ $t("moi-nhat") }}</q-item-section>
            </q-item>

            <q-item clickable v-close-popup @click="orderByReverse = false">
              <q-item-section avatar class="min-w-0 pr-2">
                <i-bi-sort-down class="text-1.2em" />
              </q-item-section>
              <q-item-section>{{ $t("cu-nhat") }}</q-item-section>
            </q-item>
          </q-list>
        </q-menu>
      </q-btn>
    </div>
    <!-- /header -->

    <Reply
      model-value
      :cmt-api="commentAPI"
      :user="user!"
      main
      no-autofocus
      :message-error="$t('msg-error-cmt')"
      class="mt-3 mb-4"
      @merge="mergeData"
    />

    <q-infinite-scroll @load="onLoad" :offset="250">
      <Comments
        :cmt-api="commentAPI"
        :user="user!"
        :commentIDs="data.comments.comments.commentIDs"
        :id-map="data.comments.comments.idMap"
        @merge="mergeData"
      />

      <template #loading>
        <div>
          <q-spinner color="white" size="40px" class="mx-auto" />
        </div>
      </template>
    </q-infinite-scroll>

    <div
      v-if="loading"
      class="absolute top-0 left-0 w-full h-full bg-[rgb(17,19,25)] bg-opacity-50 pt-24 flex justify-center"
    >
      <q-spinner color="white" size="40px" />
    </div>
  </section>
  <section v-else class="py-8 flex items-center justify-center">
    <q-spinner color="white" size="40px" />
  </section>
</template>

<script lang="ts" setup>
import { computedAsync } from "@vueuse/core"
import { Http } from "client-ext-animevsub-helper"
import { FBCommentPlugin } from "fb-comments-web"
import type { AsyncComments, PostComment } from "fb-comments-web"
import { useQuasar } from "quasar"
import { WARN } from "src/constants"
import type { ComponentInternalInstance, ShallowRef } from "vue"

import Comments from "./components/Comments.vue"
import Reply from "./components/Reply.vue"

const LIMIT = 10

const props = defineProps<{
  href: string
  lang: string
}>()

const $q = useQuasar()
const i18n = useI18n()

const intersecting = ref(false)
const onIntersection = (({ isIntersecting }: any) => {
  intersecting.value ||= isIntersecting
}) as unknown as any

const orderByReverse = ref(true)
const commentAPI = computed(() => {
  return new FBCommentPlugin({
    href: props.href,
    locale: props.lang,
    limit: LIMIT,
    order_by: orderByReverse.value ? "reverse_time" : "time",
    app: "https://app.animevsub.eu.org",
    fetch: (url, options) => {
      url += "#fb_extrao"

      if (options?.method === "POST") {
        return Http.post({
          url,
          headers: options.headers,
          data: options.body.toString()
        }).then((res) => res.data as string)
      }

      return Http.get({ url, headers: options?.headers }).then(
        (res) => res.data as string
      )
    }
  })
})
const loading = ref(false)
const data = computedAsync<null | Awaited<
  ReturnType<typeof commentAPI.value.setup>
>>(
  async () => {
    console.log("[comment]: start setup...")

    loading.value = true

    try {
      const data = await commentAPI.value.setup()

      data.comments.comments.commentIDs = shallowReactive(
        data.comments.comments.commentIDs
      )
      data.comments.comments.idMap = shallowReactive(
        data.comments.comments.idMap
      )
      data.comments.comments = shallowReactive(data.comments.comments)
      data.comments.meta = shallowReactive(data.comments.meta)

      return shallowReactive(data)
    } catch (err) {
      $q.notify({
        message: i18n.t("msg-err-load-cmt"),
        caption: err + ""
      })
      throw err
    } finally {
      loading.value = false
    }
  },
  null,
  {
    onError: WARN,
    lazy: true
  }
) as ShallowRef<null | Awaited<ReturnType<typeof commentAPI.value.setup>>>

const user = computed(() => {
  return data.value?.comments.meta.actors[data.value.comments.meta.userID]
})

async function onLoad(
  index: number,
  done: (end?: boolean) => void
): Promise<void> {
  if (!data.value) return done()

  const dataMore = await commentAPI.value.getComments(
    data.value.comments.meta.afterCursor
  )

  data.value.comments.comments.commentIDs = Array.from(
    new Set([
      ...data.value.comments.comments.commentIDs,
      ...dataMore.payload.commentIDs
    ])
  )

  Object.assign(data.value.comments.comments.idMap, dataMore.payload.idMap)

  data.value.comments.meta.afterCursor = dataMore.payload.afterCursor

  if (dataMore.payload.commentIDs.length < LIMIT) done(true)
  else done()
}

function mergeData(
  payload: PostComment["payload"] | AsyncComments["payload"]
): void {
  if (!data.value) return

  Object.assign(data.value.comments.comments.idMap, payload.idMap)
}

class Mitt<Events extends Record<string, unknown>> {
  // eslint-disable-next-line @typescript-eslint/ban-types
  private readonly events = new Map<keyof Events, Set<Function>>()

  on<Name extends keyof Events>(name: Name, fn: (event: Events[Name]) => void) {
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    if (this.events.has(name)) this.events.get(name)!.add(fn)
    else this.events.set(name, new Set([fn]))

    onBeforeUnmount(() => this.events.get(name)?.delete(fn))
  }

  emit<Name extends keyof Events>(name: Name, param: Events[Name]) {
    this.events.get(name)?.forEach((fn) => fn(param))
  }
}
const bus = new Mitt<{
  active_reply: ComponentInternalInstance
  sign_in: void
}>()
export type EventBus = typeof bus

bus.on("sign_in", async () => {
  await commentAPI.value.signInPopup()

  const { comments } = await commentAPI.value.setup()
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  Object.assign(data.value!.comments.meta, comments.meta)
})

provide("bus", bus)
</script>
