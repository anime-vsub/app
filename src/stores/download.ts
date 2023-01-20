/* eslint-disable @typescript-eslint/no-non-null-assertion */
import { defineStore } from "pinia"
import type { MetaChap, MetaSeason } from "src/logic/offline-anime"
import {
  deleteOfflineChapAnime,
  downloadInfoAnime,
  downloadOfflineAnime,
  getListDownload,
} from "src/logic/offline-anime"
import { computed, ref } from "vue"

export const useDownloadStore = defineStore("download", () => {
  const _lists = ref<Awaited<ReturnType<typeof getListDownload>>>()

  const syncListInDiskToRef = async () => {
    _lists.value = await getListDownload()
  }
  const lists = computed(() => {
    if (!_lists.value) syncListInDiskToRef()

    return _lists
  })

  const aborterStore = new Map<string, AbortController>()

  async function addChapToTask(options: {
    season: string
    seasonName: string
    info: MetaSeason
    chap: string
    chapName: string
    file: string
  }) {
    if (!_lists.value) await syncListInDiskToRef()

    const info = await downloadInfoAnime(options.info) // equal options.info
    // ok add this task to the list of tasks
    // eslint-disable-next-line functional/no-let
    let animeOnQueue = _lists.value!.get(options.season)
    if (!animeOnQueue) {
      // add to reactive
      // ああああああああ！わかない。ぜんぜん。
      _lists.value!.set(
        options.season,
        {
          ...info,
          chaps: new Map(),
        } as unknown as MetaSeason & {
          chaps: Map<
            string,
            MetaChap & {
              status:
                | {
                    loaded: number
                    total: number
                  }
                | undefined
            }
          >
        } /* === options.info */
      )
      animeOnQueue = _lists.value?.get(options.season)
    } else {
      // exists bypass . mov 102
    }

    const created = new Date().getUTCMilliseconds()
    // eslint-disable-next-line functional/no-let
    let sulfur = animeOnQueue!.chaps.get(options.chap)
    if (!sulfur) {
      animeOnQueue!.chaps.set(options.chap, {
        chapName: options.chapName,
        created,
        status: {
          loaded: 0,
          total: Infinity,
        },
      })
      sulfur = animeOnQueue!.chaps.get(options.chap) // reactive
    }

    const id = `${options.season}/${options.chap}`
    const aborter = new AbortController()
    aborterStore.set(id, aborter)

    await downloadOfflineAnime({
      ...options,
      created,
      onprogress(evt) {
        sulfur!.status = evt
      },
      signal: aborter.signal,
    })

    sulfur!.status = undefined // download done
    aborterStore.delete(id)
  }

  async function deleteChapFromTask(options: { season: string; chap: string }) {
    await deleteOfflineChapAnime(options)
    _lists.value?.get(options.season)?.chaps?.delete(options.chap)
  }

  async function pauseChapFromTask(options: { season: string; chap: string }) {
    aborterStore.get(`${options.season}/${options.chap}`)?.abort()
  }

  return { lists , addChapToTask, deleteChapFromTask, pauseChapFromTask }
})
