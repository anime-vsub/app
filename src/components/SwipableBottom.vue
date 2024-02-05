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
      transition: interacting ? 'height 0s ease' : undefined,
      'max-height': height + 'px'
    }"
  >
    <slot />
  </q-card>
</template>
<script lang="ts" setup>
import { computed, ref } from "vue"

const breakpoints = [0, innerHeight - (innerWidth * 9) / 16, innerHeight]

const height = ref(breakpoints[1])

const indexAbsHeight = computed(() => {
  let diff = Infinity

  let index = -1
  const h = height.value
  // eslint-disable-next-line array-callback-return
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
addEventListener("mouseup", () => {
  interacting.value = false
  onTouchEnd()
})
addEventListener("touchend", () => {
  interacting.value = false
  onTouchEnd()
})

let hStart: number, iStart: number, yStart: number

let yLast: number, tLast: number
function onTouchStart(event: TouchEvent | MouseEvent) {
  yLast = yStart = ((event as TouchEvent).changedTouches?.[0] || event).clientY
  hStart = height.value
  tLast = performance.now()
  iStart = indexAbsHeight.value
}

let lastSpeed: number
function onTouchMove(event: TouchEvent | MouseEvent) {
  if (!interacting.value) return

  const { clientY } = (event as TouchEvent).changedTouches?.[0] || event

  const diff = clientY - yStart
  height.value = hStart - diff

  const now = performance.now()
  lastSpeed = (clientY - yLast) / (now - tLast)

  console.log(lastSpeed)

  yLast = clientY
  tLast = now
}
const space = 0.7
function onTouchEnd() {
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
  max-height: 100%;
  width: 100%;
  user-select: none;
  transition: max-height 0.33s linear;
  will-change: max-height;
  transform: translate3d(0px, 0px, 0px);
  backface-visibility: hidden;
}
</style>
