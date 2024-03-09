<template>
  <div class="py-1 text-base flex flex-nowrap items-start">
    <q-avatar size="40px">
      <q-img no-spinner :src="author.thumbSrc" :alt="author.name" />
    </q-avatar>

    <div class="text-sm w-full min-w-0 ml-4">
      <!-- main comment -->
      <UseMouseInElement v-slot="{ isOutside }">
        <div class="flex flex-nowrap">
          <div class="w-full min-w-0">
            <!-- header comment -->
            <template v-if="!editing">
              <div>
                <a
                  :href="author.uri"
                  target="_blank"
                  class="text-gray-900 dark:text-white font-semibold"
                >
                  {{ author.name }}
                  <i-bitcoin-icons-verify-filled
                    v-if="author.isVerified"
                    class="text-blue text-1.2em ml-1"
                  />
                </a>

                <time
                  class="text-0.8rem text-gray-600 dark:text-gray-400 ml-2"
                  pubdate
                  :datetime="comment.timestamp.time + ''"
                  :title="comment.timestamp.text"
                  >{{ comment.timestamp.text }}</time
                >
              </div>

              <p class="mt-1 text-gray-500 dark:text-gray-200">
                {{ comment.body.text }}
              </p>

              <div class="flex items-center mx-[-8px] my-1">
                <q-btn
                  round
                  unelevated
                  padding="8px"
                  :disable="!comment.canLike"
                  class="text-gray-500 dark:text-gray-100"
                  @click="like"
                >
                  <i-bxs-like
                    v-if="comment.hasLiked || liked"
                    class="text-1.2em"
                  />
                  <i-bx-like v-else class="text-1.2em" />
                </q-btn>
                <span class="text-sm text-gray-500 dark:text-gray-300">{{
                  comment.likeCount + (liked ? 1 : 0)
                }}</span>

                <q-btn
                  rounded
                  unelevated
                  no-caps
                  class="text-13px text-gray-500 dark:text-gray-100 ml-8px"
                  @click="repling = true"
                >
                  {{ $t("phan-hoi") }}
                </q-btn>
              </div>

              <Reply
                v-model="repling"
                :user="user"
                :cmt-api="cmtApi"
                :comment-id="comment.id"
                small-avatar
                :message-error="$t('msg-err-reply')"
                @done="emit('merge', $event.payload)"
              />
            </template>
            <!-- /header comment -->
            <!-- editor comment -->
            <Reply
              v-else
              v-model="editing"
              :user="user"
              :cmt-api="cmtApi"
              :comment-id="comment.id"
              editor
              no-avatar
              :text="comment.body.text"
              :message-error="$t('msg-err-update-cmt')"
              @done="emit('merge', $event.payload)"
            />
            <!-- /editor comment -->
          </div>

          <!-- button more options -->
          <div v-show="(!isOutside || menuShowing) && !editing">
            <q-btn round>
              <i-solar-menu-dots-bold class="transform rotate-90" />
              <q-menu
                v-model="menuShowing"
                anchor="bottom end"
                self="top end"
                class="rounded-xl"
              >
                <q-list class="min-w-150px">
                  <template v-if="comment.canEdit">
                    <q-item clickable v-close-popup @click="editing = true">
                      <q-item-section avatar class="min-w-0 pr-2">
                        <i-iconamoon-edit-light class="text-1.2em" />
                      </q-item-section>
                      <q-item-section>{{ $t("chinh-sua") }}</q-item-section>
                    </q-item>
                    <q-item clickable v-close-popup @click="remove">
                      <q-item-section avatar class="min-w-0 pr-2">
                        <i-iconamoon-trash-simple-light class="text-1.2em" />
                      </q-item-section>
                      <q-item-section>{{ $t("xoa") }}</q-item-section>
                    </q-item>
                  </template>

                  <template v-else>
                    <q-item
                      clickable
                      v-close-popup
                      :href="comment.reportURI"
                      target="_blank"
                    >
                      <q-item-section avatar class="min-w-0 pr-2">
                        <i-ri-spam-2-line class="text-1.2em" />
                      </q-item-section>
                      <q-item-section>{{
                        $t("danh-dau-la-spam")
                      }}</q-item-section>
                    </q-item>
                    <q-item
                      clickable
                      v-close-popup
                      :href="comment.reportURI"
                      target="_blank"
                    >
                      <q-item-section avatar class="min-w-0 pr-2">
                        <i-ic-outline-report-problem class="text-1.2em" />
                      </q-item-section>
                      <q-item-section>{{
                        $t("bao-cao-voi-facebook")
                      }}</q-item-section>
                    </q-item>
                  </template>
