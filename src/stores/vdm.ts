import { defineStore } from "pinia"
import { Dialog, Notify } from "quasar"
import { PlayerLink } from "src/apis/runs/ajax/player-link"
import type { PhimId } from "src/apis/runs/phim/[id]"
import type { PhimIdChap } from "src/apis/runs/phim/[id]/[chap]"
import { i18n } from "src/boot/i18n"
import { downloadToMp4 } from "src/logic/download-to-mp4"
import {
  registerBeforeUnload,
  unRegisterBeforeUnload
} from "src/logic/registry-before-unload"
// import { convertHlsToMP4 } from "src/logic/convert-hls-to-mp4"

async function questionQualityDownload(
  list: Awaited<ReturnType<typeof PlayerLink>>["link"]
) {
  return new Promise<string>((resolve, reject) => {
    Dialog.create({
      title: i18n.global.t("chat-luong-tai-xuong"),
      message: i18n.global.t("msg-ques-quality"),
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
      .onCancel(() => reject(new Error(i18n.global.t("cancel-by-user"))))
      .onDismiss(() => reject(new Error(i18n.global.t("cancel-by-user"))))
  })
}

// async function questionSaveToFile() {
//   return new Promise<boolean>((resolve, reject) => {
//     Dialog.create({
//       title: "Lưu vào",
//       message: "Anime sẽ lưu vào đâu",
//       options: {
//         type: "radio",
//         model: "device",
//         // inline: true
//         items: [
//           {
//             label: "Thiết bị - Bạn có thể mở hoặc chia sẻ chúng như tệp",
//             value: "device",
//             color: "secondary",
//             keepColor: true,
//             checkedIcon: "task_alt",
//             uncheckedIcon: "panorama_fish_eye"
//           },
//           {
//             label:
//               "Ứng dụng - Bạn có mở AnimeVsub ngay cả khi không có internet",
//             value: "app",
//             color: "secondary",
//             keepColor: true,
//             checkedIcon: "task_alt",
//             uncheckedIcon: "panorama_fish_eye"
//           }
//         ]
//       },
//       cancel: {
//         label: i18n.global.i18n.global.t("huy"),
//         noCaps: true,
//         color: "grey",
//         text: true,
//         flat: true,
//         rounded: true
//       },
//       ok: { color: "green", text: true, flat: true, rounded: true }
//     })
//       .onOk((data) => {
//         resolve(data === "device")
//       })
//       .onCancel(() => reject(new Error("Cancel by user")))
//       .onDismiss(() => reject(new Error("Cancel by user")))
//   })
// }

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
  if (!currentChap) throw new Error(i18n.global.t("chap-not-found", [currentChapId]))

  const player = await PlayerLink(currentChap)

  const hlsUrl =
    (player.link.length > 1
      ? await questionQualityDownload(player.link)
      : player.link[0].file) + "#animevsub-vsub_extra"

  // const saveToFile = await questionSaveToFile()

  Notify.create({
    message: i18n.global.t("dlg"),
    caption: i18n.global.t("msg-keep-tab"),
    position: "bottom-left"
  })
  registerBeforeUnload()

  progressStore.set(`${currentChapId}@${realSeasonId}`, [0, 0, 0, 0, 0])

  try {
    await downloadToMp4(
      hlsUrl,
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

function getProgress(realSeasonId: string, currentChapId: string) {
  return progressStore.get(`${currentChapId}@${realSeasonId}`)
}

function removeProgress(realSeasonId: string, currentChapId: string) {
  return progressStore.delete(`${currentChapId}@${realSeasonId}`)
}

export const useVDMStore = defineStore("vdm", () => {
  // const queue = []
  // const dlStore = new Map<string, {

  // }>()

  return {
    download,
    getProgress,
    removeProgress
  }
})
