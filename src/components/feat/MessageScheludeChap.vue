<template>
  {{
    t("tap-moi-chieu-vao-_time-_day", [
      dayjs(new Date(`${update[1]}:${update[2]} 1/1/0`)).format("HH:MM"),
      ...(update[0] === currentDay
        ? [t("hom-nay"), ""]
        : [
            update[0] === 0 ? t("chu-nhat") : t("thu-_day", [update[0] + 1]),
            update[0] > currentDay ? t("tuan-sau") : t("tuan-nay")
          ])
    ])
  }}
</template>

<script lang="ts" setup>
import dayjs from "src/logic/dayjs"
import { useI18n } from "vue-i18n"

const { t } = useI18n()

defineProps<{
  update: [number, number, number]
}>()

const currentDay = new Date().getDay() + 1
</script>
