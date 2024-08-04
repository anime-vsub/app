import "src/logic/patch-request"

if (!("CapacitorWebFetch" in self)) window.CapacitorWebFetch = fetch
