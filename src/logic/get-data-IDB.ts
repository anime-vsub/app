import { get } from "idb-keyval"
import { v4 } from "uuid"

const swReady = !!document.head.querySelector('script[type="flags"][sw]')
if (swReady) {
  // service worker ready
  console.log("%c Service Worker ready!", "color: blue")
}

// create server get data for IndexedDB
const idbCast = swReady && new BroadcastChannel("idb-cast")
export function getDataIDB<T>(key: string) {
  return new Promise<T>((resolve, reject) => {
    if (!idbCast) {
      void get(key)
        .then((data) => {
          // eslint-disable-next-line promise/always-return
          if (data) resolve(data)
          else throw new Error("There is no data in IndexedDB.")
        })
        .catch(reject)

      return
    }

    const id = v4()

    const handler = ({
      data: { id: rId, respond }
    }: MessageEvent<{
      id: string
      respond: {
        ok: true
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        data: any
      }
    }>) => {
      if (id !== rId) return

      if (respond.ok) resolve(respond.data)
      else reject(new Error(respond.data))

      idbCast.removeEventListener("message", handler)
    }

    idbCast.addEventListener("message", handler)
    idbCast.postMessage({
      id,
      key
    })
  })
}
