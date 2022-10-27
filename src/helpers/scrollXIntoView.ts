export function scrollXIntoView(el: HTMLDivElement) {
  // let parent = el.parentNode.parentNode

  let i = 0
  let parent = el
  while ((parent = parent.parentNode) && i++ < 3) {
    const oldLeft = el.offsetLeft

    const left = el.offsetLeft - (parent.offsetWidth - el.offsetWidth) / 2

    parent.scrollTo({
      left,
      behavior: "smooth",
    })

    if (oldLeft !== el.offsetLeft) break
  }
}
