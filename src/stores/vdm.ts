import { delMany, get, set } from "idb-keyval"
import { defineStore } from "pinia"
import { Dialog, Notify } from "quasar"
import { PlayerLink } from "src/apis/runs/ajax/player-link"
import type { PhimId } from "src/apis/runs/phim/[id]"
import type { PhimIdChap } from "src/apis/runs/phim/[id]/[chap]"
import { i18n } from "src/boot/i18n"
import { downloadToMp4 } from "src/logic/download-to-mp4"
import { hasVideoOffline as has, initStore } from "src/logic/has-video-offline"
import {
  registerBeforeUnload,
  unRegisterBeforeUnload
} from "src/logic/registry-before-unload"
// import { convertHlsToMP4 } from "src/logic/convert-hls-to-mp4"

export interface VideoOfflineMeta {
  readonly size: number
  readonly saved_at: string
}
export interface SeasonOffline {
  readonly dat: Readonly<Awaited<ReturnType<typeof PhimId>>>
  readonly off: Readonly<Record<string, VideoOfflineMeta>>
}
async function questionQualityDownload(
  list: Awaited<ReturnType<typeof PlayerLink>>["link"]
) {
  return new Promise<string>((resolve, reject) => {
    Dialog.create({
      title: "Chất lượng tải xuống",
      message:
        "Chất lượng càng cao thì chiếm dụng bộ nhớ càng lớn và thời gian tải càng lâu",
      options: {
        type: "radio",
        model: list[0].file,
        // inline: true
        items: list.map((item) => {
          return {
            label: item.label,
            value: item.file,
            color: "secondary",
            keepColor: true,
            checkedIcon: "task_alt",
            uncheckedIcon: "panorama_fish_eye"
          }
        })
      },
      cancel: {
        label: i18n.global.t("huy"),
        noCaps: true,
        color: "grey",
        text: true,
        flat: true,
        rounded: true
      },
      ok: { color: "green", text: true, flat: true, rounded: true }
    })
      .onOk((data) => {
        resolve(data)
      })
      .onCancel(() => reject(new Error("Cancel by user")))
      .onDismiss(() => reject(new Error("Cancel by user")))
  })
}

async function getMetaChaps(realSeasonId: string) {
  return get(`${PREFIX_CHAPS}${realSeasonId}`, initStore()).then((data) =>
    data
      ? (JSON.parse(data) as Awaited<ReturnType<typeof PhimIdChap>>)
      : Promise.reject(new Error("not_found"))
  )
}

async function getMetaSeason(realSeasonId: string) {
  return get(`${PREFIX_SEASON}${realSeasonId}`, initStore()).then((data) =>
    data
      ? (JSON.parse(data) as SeasonOffline)
      : Promise.reject(new Error("not_found"))
  )
}

const progressStore = shallowReactive(
  new Map<
    string,
    | readonly [
        current: number,
        total: number,
        tCurrent: number,
        tDuration: number,
        speed: number
      ]
    | Error
  >()
)
async function download(
  realSeasonId: string,
  season: Awaited<ReturnType<typeof PhimId>>,
  chaps: Awaited<ReturnType<typeof PhimIdChap>>,
  currentChapId: string
) {
  // get episode
  const currentChap = chaps.chaps.find((chap) => chap.id === currentChapId)
  if (!currentChap) throw new Error(`Chap ${currentChapId} not found`)

  if (await has(realSeasonId, currentChapId))
    throw new Error("Video đã tải xuống trước đó")

  const player = await PlayerLink(currentChap)

  const hlsUrl =
    (player.link.length > 1
      ? await questionQualityDownload(player.link)
      : player.link[0].file) + "#animevsub-vsub_extra"

  Notify.create({
    message: "Đang tải xuống...",
    caption: "Giữ cửa sổ này mở để tiếp tục",
    position: "bottom-left"
  })
  registerBeforeUnload()

  progressStore.set(`${currentChapId}@${realSeasonId}`, [0, 0, 0, 0, 0])

  try {
    await downloadToMp4(
      hlsUrl,
      `${PREFIX_VIDEO}${currentChapId}@${realSeasonId}`,
      realSeasonId,
      season,
      chaps,
      currentChapId,
      // eslint-disable-next-line functional/functional-parameters
      (...args) => {
        progressStore.set(`${currentChapId}@${realSeasonId}`, args)
      }
    )
  } catch (err) {
    progressStore.set(`${currentChapId}@${realSeasonId}`, err as Error)
    throw err
  } finally {
    unRegisterBeforeUnload()
    removeProgress(realSeasonId, currentChapId)
  }
}

