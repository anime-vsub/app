<template>
  <div class="w-full text-center">
    <div class="loader w-full" />
    <div>{{ t("dang-lay-du-lieu") }}</div>
  </div>
</template>

<script lang="ts" setup>
import { useI18n } from "vue-i18n"

const { t } = useI18n()
</script>

<style lang="scss" scoped>
.loader {
  padding-top: (216 / 393 * 100%);
  animation: runner 1.5s linear infinite alternate;
  background: {
    position: center;
    size: cover;
    image: url("src/assets/ic_laoding_anim_01.png");
  }

  @keyframes runner {
    from {
      background-image: url("src/assets/ic_laoding_anim_01.png");
    }
    50% {
      background-image: url("src/assets/ic_laoding_anim_02.png");
    }
    to {
      background-image: url("src/assets/ic_laoding_anim_03.png");
    }
  }
}
</style>
