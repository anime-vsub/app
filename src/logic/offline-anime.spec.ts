import fs from "fs"
import https from "https"

// eslint-disable-next-line import/order, vue/prefer-import-from-vue, n/no-extraneous-import
import { NOOP } from "@vue/shared"

// eslint-disable-next-line n/no-extraneous-import
import fetch from "node-fetch"
import { vi } from "vitest"

// eslint-disable-next-line @typescript-eslint/no-explicit-any
;(window as unknown as any).fetch = fetch
// eslint-disable-next-line @typescript-eslint/no-explicit-any
;(window as unknown as any).fs = fs.promises
// eslint-disable-next-line @typescript-eslint/no-explicit-any
;(window as unknown as any).https = https
// eslint-disable-next-line @typescript-eslint/no-explicit-any
;(window as unknown as any).fs.exists = fs.existsSync
// eslint-disable-next-line @typescript-eslint/no-explicit-any
;(window as unknown as any).fs.createWriteStream = fs.createWriteStream

// eslint-disable-next-line import/first
import {
  downloadInfoAnime,
  downloadOfflineAnime,
  getStatusDownload,
} from "./offline-anime"

const dir = "/var/tmp/hls-offline"

describe("offline-anime", () => {
  describe("downloadOfflineAnime", () => {
    test("should download an offline", async () => {
      try {
        fs.rmSync(dir + "/s1", { recursive: true })
      } catch {}

      const fn = vi.fn((e) => e)

      await downloadOfflineAnime({
        season: "s1",
        chap: "[chap]",
        chapName: "chap name",

        file: "https://test-streams.mux.dev/x36xhzz/url_0/193039199_mp4_h264_aac_hd_7.m3u8",
        onprogress: fn,
        signal: new AbortController().signal,
      })

      expect(fs.existsSync(dir + "/s1/[chap]/main.m3u8")).toBe(true)
      expect(fs.existsSync(dir + "/s1/[chap]/main.rawd")).toBe(false)
      expect(fs.existsSync(dir + "/s1/[chap]/meta.json")).toBe(true)
      expect(
        JSON.parse(fs.readFileSync(dir + "/s1/[chap]/meta.json", "utf8"))
          .chapName
      ).toBe("chap name")
      expect(fs.existsSync(dir + "/s1/[chap]/blob")).toBe(true)
      expect(fs.readdirSync(dir + "/s1/[chap]/blob").length > 0).toBe(true)
      expect(fn.mock.results.length).toBe(fn.mock.calls.at(0)?.[0].total)
    }, 60_000)
    test("should not download if downloaded", async () => {
      await downloadOfflineAnime({
        season: "s2",
        chap: "[chap]",
        chapName: "chap name",
        file: "https://test-streams.mux.dev/x36xhzz/url_0/193039199_mp4_h264_aac_hd_7.m3u8",
        onprogress: NOOP,
        signal: new AbortController().signal,
      })

      const fn = vi.fn(NOOP)

      await downloadOfflineAnime({
        season: "s2",
        chap: "[chap]",
        chapName: "chap name",
        file: "https://test-streams.mux.dev/x36xhzz/url_0/193039199_mp4_h264_aac_hd_7.m3u8",
        onprogress: fn,
        signal: new AbortController().signal,
      })

      expect(fn.mock.calls.length).toBe(0)
    }, 60_000)
    test("should pause/continue download", async () => {
      try {
        fs.rmSync(dir + "/s3", { recursive: true })
      } catch {}

      const abort = new AbortController()

      const fnPause = vi.fn((v) => v)

      try {
        await downloadOfflineAnime({
          season: "s3",
          chap: "[chap]",
          chapName: "chap name",
          file: "https://test-streams.mux.dev/x36xhzz/url_0/193039199_mp4_h264_aac_hd_7.m3u8",
          onprogress: ({ loaded }) => {
            if (loaded === 5) abort.abort()
            fnPause(loaded)
          },
          signal: abort.signal,
        })
      } catch (err) {
        // eslint-disable-next-line functional/no-throw-statement
        if ((err as Error).message !== "ABORTED") throw err
      }

      expect(fnPause.mock.results.length).toEqual(5)

      // continue
      const fnConti = vi.fn((v) => v)
      await downloadOfflineAnime({
        season: "s3",
        chap: "[chap]",
        chapName: "chap name",
        file: "https://test-streams.mux.dev/x36xhzz/url_0/193039199_mp4_h264_aac_hd_7.m3u8",
        onprogress: ({ loaded }) => fnConti(loaded),
        signal: new AbortController().signal,
      })

      expect((fnPause.mock.calls.at(-1)?.[0] ?? 0) + 1).toBe(
        fnConti.mock.calls[0][0]
      )
    }, 60_000)
    test("should abort", async () => {
      try {
        fs.rmSync(dir + "/s4", { recursive: true })
      } catch {}
      const abort = new AbortController()
      abort.abort()

      const fn = vi.fn()

      await downloadOfflineAnime({
        season: "s4",
        chap: "[chap]",
        chapName: "chap name",
        file: "https://test-streams.mux.dev/x36xhzz/url_0/193039199_mp4_h264_aac_hd_7.m3u8",
        onprogress: fn,
        signal: abort.signal,
      }).catch(NOOP)

      expect(fn.mock.calls.length).toBe(0)
    })
  })
  describe("downloadInfoAnime", () => {
    test("should download info anime", async () => {
      const data = {
        season: "s5",
        seasonName: "name s5",
        info: {
          name: "test",
          poster:
            "https://file-examples.com/storage/fe2879c03363c669a9ef954/2017/10/file_example_JPG_100kB.jpg",
          image:
            "https://file-examples.com/storage/fe2879c03363c669a9ef954/2017/10/file_example_JPG_100kB.jpg",
          description: "description",
          authors: [],
        },
      }

      await downloadInfoAnime(data)

      expect(fs.existsSync(dir + "/s5/poster.image")).toBe(true)
      expect(fs.existsSync(dir + "/s5/image.image")).toBe(true)
      expect(fs.existsSync(dir + "/s5/meta.json")).toBe(true)
      expect(
        JSON.parse(fs.readFileSync(dir + "/s5/meta.json", "utf8"))
      ).toEqual(data.info)
    })
  })
  describe("getStatusDownload", () => {
    test("should downloaded", async () => {
      try {
        fs.rmSync(dir + "/s6", { recursive: true })
      } catch {}

      await downloadOfflineAnime({
        season: "s6",
        chap: "[chap]",
        chapName: "chap name",
        file: "https://test-streams.mux.dev/x36xhzz/url_0/193039199_mp4_h264_aac_hd_7.m3u8",
        onprogress: NOOP,
        signal: new AbortController().signal,
      })

      expect(
        await getStatusDownload({
          season: "s6",
          chap: "[chap]",
        })
      ).toBe(true)
    }, 60_000)
    test("should downloading", async () => {
      try {
        fs.rmSync(dir + "/s7", { recursive: true })
      } catch {}

      const abort = new AbortController()
      await downloadOfflineAnime({
        season: "s7",
        chap: "[chap]",
        chapName: "chap name",
        file: "https://test-streams.mux.dev/x36xhzz/url_0/193039199_mp4_h264_aac_hd_7.m3u8",
        onprogress({ loaded }) {
          if (loaded === 1) abort.abort()
        },
        signal: abort.signal,
      }).catch(NOOP)

      expect(
        (
          (await getStatusDownload({
            season: "s7",
            chap: "[chap]",
          })) as { loaded: number; total: number }
        ).loaded
      ).toBe(1)
    }, 60_000)
    test("should not download", async () => {
      expect(
        await getStatusDownload({
          season: "s8",
          chap: "[chap]",
        })
      ).toBe(false)
    })
    test("should task in pending", async () => {
      fs.mkdirSync(dir + "/s9/[chap]", { recursive: true })
      fs.writeFileSync(
        dir + "/s9/[chap]/meta.json",
        JSON.stringify({
          name: "chap name",
          created: new Date().getUTCMilliseconds(),
        })
      )

      expect(
        (
          (await getStatusDownload({
            season: "s9",
            chap: "[chap]",
          })) as { loaded: number; total: number }
        ).loaded
      ).toBe(0)
    })
  })
})
