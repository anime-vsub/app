export function scrollXIntoView(el: HTMLDivElement) {
  // let parent = el.parentNode.parentNode

  parentScroll(el, el.parentNode as HTMLDivElement)
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  parentScroll(el, el.parentNode!.parentNode as HTMLDivElement)
}

function parentScroll(el: HTMLElement, parent: HTMLElement) {
  const left = el.offsetLeft - (parent.offsetWidth - el.offsetWidth) / 2

  parent.scrollTo({
    left,
    behavior: "smooth",
  })
}
