export function unflat<T>(array: readonly T[], size: number): T[][] {
  const { length } = array
  const max = ~~(length / size)

  const result: T[][] = []

  for (let i = 0; i < max; i++) {
    const index = i * size
    result.push(array.slice(index, index + size))
  }

  if (max * size < length) result.push(array.slice(max * size))

  return result
}
