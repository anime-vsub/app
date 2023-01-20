import { Http } from "@capacitor-community/http"
import { Directory } from "@capacitor/filesystem"
import { dirname, join } from "path-browserify"
import type { PhimId } from "src/apis/runs/phim/[id]"
import { useFs } from "src/logic/fs"

const fsData = import.meta.env.TEST
  ? // eslint-disable-next-line @typescript-eslint/no-explicit-any
    ((window as unknown as any).fs as ReturnType<typeof useFs>)
  : useFs(Directory.Data)

type MetaSeason = Pick<
  Awaited<ReturnType<typeof PhimId>>,
  "name" | "poster" | "image" | "description" | "authors"
> & {
  season: string
  seasonName: string
}
interface MetaChap {
  chapName: string
  created: number
}

async function downloadFile(url: string, uri: string): Promise<void> {
  if (import.meta.env.TEST) {
    await new Promise<void>((resolve) => {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      const file = (window as unknown as any).fs.createWriteStream(uri)
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      ;(window as unknown as any).https.get(url, (response: any) => {
        response.pipe(file)

        // after download completed close filestream
        file.on("finish", resolve)
      })
    })

    return
  }

  await Http.downloadFile({
    url,
    filePath: uri,
    fileDirectory: Directory.Data,
  })
}

