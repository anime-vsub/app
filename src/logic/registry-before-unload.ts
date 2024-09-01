let count = 0

const fn = (event: Event) => {
  event.preventDefault()
  event.returnValue = true
}

export function registerBeforeUnload() {
  count++
  addEventListener("beforeunload", fn)
}
export function unRegisterBeforeUnload() {
  count--
  if (count < 1) removeEventListener("beforeunload", fn)
}
