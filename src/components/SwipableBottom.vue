<template>
  <q-card
    class="bottom"
    @touchstart="onTouchStart"
    @touchmove="onTouchMove"
    @touchend="onTouchEnd"
    @mousedown="onTouchStart"
    @mousemove="onTouchMove"
    @mouseup="onTouchEnd"
    :style="{
      transition: interacting ? 'height 0.1s ease' : undefined,
      height: height + 'px',
    }"
  >
    <slot />
  </q-card>
</template>
<script lang="ts" setup>
import { ref, computed } from "vue"

const breakpoints = [0, innerHeight - (innerWidth * 9) / 16, innerHeight]

const height = ref(breakpoints[1])

const indexAbsHeight = computed(() => {
  let diff = Infinity,
    index = -1
  const h = height.value
  breakpoints.some((item, curIndex) => {
    const curDiff = Math.abs(item - h)

    if (curDiff < diff) {
      diff = curDiff
      index = curIndex
    } else {
      return true
    }
  })

  return index
})

const interacting = ref(false)
addEventListener("mousedown", () => (interacting.value = true))
addEventListener("touchstart", () => (interacting.value = true))
addEventListener("mouseup", (event) => {
  interacting.value = false
  onTouchEnd(event)
})
addEventListener("touchend", (event) => {
  interacting.value = false
  onTouchEnd(event)
})
let hStart, iStart, yStart
let yLast, tLast
function onTouchStart(event) {
  yLast = yStart = (event.changedTouches?.[0] || event).clientY
  hStart = height.value
  tLast = performance.now()
  iStart = indexAbsHeight.value
}
let lastSpeed
function onTouchMove(event) {
  if (!interacting.value) return

  const { clientY } = event.changedTouches?.[0] || event

  const diff = clientY - yStart
  height.value = hStart - diff

  const now = performance.now()
  lastSpeed = (clientY - yLast) / (now - tLast)

  console.log(lastSpeed)

  yLast = clientY
  tLast = now
}
const space = 0.7
function onTouchEnd(event) {
  setTimeout(() => {
    if (lastSpeed > space) {
      // move to down
      console.log("move down")
      height.value = breakpoints[iStart - 1]
    } else if (lastSpeed < -space) {
      // move to up
      console.log("move up")
      height.value = breakpoints[iStart + 1]
    } else {
      height.value = breakpoints[indexAbsHeight.value]
    } // check formandehit
    // const indexGood =
  })
}
</script>
<style lang="scss" scoped>
.bottom {
  width: 100%;
  user-select: none;
  transition: height 0.11s linear;
  will-change: height;
}
</style>
