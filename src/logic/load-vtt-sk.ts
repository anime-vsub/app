import { parse } from "@plussub/srt-vtt-parser"

export async function loadVttSk(url: string) {
  const text = await fetch(url).then((res) =>
    res.ok ? res.text() : Promise.reject(res)
  )

  const { entries } = parse(text)

  return entries.map((item) => {
    const [x, y, w, h] = item.text.split("xywh=")[1].split(",").map(Number)
    return {
      ...item,
      x: -x,
      y: -y,
      w,
      h
    }
  })
}
