import "src/logic/patch-request"

if (!("CapacitorWebFetch" in self))
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  (self as unknown as any).CapacitorWebFetch = fetch
