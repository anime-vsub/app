const levels = ["N", "Tr", "T", "V"]

export function formatView(view: number): string {
  if (view < 1000) return view + ""
  const index = levels.findIndex(() => {
    view /= 1000

    return view < 1000
  })
  // while (view > 1000 && i < levels.length - 1) {
  //   view /= 1000
  //   i++
  // }

  return `${parseFloat(view.toFixed(2)).toString().replace(/\./, ",")}${
    levels[index]
  }`
}
