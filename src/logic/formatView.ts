const levels = ["N", "Tr", "T", "V"]

export function formatView(view: number) {
  if (view < 1000) return view
  const index = levels.findIndex((item, index) => {
    view /= 1000

    if (view < 1000) return true
  })
  // while (view > 1000 && i < levels.length - 1) {
  //   view /= 1000
  //   i++
  // }

  return `${view.toFixed(2).replace(/\./, ",")}${levels[index]}`
}