async function remove(realSeasonId: string, currentChapId: string) {
  const season = await getMetaSeason(realSeasonId)
  const off = Object.keys(season.off)

  removeProgress(realSeasonId, currentChapId)

  if (off.length === 1 ? off[0] === currentChapId : off.length === 0) {
    // delete all
    await delMany(
      [
        `${PREFIX_VIDEO}${currentChapId}@${realSeasonId}`,
        `${PREFIX_VIDEO}${currentChapId}@${realSeasonId}_m`,
        `${PREFIX_SEASON}${realSeasonId}`,
        `${PREFIX_CHAPS}${realSeasonId}`,
        `${PREFIX_POSTER}${realSeasonId}`,
        `${PREFIX_IMAGE}${realSeasonId}`
      ],
      initStore()
    )

    const oldRegistry = (await get(`${REGISTRY_OFFLINE}`, initStore())
      .then((data) => (data ? JSON.parse(data) : null))
      .catch(() => null)) as Record<string, VideoOfflineMeta> | null

    if (oldRegistry) {
      delete oldRegistry[realSeasonId]
      await set(`${REGISTRY_OFFLINE}`, JSON.stringify(oldRegistry), initStore())
    }
  } else {
    await delMany(
      [
        `${PREFIX_VIDEO}${currentChapId}@${realSeasonId}`,
        `${PREFIX_VIDEO}${currentChapId}@${realSeasonId}_m`
      ],
      initStore()
    )
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    delete (season.off as unknown as any)[currentChapId]
    await set(
      `${PREFIX_SEASON}${realSeasonId}`,
      JSON.stringify(season),
      initStore()
    )
  }
}

function getProgress(realSeasonId: string, currentChapId: string) {
  return progressStore.get(`${currentChapId}@${realSeasonId}`)
}

function removeProgress(realSeasonId: string, currentChapId: string) {
  return progressStore.delete(`${currentChapId}@${realSeasonId}`)
}

function confirmRemove(realSeasonId: string, currentChapId: string) {
  return new Promise<void>((resolve, reject) => {
    Dialog.create({
      title: "Bạn muốn xóa video chứ?",
      message: "Sau khi xóa bạn không thể xem tập này ngoại tuyến nữa",
      persistent: true,
      cancel: {
        label: i18n.global.t("huy"),
        noCaps: true,
        color: "grey",
        text: true,
        flat: true,
        rounded: true
      },
      ok: { color: "green", text: true, flat: true, rounded: true }
    })
      .onOk(async () => {
        try {
          await remove(realSeasonId, currentChapId)

          Notify.create({
            message: "Đã xoá video",
            caption: "Bạn không thể xem tập này ngoại tuyến nữa",
            position: "bottom-left"
          })

          resolve()
        } catch (err) {
          reject(err)
          Notify.create({
            message: "Xóa video thất bại",
            caption: err + "",
            position: "bottom-left"
          })
        }
      })
      .onCancel(resolve)
  })
}

export const useVDMStore = defineStore("vdm", () => {
  // const queue = []
  // const dlStore = new Map<string, {

  // }>()

  return {
    has,
    download,
    remove,
    getMetaChaps,
    getMetaSeason,
    getProgress,
    removeProgress,
    confirmRemove
  }
})
