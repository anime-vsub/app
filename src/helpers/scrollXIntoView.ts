export function scrollXIntoView(el: HTMLDivElement) {
  // let parent = el.parentNode.parentNode

  // eslint-disable-next-line functional/no-let
  let i = 0
  // eslint-disable-next-line functional/no-let
  let parent: HTMLElement | null = el
  // eslint-disable-next-line no-unmodified-loop-condition
  while ((parent = parent.parentNode as HTMLElement) && i++ < 3) {
    const oldLeft = el.offsetLeft

    const left = el.offsetLeft - (parent.offsetWidth - el.offsetWidth) / 2

    parent.scrollTo({
      left,
      behavior: "smooth",
    })

    if (oldLeft !== el.offsetLeft) break
  }
}
