import wasmURL from "@ffmpeg/core/wasm?url"
import coreURL from "@ffmpeg/core?url"
import { FFmpeg } from "@ffmpeg/ffmpeg"
import { parse, stringify } from "hls-parser"
import pLimit from "p-limit"
import sha256 from "sha256"
import type { RetryOptions } from "ts-retry";
import { retryAsync } from "ts-retry"

/**
 * Converts an HLS (HTTP Live Streaming) manifest to an MP4 file.
 *
 * @param {string} m3u8Content - The content of the HLS manifest file.
 * @param {(url: string) => Promise<Uint8Array>} fetchFile - A function to fetch a file from a URL.
 * @param {(current: number, total: number, timeCurrent: number, timeDuration: number) => void} onProgress - A callback function to report the progress of the conversion.
 * @param {number} [concurrency=20] - The number of concurrent downloads.
 * @param {RetryOptions} [retryOptions] - Options for retrying failed downloads.
 * @return {Promise<FileData>} The MP4 file data.
 */
export async function convertHlsToMP4(
  m3u8Content: string,
  fetchFile: (url: string) => Promise<Uint8Array>,
  onProgress: (
    current: number,
    total: number,
    timeCurrent: number,
    timeDuration: number,
  ) => void,
  concurrency = 20,
  retryOptions?: RetryOptions,
) {
  const ffmpeg = new FFmpeg()

  const hash = sha256(m3u8Content)
  const manifest = parse(m3u8Content)

  if (!("segments" in manifest))
    throw new Error("Can't support master playlist")

  if (import.meta.env.DEV)
    ffmpeg.on("log", ({ message }) => {
      console.log(`[@ffmpeg/ffmpeg]: ${message}`)
    })

  if (import.meta.env.DEV)
    ffmpeg.on("progress", ({ progress, time }) => {
      console.log(`${progress * 100} % (transcoded time: ${time / 1000000} s)`)
    })

  await ffmpeg.load({
    coreURL,
    wasmURL,
  })

  const limit = pLimit(concurrency)

  const timeDuration = manifest.segments.reduce(
    (prev, cur) => cur.duration + prev,
    0,
  )
  // Download all the TS segments
  let downloaded = 0
  let timeCurrent = 0
  await Promise.all(
    manifest.segments.map((segment, i) =>
      limit(() =>
        retryAsync<void>(async () => {
          const path = `${hash}-${i}.ts`
          await ffmpeg.writeFile(path, await fetchFile(segment.uri))
          segment.uri = path
          downloaded++
          timeCurrent += segment.duration
          onProgress(
            downloaded,
            manifest.segments.length,
            timeCurrent,
            timeDuration,
          )
        }, retryOptions),
      ),
    ),
  )
  await ffmpeg.writeFile(`${hash}-media.m3u8`, stringify(manifest))

  await ffmpeg.exec([
    "-i",
    `${hash}-media.m3u8`,
    ..."-acodec copy -vcodec copy".split(" "),
    `${hash}-output.mp4`,
  ])

  // Retrieve the output file
  const mp4Data = await ffmpeg.readFile(`${hash}-output.mp4`)

  return mp4Data
}
