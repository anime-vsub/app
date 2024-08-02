export function findInRangeSet(
  dat: Set<number>,
  value: number,
  offset: number
) {
  for (const item of dat)
    if (value >= item && value <= item + offset) return true
  return false
}
