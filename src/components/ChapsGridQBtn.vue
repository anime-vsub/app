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
    :ref="(el) => find(item) && (activeRef = el)"
  >
    {{ item.name }}
  </q-btn>
</template>

<script lang="ts" setup>
defineProps<{
  find: (value: Awaited<ReturnType<typeof PhimIdChap>>["chaps"][0]) => boolean;
  chaps?: Awaited<ReturnType<typeof PhimIdChap>>["chaps"];
  season: string;
  classActive?: string;
}>();
import { ref, watchEffect, useAttrs } from "vue";

const attrs = useAttrs();

const activeRef = ref<QBtn>();

watchEffect(() => {
  activeRef.value?.$el.scrollIntoView({
    inline: "center",
  });
});
</script>

<script lang="ts">
export default {
  inheritAttrs: false,
};
</script>
