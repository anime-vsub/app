<template>
  <div>
    <q-btn
      dense
      no-caps
      :ripple="false"
      v-for="item in chaps"
      :key="item.epId"
      outline
      class="px-4 py-[10px] mx-2 rounded-md before:text-[#3a3a3a] overflow-hidden item"
      :class="[
        classItem,
        {
          [`c--main before:text-[rgb(0,194,52)] active ${classActive ?? ''}`]:
            find(item),
          'mb-3': grid,
        },
      ]"
      replace
      :to="`/phim/${season}/${item.epId}`"
      :ref="(el: QBtn) =>void( find(item) && (activeRef = el as QBtn))"
    >
      {{ item.name }}
      <q-linear-progress
        v-if="(tmp = progressChaps?.get(item.epId))"
        :value="tmp.cur / tmp.dur"
        rounded
        color="main"
        class="absolute bottom-[1px] left-0 !h-[3px] w-full progress"
      />
    </q-btn>
  </div>
</template>

<script lang="ts" setup>
import { QBtn } from "quasar"
import type { PhimIdChapReturns } from "src/apis/types/phim/[id]/[chap]"
import { scrollXIntoView, scrollYIntoView } from "src/helpers/scrollIntoView"
import { ref, watchEffect } from "vue"

const props = defineProps<{
  find: (value: PhimIdChapReturns<unknown>["chaps"][0]) => boolean
  chaps?: PhimIdChapReturns<unknown>["chaps"]
  season: string
  classItem?: string
  classActive?: string
  grid?: boolean
  progressChaps?: Map<
    string,
    {
      cur: number
      dur: number
    }
  > | null
  scrollX?: boolean
}>()

const activeRef = ref<QBtn>()
function scrollToView() {
  if (activeRef.value?.$el)
    (props.scrollX ? scrollXIntoView : scrollYIntoView)(activeRef.value.$el)
}

watchEffect(scrollToView)
// eslint-disable-next-line functional/no-let
let tmp: ReturnType<
  Exclude<typeof props.progressChaps, undefined | null>["get"]
>
</script>

<style lang="scss" scoped>
.item {
  &.active {
    .progress {
      bottom: 1px;
      color: $blue;
    }
  }
}
</style>
