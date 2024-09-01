import { get } from "idb-keyval"
import type { VideoOfflineMeta } from "src/stores/vdm"

import { initStore } from "./has-video-offline"

export function listSeasonOffline() {
  return get(`${REGISTRY_OFFLINE}`, initStore())
    .then((data) =>
      data ? (JSON.parse(data) as Record<string, VideoOfflineMeta>) : null
    )
    .catch(() => null)
}