function showPopupSelectQuality(
  parentFileM3u8: string,
  lines: string[],
  i: number
) {
  const qualities: { name: string; url: string }[] = []

  for (; i < lines.length; i++) {
    const curLine = lines[i].trim()
    if (curLine === "#EXT-X-ENDLIST") break

    if (curLine.startsWith("#EXT-X-STREAM-INF")) {
      const url = new URL(lines[++i], parentFileM3u8).toString()
      const name =
        /(?<=NAME="?)[^,]+(?:"?)/.exec(curLine)?.[0]?.replace(/"/g, "") ??
        "unknown"

      qualities.push({ name, url })
    }
  }

  console.log(qualities)
}

function getCountExtInf(lines: string[]) {
  // eslint-disable-next-line functional/no-let
  let count = 0

  // eslint-disable-next-line functional/no-let
  for (let i = 0; i < lines.length; i++) {
    if (lines[i].startsWith("#EXTINF")) {
      count++
      i++
    }
  }

  return count
}

/**
Progress:
  - save file m3u8 to [filename].raw
  - read and use `appendFile` save new file m3u8 with name [filename]
    (important: line length [filename].raw sync line length [filename])
  - download file progress and apply lines
*/
/**
 * @param dir: /hls-offline/[base64(season)]/[base64(chap)]/
 */
export async function downloadOfflineAnime({
  season,
  chap,
  chapName,
  file,
  onprogress,
  signal,
  created
}: {
  season: string
  chap: string
  chapName: string
  file: string
  onprogress: (evt: { loaded: number; total: number }) => void
  signal: AbortSignal
  created: number
}) {
  const dir = ((import.meta.env.TEST ? "/var/tmp/" : "/") +
    `hls-offline/${season}/${chap}/`) as `/${string}/hls-offline/${string}/${string}/`

  await fsData.mkdir(dir + "blob", { recursive: true })
  if (!(await fsData.exists(join(dir + "meta.json"))))
    await fsData.writeFile(
      join(dir, "meta.json"),
      JSON.stringify(<MetaChap>{
        chapName,
        created
      })
    )

  const parentFileM3u8 = dirname(file)
  const pathSaveM3u8 = join(dir, "main.m3u8")
  const pathRawdM3u8 = join(dir, "main.rawd")
  const existsSaveM3u8 = await fsData.exists(pathSaveM3u8)
  const existsRawdM3u8 = await fsData.exists(pathRawdM3u8)
  if (existsSaveM3u8 && !existsRawdM3u8) return

  const m3u8 = existsRawdM3u8
    ? (await fsData.readFile(pathRawdM3u8, "utf8")).trimEnd()
    : await fetch(file).then((res) => res.text())

  const lines = m3u8.split("\n")
  const linesInSaveM3u8 = (
    await fsData.readFile(pathSaveM3u8, "utf8").catch(() => "")
  )
    .trimEnd()
    .split("\n")

  // check if file [pathSaveM3u8] exists continue download

  if (lines.length === linesInSaveM3u8.length) {
    // download donee. remove file temporary if exists
    // eslint-disable-next-line @typescript-eslint/no-empty-function
    await fsData.unlink(pathRawdM3u8).catch(() => {})
    return
  }

  if (lines.length < linesInSaveM3u8.length) {
    lines.splice(linesInSaveM3u8.length)
  }
  if (!existsRawdM3u8) await fsData.writeFile(pathRawdM3u8, m3u8) // mark [dir] is task downloading

  // if is string: is media file
  // else: is progress file m3u8

  const maxI = lines.length - 1
  const taskCurrent: [
    Promise<[string, string]>?,
    Promise<[string, string]>?,
    Promise<[string, string]>?
  ] = []

  const countResource = getCountExtInf(lines)
  console.log("[total]: %s", countResource)

  // eslint-disable-next-line functional/no-let
  let realIndex = existsSaveM3u8 ? getCountExtInf(linesInSaveM3u8) : 0
  // eslint-disable-next-line functional/no-let
  for (let i = 0; i <= maxI; i++) {
    // eslint-disable-next-line functional/no-throw-statement
    if (signal.aborted) throw new Error("ABORTED")

    const curLine = lines[i].trim()

    const isExtInf = curLine.startsWith("#EXTINF")
    const isEnd = !isExtInf && curLine === "#EXT-X-ENDLIST"

    if (curLine.startsWith("#EXT-X-STREAM-INF")) {
      // this file is m3u8

      showPopupSelectQuality(parentFileM3u8, lines, i)

      break
    }
    if (isExtInf) {
      // this file is resource
      const nextLine = lines[++i]

      if (!nextLine.startsWith("file://")) {
        const url = new URL(nextLine, parentFileM3u8).toString()
        const uri = dir + "blob/" + realIndex
        taskCurrent.push(
          downloadFile(url, uri).then(() => {
            onprogress({ loaded: realIndex, total: countResource })
            return [uri, curLine]
          })
        )
      }
      realIndex++
    }

    if (taskCurrent.length === 3 || isExtInf || i === maxI) {
      // if task full start run
      // if end file run task
      const donees = await Promise.all(taskCurrent)
      for (const t of donees) {
        if (!t) continue

        const [savePath, tag] = t
        await fsData.appendFile(
          pathSaveM3u8,
          tag + "\n" + `file://${savePath}` + "\n"
        )
      }

      taskCurrent.splice(0)
    }

    if (isExtInf) continue

    await fsData.appendFile(pathSaveM3u8, curLine + "\n")
    if (isEnd) break
  }

  await fsData.unlink(pathRawdM3u8)
  console.log("done")
}
export async function downloadInfoAnime({
  season,
  info,
}: {
  season: string
  seasonName: string
  info: Pick<
    Awaited<ReturnType<typeof PhimId>>,
    "name" | "poster" | "image" | "description" | "authors"
  >
}) {
  const dir = ((import.meta.env.TEST ? "/var/tmp/" : "/") +
    `hls-offline/${season}/`) as `/${string}/hls-offline/${string}/`

  await fsData.mkdir(dir, { recursive: true })
  const str = JSON.stringify(info, null, 2).trim()

  try {
    const local = await fsData.readFile(`${dir}/meta.json`, "utf8")
    if (str === local) return
  } catch {}

  await Promise.all([
    info.poster
      ? downloadFile(info.poster, `${dir}/poster.image`)
      : Promise.resolve(),
    info.image
      ? downloadFile(info.image, `${dir}/image.image`)
      : Promise.resolve(),
    fsData.writeFile(`${dir}/meta.json`, str),
  ])

  return info
}
/**
 *
 * @returns boolean | { total: number; loaded: number; }:
 * - true is loaded
 * - false is not loaded
 * - other is downloading
 */
export async function getStatusDownload({
  season,
  chap,
}: {
  season: string
  chap: string
}) {
  const dir = ((import.meta.env.TEST ? "/var/tmp/" : "/") +
    `hls-offline/${season}/${chap}/`) as `/${string}/hls-offline/${string}/${string}/`

  if (!(await fsData.exists(join(dir, "meta.json")))) return false

  const pathSaveM3u8 = join(dir, "main.m3u8")
  const pathRawdM3u8 = join(dir, "main.rawd")
  const existsSaveM3u8 = await fsData.exists(pathSaveM3u8)
  const existsRawdM3u8 = await fsData.exists(pathRawdM3u8)
  if (!existsRawdM3u8) return existsSaveM3u8 || { loaded: 0, total: Infinity }

  const m3u8 = (await fsData.readFile(pathRawdM3u8, "utf8")).trimEnd()

  const lines = m3u8.split("\n")
  const linesInSaveM3u8 = (
    await fsData.readFile(pathSaveM3u8, "utf8").catch(() => "")
  )
    .trimEnd()
    .split("\n")

  // check if file [pathSaveM3u8] exists continue download

  if (lines.length === linesInSaveM3u8.length) {
    // download donee. remove file temporary if exists
    // eslint-disable-next-line @typescript-eslint/no-empty-function
    await fsData.unlink(pathRawdM3u8).catch(() => {})
    return true
  }

  return {
    loaded: getCountExtInf(linesInSaveM3u8),
    total: getCountExtInf(lines),
  }
}
export async function getListDownload() {
  const dir = (import.meta.env.TEST ? "/var/tmp/" : "/") + "hls-offline/"

  const animes = await Promise.all(
    (
      await fsData.readdir(dir)
    ).map(async (season) => {
      const path = join(dir, season)

      try {
        const metaSeason = JSON.parse(
          await fsData.readFile(path + "/meta.json", "utf8")
        ) as MetaSeason

        const chaps: [
          string,
          MetaChap & {
            status: Awaited<ReturnType<typeof getStatusDownload>>
          }
        ] = await Promise.all(
          (
            await fsData.readdir(path)
          ).map(async (chap) => {
            const p = path + "/" + chap

            try {
              return [
                chap,
                {
                  ...(JSON.parse(
                    await fsData.readFile(p + "/meta.json", "utf8")
                  ) as MetaChap),
                  status: await getStatusDownload({
                    season,
                    chap,
                  }),
                },
              ]
            } catch (err) {
              // eslint-disable-next-line @typescript-eslint/no-explicit-any
              if ((err as unknown as any)?.code === "ENOENT") {
                return undefined
              }
            }
          })
        ).then((res) => res.filter(Boolean) as Exclude<typeof res[0], void>[])

        if (chaps.length === 0) return undefined

        return [
          season,
          { ...metaSeason, chaps: new Map<string, typeof chaps[0][1]>(chaps) },
        ]
      } catch (err) {
        console.error(err)
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        if ((err as unknown as any)?.code === "ENOENT") return undefined
        // eslint-disable-next-line functional/no-throw-statement
        throw err
      }
    })
  ).then((res) => res.filter(Boolean) as Exclude<typeof res[0], void>[])


  return new Map<string, typeof animes[0][1]>(animes)
}
