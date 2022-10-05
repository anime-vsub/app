const cacheStore = new Map<string, unknown>()
setInterval(() => {
  cacheStore.forEach(({ time }, key) => {
    if (performance.now() - time > 60_000) cacheStore.delete(key)
  })
}, 60_000)

export async function useCache<Fn extends (...args: any) => any>(
  key: string,
  fn: Fn
) {
  const inCache = cacheStore.get(key)

  if (inCache) return inCache

  const result = await fn()

  cacheStore.set(key, result)

  return result
}