<!-- 
                  <q-separator />

                  <q-item
                    v-if="comment.canEmbed"
                    clickable
                    v-close-popup
                    @click="embed"
                  >
                    <q-item-section avatar class="min-w-0 pr-2">
                      <i-fluent-mdl2-embed class="text-1.2em" />
                    </q-item-section>
                    <q-item-section>{{ $t("nhung") }}</q-item-section>
                  </q-item> -->
                </q-list>
              </q-menu>
            </q-btn>
          </div>
          <!-- /button more options -->
        </div>
      </UseMouseInElement>
      <!-- /main comment -->

      <Comments
        v-if="comment.public_replies"
        :cmt-api="cmtApi"
        :user="user"
        :commentIDs="comment.public_replies.commentIDs"
        :id-map="idMap"
        @merge="emit('merge', $event)"
      />

      <q-btn
        v-if="countMoreReply > 0"
        rounded
        no-caps
        unelevated
        class="text-blue"
        :loading="loadingMoreReply"
        @click="loadMoreReply(comment.public_replies!.afterCursor)"
      >
        <i-solar-alt-arrow-down-bold class="text-1.2em" />
        {{ $t("i-phan-hoi", [countMoreReply]) }}
      </q-btn>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { UseMouseInElement } from "@vueuse/components"
import type {
  AsyncComments,
  Comment,
  FBCommentPlugin,
  OGObject,
  PostComment,
  User
} from "fb-comments-web"

import type { EventBus } from "../index.vue";

import Comments from "./Comments.vue"
import Reply from "./Reply.vue"

const props = defineProps<{
  cmtApi: FBCommentPlugin

  user: User

  cmtId: string

  idMap: Record<string, User | Comment | OGObject>
}>()
const emit = defineEmits<{
  (
    name: "merge",
    payload: PostComment["payload"] | AsyncComments["payload"]
  ): void
}>()

const $q = useQuasar()
const i18n = useI18n()

const bus = inject<EventBus>("bus")
if (!bus) {
  throw new Error("bus event not exists")
}

const menuShowing = ref(false)

const comment = computed(() => props.idMap[props.cmtId] as Comment)
const author = computed(() => props.idMap[comment.value.authorID] as User)

const countMoreReply = computed(() => {
  if (!comment.value.public_replies) return 0

  return (
    comment.value.public_replies.totalCount -
    comment.value.public_replies.commentIDs.length
  )
})

const repling = ref(false)

const liked = ref(false)
async function like() {
    if (props.user.id === "0") {
      // not login
      await bus?.emit("sign_in",undefined)
      return
    }

  liked.value = !liked.value
  try {
    await props.cmtApi.likeComment(comment.value.id, liked.value)
  } catch (err) {
    void $q.notify({
      message: i18n.t("msg-err-update-like"),
      caption: err + ""
    })
  }
}

const loadingMoreReply = ref(false)
// eslint-disable-next-line camelcase
async function loadMoreReply(after_cursor?: string) {
  loadingMoreReply.value = true
  try {
    const cmt = comment.value
    const { payload } = await props.cmtApi.getMoreComments(cmt.id, after_cursor)

    // ぼくはぜんぜんわかない
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    ;(payload.idMap[cmt.id] as Comment).public_replies!.commentIDs = Array.from(
      new Set([
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        ...cmt.public_replies!.commentIDs,
        ...payload.commentIDs
      ])
    )

    payload.commentIDs = []

    emit("merge", payload)
  } catch (err) {
    void $q.notify({
      message: i18n.t("msg-err-update-reply"),
      caption: err + ""
    })
  } finally {
    loadingMoreReply.value = false
  }
}

const editing = ref(false)

function remove() {
    if (props.user.id === "0") {
      // not login
     bus?.emit("sign_in", undefined)
      return
    }

  $q.dialog({
    class: "rounded-xl",
    title: i18n.t("xac-nhan"),
    message: i18n.t("msg-confirn-delete-cmt"),
    cancel: {
      rounded: true,
      unelevated: true,
      noCaps: true,
      class: "text-gray-300",
      color: "transparent"
    },
    ok: {
      rounded: true,
      unelevated: true,
      noCaps: true,
      color: "blue-6"
    },
    persistent: true
  }).onOk(async () => {
    try {
      await props.cmtApi.removeComment(props.cmtId)
      $q.notify({
        message: i18n.t("da-xoa-binh-luan")
      })
    } catch (err) {
      $q.notify({
        message: i18n.t("msg-err-delete-cmt"),
        caption: err + ""
      })
    }
  })
}
// function embed() {}
</script>
