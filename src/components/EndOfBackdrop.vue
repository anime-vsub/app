<template>
  <div class="end-of-backdrop" :class="{ reverse }"></div>
</template>

<script lang="ts" setup>
defineProps<{
  reverse?: boolean
}>()
</script>

<style lang="scss" scoped>
.end-of-backdrop {
  display: block;
  width: 32px;
  height: 24px;
  background-image: linear-gradient(
    90deg,
    rgba(17, 19, 25, 0) 0%,
    rgb(17, 19, 25) 100%
  );
  &.reverse {
    background-image: linear-gradient(
      270deg,
      rgba(17, 19, 25, 0) 0%,
      rgb(17, 19, 25) 100%
    );
  }
}
</style>
