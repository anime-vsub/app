import pLimit from "p-limit"

addEventListener(
  "message",
  async ({
    data: { uris: segments, old: map },
  }: MessageEvent<{
    uris: string[]
    old: Record<string, readonly [string, number]>
  }>) => {
    try {
      const limit = pLimit(100)

      await Promise.all(
        segments.map((segment, index) => {
          if (segment in map) return -1
          return limit(async () => {
            map[segment] = [
              await fetch(new Request(`${segment}#resolve`)).then((res) =>
                res.text()
              ),
              index,
            ]

            postMessage({ segment, value: map[segment] })
          })
        })
      )

      postMessage({ map })
    } catch (err) {
      postMessage({
        map,
        err: err + "",
      })
    }

    close()
  }
)
