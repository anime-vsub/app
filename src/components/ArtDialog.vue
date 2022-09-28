<template>
  <transition name="art-dialog">
    <div
      class="fixed top-0 left-0 w-full h-full z-100"
      v-if="modelValue"
      @click="emit('update:model-value', false)"
    >
      <div
        class="absolute top-0 right-[72px] rounded-2xl max-w-[calc(50%-72px)] min-w-[100px] my-6 h-[calc(100%-48px)] z-100 overflow-hidden text-stone-200"
        :class="{
          'w-full': fit,
        }"
        @click.stop
      >
        <div
          class="absolute w-full h-full top-0 left-0 bg-[rgba(0,0,0,.4)] backdrop-filter backdrop-blur-[10px]"
        />
        <div
          class="z-2 w-full h-full relative"
          :class="{
            'flex column flex-nowrap': fit,
          }"
        >
          <div
            class="bg-[rgba(45,45,45,0.95)] py-2 px-4 flex items-center justify-between relative"
          >
            {{ title }}

            <q-btn
              dense
              flat
              round
              icon="close"
              class="text-zinc-500"
              @click="emit('update:model-value', false)"
            />
          </div>
          <div
            class="bg-[rgba(28,28,30,0.95)] !min-h-0 px-4 pt-4 relative"
            :class="fit ? 'flex-1' : 'h-full'"
          >
            <slot />

            <div class="bottom-drop" />
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script lang="ts" setup>
defineProps<{
  modelValue: boolean
  title: string
  fit?: boolean
}>()

const emit = defineEmits<{
  (name: "update:model-value", v: false): void
}>()
</script>

<style lang="scss">
.art-dialog {
  &-enter-active,
  &-leave-active {
    transition: transform 0.44s ease-in-out, opacity 0.44s ease-in-out;
  }
  &-enter-from,
  &-leave-to {
    transform: translateX(50vw);
    opacity: 0;
  }
}

.bottom-drop {
  background-image: linear-gradient(
    rgba(17, 19, 25, 0) 0%,
    rgb(17, 19, 25) 100%
  );
  // background: red;
  @apply absolute w-full h-[20px] bottom-0 left-0;
}
</style>
