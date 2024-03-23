<template>
  <Comment
    v-for="commentID in commentIDs"
    :key="commentID"
    :cmt-api="cmtApi"
    :user="user"
    :cmt-id="commentID"
    :id-map="idMap"
    @merge="emit('merge', $event)"
  />
</template>

<script lang="ts" setup>
import type {
  AsyncComments,
  Comment as CommentType,
  FBCommentPlugin,
  OGObject,
  PostComment,
  User
} from "fb-comments-web"

import Comment from "./Comment.vue"

defineProps<{
  cmtApi: FBCommentPlugin

  user: User

  commentIDs: string[]
  idMap: Record<string, User | CommentType | OGObject>
}>()
const emit = defineEmits<{
  (
    name: "merge",
    payload: PostComment["payload"] | AsyncComments["payload"]
  ): void
}>()
</script>
