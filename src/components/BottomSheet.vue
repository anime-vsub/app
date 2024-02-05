<template>
  <teleport to="body">
    <transition name="slide">
      <div
        v-show="open"
        v-bind="attrs"
        class="h-[calc(100%-100vmin/16*9)] bottom-0 fixed left-0 w-full bg-dark flex column flex-nowrap"
      >
        <slot />
      </div>
    </transition>
  </teleport>
</template>

<script lang="ts" setup>
import { useAttrs } from "vue"

const attrs = useAttrs()
defineProps<{
  open?: boolean
}>()
</script>

<script lang="ts">
export default {
  inheritAttrs: false
}
</script>

<style lang="scss">
.slide {
  &-enter-active {
    animation: slide-up 0.44s ease;
    @keyframes slide-up {
      from {
        transform: translateY(100%);
      }
      to {
        transform: translateY(0);
      }
    }
  }
  &-leave-active {
    animation: slide-down 0.44s ease;
    @keyframes slide-down {
      from {
        transform: translateY(0);
      }
      to {
        transform: translateY(100%);
      }
    }
  }
}
</style>
