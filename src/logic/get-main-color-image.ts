export function getMainColorImage(
  img: HTMLImageElement
): [number, number, number] {
  const canvas = document.createElement("canvas")
  canvas.width = img.width
  canvas.height = img.height

  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const ctx = canvas.getContext("2d")!
  ctx.drawImage(img, 0, 0, img.width, img.height)

  const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height)
  const data = imageData.data
  // eslint-disable-next-line one-var
  let r = 0,
    g = 0,
    b = 0

  for (let i = 0; i < data.length; i += 4) {
    r += data[i]
    g += data[i + 1]
    b += data[i + 2]
  }

  r /= data.length / 4
  g /= data.length / 4
  b /= data.length / 4

  return [Math.round(r), Math.round(g), Math.round(b)]
}
