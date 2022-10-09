const cacheStore = new Map<
  string,
  {
    time: number
    result: unknown
  }
>()
setInterval(() => {
  cacheStore.forEach(({ time }, key) => {
    if (performance.now() - time > 60_000) cacheStore.delete(key)
  })
}, 60_000)

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export async function useCache<Fn extends (...args: any) => any>(
  key: string,
  fn: Fn
): Promise<Awaited<ReturnType<Fn>>> {
  const inCache = cacheStore.get(key)

  if (inCache) return inCache.result as Awaited<ReturnType<Fn>>

  const result = await fn()

  cacheStore.set(key, { time: performance.now(), result })

  return result
}
