import FS from "@isomorphic-git/lightning-fs"
import { AnimeDownloadManager } from "animevsub-download-manager/src/main"
import { defineStore } from "pinia"

export const useADM = defineStore("adm", () => {
  const fs = new FS("adm").promises
  async function mkdirRecursive(path: string) {
    try {
      await fs.mkdir(path)
    } catch (err: any) {
      if (err?.code === "ENOENT")
        await mkdirRecursive(path.split("/").slice(0, -1).join("/"))
      else throw err
    }
  }

  const tasks = shallowReactive(new Map())

  const adm = markRaw(
    new AnimeDownloadManager(
      {
        readdir(path) {
          return fs.readdir(path)
        },
        readFile(path) {
          return fs.readFile(path, "utf8") as Promise<string>
        },
        readFiles(paths: string[]) {
          return Promise.all(
            paths.map((path) => fs.readFile(path, "utf8") as Promise<string>)
          )
        },
        hasFile(path: string) {
          return fs
            .lstat(path)
            .then((value) => value.isFile() && { size: value.size })
            .catch(() => false)
        },
        async writeFile(path: string, content: string | Uint8Array) {
          await fs.writeFile(path, content).catch((err) => {
            if (err.code === "ENOENT") {
              return mkdirRecursive(
                path.split("/").slice(0, -1).join("/")
                // eslint-disable-next-line promise/no-nesting
              ).then(() => this.writeFile(path, content))
            }

            throw err
          })
        },
        async writeFiles(
          contents: [string, string | Uint8Array][]
        ): Promise<void> {
          await Promise.all(
            contents.map(([path, content]) => this.writeFile(path, content))
          )
        },
        async unlinks(paths: string[]): Promise<void> {
          await Promise.all(paths.map((path) => fs.unlink(path)))
        }
      },
      {
        request(uri: string, method = "GET"): Promise<Response> {
          return fetch(uri + "#animevsub-vsub_extra", { method })
        },
        delay: 300,
        repeat: 5,
        concurrent: 5,
        onstart(seasonInfo, episode) {
          this.onprogress(seasonInfo, episode, 0, 0)
        },
        onprogress(seasonInfo, episode, current, total) {
          let inTask = tasks.get(seasonInfo.seasonId)

          if (!inTask) {
            tasks.set(
              seasonInfo.seasonId,
              (inTask = {
                seasonInfo,
                episodes: shallowReactive(new Map())
              })
            )
          }

          inTask.episodes.set(episode.id, { episode, current, total })
          console.log("downloaded segment %i on %i", current, total)
        }
      }
    )
  )

  return { tasks, adm, fs: markRaw(fs) }
})
