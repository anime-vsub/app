<template>
  <q-btn
    dense
    no-caps
    v-bind="attrs"
    :ripple="false"
    v-for="item in chaps"
    :key="item.id"
    outline
    class="px-4 py-[10px] mx-2 mb-3 rounded-md before:text-[#3a3a3a]"
    :class="{
      [`c--main before:text-[rgb(0,194,52)] ${classActive}`]: find(item),
    }"
    :to="{
      name: 'phim_[season]_[chap]',
      params: {
        season,
        chap: item.id,
      },
    }"
    :ref="(el) => find(item) && (activeRef = el as QTab)"
  >
    {{ item.name }}
  </q-btn>
</template>

<script lang="ts" setup>
import { QBtn } from "quasar";
import type { PhimIdChap } from "src/apis/phim/[id]/[chap]";
import { ref, useAttrs, watchEffect } from "vue"
defineProps<{
  find: (value: Awaited<ReturnType<typeof PhimIdChap>>["chaps"][0]) => boolean
  chaps?: Awaited<ReturnType<typeof PhimIdChap>>["chaps"]
  season: string
  classActive?: string
}>()

const attrs = useAttrs()

const activeRef = ref<QBtn>()

watchEffect(() => {
  activeRef.value?.$el.scrollIntoView({
    inline: "center",
  })
})
</script>

<script lang="ts">
export default {
  inheritAttrs: false,
}
</script>
