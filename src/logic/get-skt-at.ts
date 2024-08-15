import type { loadVttSk } from "./load-vtt-sk"

export function getSktAt(
  currentTime: number,
  vtt: Awaited<ReturnType<typeof loadVttSk>>
): Awaited<ReturnType<typeof loadVttSk>>[0] | null {
  const Δt = vtt[0].to - vtt[0].from

  // convert current time as seconds to miliseconds
  currentTime *= 1e3
  const index = (currentTime / Δt + 1) & ~1

  if (vtt.length <= index) return null
  return vtt[index]
}
