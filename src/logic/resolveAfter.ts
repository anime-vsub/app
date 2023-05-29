export function resolveAfter(ms: number) {
  return new Promise((resolve) => setTimeout(resolve, ms))
}
