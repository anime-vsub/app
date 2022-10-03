<template>
  <q-page class="text-center flex items-center">
    <div class="w-full pb-15">
      <img src="~assets/ic_22_cry.png" width="240" />
      <slot />
    </div>
  </q-page>
</template>

<script lang="ts" setup></script>

<style lang="scss" scoped>
img {
  width: auto;
  max-width: 240px;
  margin: 0 auto;
}
</style>
