// イニシャル
import Worker from "./resolve-master-manifest.worker?worker"

export async function resolveMasterManifestWorker(
  segments: string[],
  map: Map<string, readonly [string, number]> = new Map(),
  index = 0,
  balance?: number
) {
  return new Promise<void>((resolve, reject) => {
    const worker = new Worker()
    worker.addEventListener(
      "message",
      ({
        data
      }: MessageEvent<
        | {
            map: Record<string, readonly [string, number]>
            err?: string
          }
        | {
            segment: string
            value: readonly [string, number]
          }
      >) => {
        if ("segment" in data) {
          map.set(data.segment, data.value)
          return
        }

        const { err } = data
        // resolve ok
        // for (const name in newMap) map.set(name, newMap[name])

        if (err) reject(new Error(err))
        else resolve()

        // free memory
        worker.terminate()
      }
    )
    worker.addEventListener("messageerror", (event) => {
      WARN(event)
      reject(event)

      // free memory
      worker.terminate()
    })

    // { uris: string[]; old: Record<string, string>  }
    worker.postMessage({
      old: Object.fromEntries(Array.from(map.entries())),
      uris: segments.slice(index, balance ? index + balance : -1)
    })
  })
}
