import "src/logic/patch-request"

if (!("CapacitorWebFetch" in self))
  self.CapacitorWebFetch = fetch
