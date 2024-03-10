<template>
  <div v-if="modelValue" class="flex flex-norwap">
    <q-avatar v-if="!noAvatar" :size="smallAvatar ? '28px' : '40px'">
      <q-img no-spinner :src="user.thumbSrc" :alt="user.name" />
    </q-avatar>

    <div class="text-sm w-full min-w-0 ml-4 flex-1">
      <q-input
        v-model="text"
        :placeholder="$t('viet-binh-luan')"
        autogrow
        :autofocus="!noAutofocus"
        color="blue"
        :input-style="{
          fontSize: '14px',
          boxShadow: 'none',
          minHeight: '0px',
          paddingTop: '8px'
        }"
        class="all:!min-h-0"
        @focus="main ? (toolbar = true) : undefined"
      />
      <div v-if="!main || toolbar" class="flex items-center justify-end mt-3">
        <q-btn
          rounded
          unelevated
          no-caps
          class="text-gray-300 mr-3"
          @click="main ? (toolbar = false) : emit('update:modelValue', false)"
          >{{ $t("huy") }}</q-btn
        >
        <q-btn
          rounded
          unelevated
          no-caps
          :color="text ? 'blue-6' : 'grey-9'"
          :disable="!text"
          :loading="sending"
          @click="send"
          >{{ $t("binh-luan") }}</q-btn
        >
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { FBCommentPlugin, PostComment, User } from "fb-comments-web"
import { useQuasar } from "quasar"

import type { EventBus } from "../index.vue"

const props = defineProps<{
  cmtApi: FBCommentPlugin

  user: User

  modelValue: boolean
  commentId?: string
  editor?: true
  main?: true
  text?: string

  smallAvatar?: true
  noAvatar?: true
  noAutofocus?: true

  messageError: string
}>()
const emit = defineEmits<{
  (name: "update:modelValue", value: boolean): void
  (name: "done", result: PostComment): void
}>()

const instance = getCurrentInstance()

const bus = inject<EventBus>("bus")
if (!bus) {
  throw new Error("bus event not exists")
}
bus.on("active_reply", ($) => {
  if ($ === instance) return

  emit("update:modelValue", false)
})
watch(
  () => props.modelValue,
  (val) => {
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    if (val) bus.emit("active_reply", instance!)
  }
)

if (import.meta.env.DEV) {
  watchEffect(() => {
    if (props.editor && !props.commentId)
      console.warn("[Reply.vue]: in mode editor require 'commentId'.")
  })
}

const $q = useQuasar()

const toolbar = ref(false)

const text = ref<string>(props.text ?? "")
watch(
  () => props.text,
  (val) => (text.value = val ?? text.value)
)

const sending = ref(false)
async function send() {
  sending.value = true

  try {
    if (props.user.id === "0") {
      // not login
      await bus?.emit("sign_in", undefined)
      return
    }
    if (props.editor) {
      // edit comment
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      emit("done", await props.cmtApi.editComment(props.commentId!, text.value))
    } else if (props.commentId) {
      emit("done", await props.cmtApi.replyComment(props.commentId, text.value))
    } else {
      emit("done", await props.cmtApi.postComment(text.value))
    }

    if (props.main) toolbar.value = false
    else emit("update:modelValue", false)

    text.value = ""
  } catch (err) {
    void $q.notify({
      message: props.messageError,
      caption: err + ""
    })
  } finally {
    sending.value = false
  }
}
</script>
