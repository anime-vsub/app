import pLimit from "p-limit"

import { getRedirect } from "./get-redirect"

export async function getSegments(url: string) {
  const manifest = await fetch(url).then((res) => res.text())

  return manifest.split("\n").filter((line) => line.includes("//"))
}

export async function resolveMasterManifest(
  url: string,
  map: Map<string, string> = new Map()
) {
  if (import.meta.env.DEV) console.time(`get ${url}`)
  const segments = await getSegments(url)
  if (import.meta.env.DEV) console.timeEnd(`get ${url}`)

  if (import.meta.env.DEV) console.time(`resolve ${url}`)
  const limit = pLimit(100)
  await Promise.all(
    segments.map((segment) => {
      if (map.has(segment)) return -1
      return limit(async () =>
        map.set(
          segment,
          await getRedirect(new Request(`${segment}#animevsub-vsub_extra`))
        )
      )
    })
  )
  if (import.meta.env.DEV) console.timeEnd(`resolve ${url}`)

  return map
}
