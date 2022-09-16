<template>
  <q-page class="row items-center justify-evenly">
    <example-component
      title="Example component"
      active
      :todos="todos"
      :meta="meta"
    ></example-component>

    <div>
      <div ref="player" />
    </div>
  </q-page>
</template>

<script setup lang="ts">
import ExampleComponent from "components/ExampleComponent.vue";
import type { Meta, Todo } from "components/models";
import jwplayer from "jwplayer8";
import { onMounted, ref } from "vue";

const todos = ref<Todo[]>([
  {
    id: 1,
    content: "ct1",
  },
  {
    id: 2,
    content: "ct2",
  },
  {
    id: 3,
    content: "ct3",
  },
  {
    id: 4,
    content: "ct4",
  },
  {
    id: 5,
    content: "ct5",
  },
]);
const meta = ref<Meta>({
  totalCount: 1200,
});

const player = ref<HTMLDivElement>();

onMounted(() => {
  jwplayer(player.value).setup({
    file: "https://storage.googleapiscdn.com/playlist/62e02a6f2b71af406651c4a0/playlist.m3u8",
  });
});
</script>
