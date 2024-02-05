export function scrollXIntoView(el: HTMLDivElement) {
  // let parent = el.parentNode.parentNode

  let i = 0

  let parent: HTMLElement | null = el
  // eslint-disable-next-line no-unmodified-loop-condition
  while ((parent = parent.parentNode as HTMLElement) && i++ < 3) {
    const oldLeft = el.offsetLeft

    const left = el.offsetLeft - (parent.offsetWidth - el.offsetWidth) / 2

    parent.scrollTo({
      left,
      behavior: "smooth"
    })

    if (oldLeft !== el.offsetLeft) break
  }
}
export function scrollYIntoView(el: HTMLDivElement, deep = 3) {
  // let parent = el.parentNode.parentNode

  let i = 0

  let parent: HTMLElement | null = el
  // eslint-disable-next-line no-unmodified-loop-condition
  while ((parent = parent.parentNode as HTMLElement) && i++ < deep) {
    const oldTop = el.offsetTop

    const top = el.offsetTop - (parent.offsetHeight - el.offsetHeight) / 2

    parent.scrollTo({
      top,
      behavior: "smooth"
    })

    if (oldTop !== el.offsetTop) break
  }
}
