<template>
  <slot />
  <div class="bottom-drop" />
</template>

<style lang="scss" scoped>
.bottom-drop {
  background-image: linear-gradient(
    rgba(17, 19, 25, 0) 0%,
    rgb(17, 19, 25) 100%
  );
  // background: red;
  @apply absolute w-full h-[20px] bottom-0 left-0 pointer-events-none;
}
</style>
