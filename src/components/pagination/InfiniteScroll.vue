<template>

  <q-infinite-scroll
    ref="infiniteScrollRef"
    @load="(index, done) => emit('load', index, done)"
    :offset="250"
    class="px-4"
    :disable="!settingsStore.infinityScroll"
  >
    <slot />

    <template v-slot:loading>
      <div class="row justify-center q-my-md">
        <q-spinner class="c--main" size="40px" />
      </div>
    </template>
  </q-infinite-scroll>
</template>

<script lang="ts" setup>
  import { ref, defineExpose } from "vue"

  import { useSettingsStore } from "stores/settings"

  const settingsStore = useSettingsStore()
  const infiniteScrollRef = ref<QInfiniteScroll>()

  const emit = defineEmits<{
    (name: "load", fn: (index: number, done: boolean) => void): Promise<void> | void
  }>()

  defineExpose({
    reset() {
      infiniteScrollRef.value?.reset()
    }
  })
</script>

<style>
</style>
