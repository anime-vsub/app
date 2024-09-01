import type { UseStore } from "idb-keyval"
import { createStore, get } from "idb-keyval"
import type { VideoOfflineMeta } from "src/stores/vdm"

let store: UseStore
export function initStore() {
  if (!store) store = createStore("vdm", "vdm")
  return store
}

export async function hasVideoOffline(
  realSeasonId: string,
  currentChapId: string
) {
  try {
    const json = await get(
      `${PREFIX_VIDEO}${currentChapId}@${realSeasonId}_m`,
      initStore()
    )
    if (!json) throw new Error("not_found")

    return JSON.parse(json) as VideoOfflineMeta
  } catch (err) {
    WARN(err)
    return null
  }
}
