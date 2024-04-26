<template>
  <div class="relative mx-2">
    <EndOfBackdrop
      class="absolute !h-full top-0 left-0"
      reverse
      v-if="
        statusChaptersScroll !== 'start' && statusChaptersScroll !== 'startImp'
      "
    />
    <div
      class="overflow-x-scroll scrollbar-hide"
      @scroll="onChaptersScroll"
      ref="wapScrollRef"
    >
      <div class="whitespace-nowrap">
        <slot />
      </div>
    </div>
    <EndOfBackdrop
      class="absolute !h-full top-0 right-0"
      v-if="
        statusChaptersScroll !== 'end' && statusChaptersScroll !== 'startImp'
      "
    />
  </div>
</template>

<script lang="ts" setup>
import EndOfBackdrop from "components/EndOfBackdrop.vue"
import { debounce } from "quasar"
import { onMounted, ref } from "vue"

const statusChaptersScroll = ref<"start" | "startImp" | "end" | "pending">(
  "start"
)
const onChaptersScroll = debounce(function ({ target }: Event) {
  const { scrollLeft, offsetWidth, scrollWidth } = target as HTMLDivElement

  if (scrollLeft === 0) {
    statusChaptersScroll.value =
      scrollWidth <= offsetWidth ? "startImp" : "start"

    return
  }
  if (offsetWidth + scrollLeft === scrollWidth) {
    statusChaptersScroll.value = "end"

    return
  }

  statusChaptersScroll.value = "pending"
}, 70)

const wapScrollRef = ref<HTMLDivElement>()
onMounted(() => {
  if (wapScrollRef.value)
    onChaptersScroll({ target: wapScrollRef.value } as unknown as UIEvent)
})
</script>
