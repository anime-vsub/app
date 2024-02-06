/// <reference types="@types/serviceworker" />
/*
 * This file (which will be your service worker)
 * is picked up by the build system ONLY if
 * quasar.config.js > pwa > workboxMode is set to "injectManifest"
 */

import { get } from "idb-keyval"
import type PhimIdChap from "src/apis/parser/phim/[id]/[chap]"
import type { PhimId } from "src/apis/runs/phim/[id]"
import { clientsClaim } from "workbox-core"
import {
  cleanupOutdatedCaches,
  createHandlerBoundToURL,
  precacheAndRoute
} from "workbox-precaching"
import { NavigationRoute, registerRoute } from "workbox-routing"

import { productName } from "../package.json"
import { getRealSeasonId } from "../src/logic/getRealSeasonId"

declare const self: ServiceWorkerGlobalScope & typeof globalThis

void self.skipWaiting()
clientsClaim()

addEventListener("activate", (event) => {
  event.waitUntil(clients.claim())
})

const routes = self.__WB_MANIFEST

if (!process.env.PROD)
  routes.unshift({
    revision: "8e797e890e6f46ba7fa411ced71bf2fd",
    url: "index.html"
  })

// Use with precache injection
precacheAndRoute(routes)

cleanupOutdatedCaches()

// Non-SSR fallback to index.html
// Production SSR fallback to offline.html (except for dev)
if (process.env.MODE !== "ssr" || process.env.PROD) {
  const handlerBoundToURL = createHandlerBoundToURL(
    process.env.PWA_FALLBACK_HTML
  )
  registerRoute(
    new NavigationRoute(
      async (event) => {
        const response = await handlerBoundToURL(event)

        const rawHtml = (await response.text()).replace(
          "<head>",
          '<head><script type="flags" sw></script>'
        )

        if (event.url.pathname.startsWith("/phim/")) {
          try {
            const [, anime, episode] = event.url.pathname.slice(1).split("/", 3)

            if (!anime) throw new Error("Season anime not found.")

            const id = getRealSeasonId(anime)

            const currentTime = performance.now()
            const [{ value: rawData }, { value: rawList }] =
              (await Promise.allSettled([
                get(`data-${id}`) as Promise<string>,
                get(`season_data ${anime}`) as Promise<string>
              ])) as { value: string }[]

            if (!rawData)
              throw new Error("Data season not exists on IndexedDB.")

            const time = performance.now() - currentTime

            const episodeId =
              rawList && episode && episode.match(/-(\d+)$/)?.[1]
            const list =
              typeof rawList === "string"
                ? (JSON.parse(rawList) as Awaited<
                    ReturnType<typeof PhimIdChap>
                  >)
                : null
            const image = list?.image
            const poster = list?.poster
            const $listEpisodes = list?.chaps

            const currentEpisode = $listEpisodes
              ? $listEpisodes.find((ep) => {
                  return ep.id === episodeId
                }) ?? $listEpisodes[0]
              : undefined

            const data = JSON.parse(rawData) as Awaited<
              ReturnType<typeof PhimId>
            >

            const title = await generateTitle(
              data.name,
              data.othername,
              currentEpisode?.name
            )
            const description = data.description

            const html = rawHtml
              .replace(
                `<title>${productName}</title>`,
                `<title>${title}</title>`
              )
              .replace(
                /<meta property="?og:title"? content="?[^"]+"?/,
                `<meta property=og:title content="${title}"`
              )

              .replace(
                /<meta name="?description"? content="?[^"]+"?/,
                `<meta name=description content="${description}"`
              )
              .replace(
                /<meta property="?og:description"? content="?[^"]+"?/,
                `<meta property=og:description content="${description}"`
              )

              .replace(
                /<meta property="?og:image"? content="?[^"]+"?/,
                `<meta property=og:image content="${
                  image ?? poster ?? data.poster
                }"`
              )

              .replace(
                "</head>",
                (!process.env.PROD
                  ? `\n<hidden style="display:none">Time: ${time}ms</hidden>\n`
                  : "") +
                  (rawData
                    ? `<script type="data/json" id="anime_info">${
                        process.env.PROD
                          ? rawData
                          : JSON.stringify(JSON.parse(rawData), null, 2)
                      }</script>`
                    : "") +
                  (process.env.PROD ? "\n" : "") +
                  (rawList
                    ? `<script type="data/json" id="anime_list">${
                        process.env.PROD
                          ? rawList
                          : JSON.stringify(JSON.parse(rawList), null, 2)
                      }</script>`
                    : "") +
                  `<link rel="canonical" href="${
                    event.url.protocol + event.url.host
                  }/phim/${id || anime}" />` +
                  "</head>"
              )

            return new Response(html, response)
          } catch (err) {
            console.warn(err)
          }
        }

        return new Response(rawHtml, response)
      },
      { denylist: [/sw\.js$/, /workbox-(.)*\.js$/] }
    )
  )

  //   registerRoute(
  //     ({ url }) => {
  //       console.log({url: url + ""})
  //       return url.pathname.startsWith("/phim/")
  //     },
  //     new CacheFirst({
  //       plugins: [
  //         {
  //           async handlerWillRespond({ response }) {
  //             const html = await response.text()

  // + '<!-- inject ok -->'

  //             return new Response(html, response)
  //           },
  //         },
  //       ],
  //     })
  //   )
}

const messagesEpisode = {
  "vi-VN": "Tập",
  "en-US": "Episode",
  "ja-JP": "話",
  "zn-CN": "集"
} as const
let lang: string | undefined
new BroadcastChannel("lang-change").addEventListener(
  "message",
  (event: MessageEvent<string>) => {
    lang = event.data
  }
)

async function generateTitle(
  name: string,
  othername: string,
  episodeName?: string
) {
  // read cookie

  if (episodeName) {
    if (!lang) lang = await get("lang")
    const message =
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      messagesEpisode[lang! as keyof typeof messagesEpisode] ??
      messagesEpisode["en-US"]

    return `${message} ${episodeName} ${name} ${othername}`
  }

  return `${name} ${othername}`
}

// create server get data for IndexedDB
const idbCast = new BroadcastChannel("idb-cast")
idbCast.addEventListener("message", (async ({
  data: { id, key }
}: MessageEvent<{
  id: string
  key: string
}>) => {
  try {
    idbCast.postMessage({
      id,
      respond: {
        ok: true,
        data: await get(key)
      }
    })
  } catch (err) {
    idbCast.postMessage({
      id,
      respond: {
        ok: false,
        data: err + ""
      }
    })
  }
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
}) as unknown as any)
