import type { MemoryInfo } from "@vueuse/core"
import { boot } from "quasar/wrappers"
import { NAME_GET_MEMORY } from "src/constants"
import { sleep } from "src/logic/sleep"
import { v4 } from "uuid"

interface MemoryInfoTab {
  jsHeapSizeLimit: number
  totalJSHeapSize: number
  usedJSHeapSize: number
}
interface MessageGetMemory {
  id: string
  type: "get"
}
export interface MessageReturnMemory {
  id: string
  type: "return"
  url: string
  title: string
  tabId: string
  memory: MemoryInfoTab
  canPlay: boolean
  poster: string
  playing: boolean
}
interface MessagePlayer {
  id: string
  type: "player"
  tabId: string
  play: boolean
}
interface MessagePauseAll {
  id: string
  type: "pause all"
}

const $memoryTabs = new Map<string, Exclude<MessageReturnMemory, "id">>()
export const memoryTabs = shallowReactive(
  new Map<string, Exclude<MessageReturnMemory, "id">>()
)

const broadcast = new BroadcastChannel(NAME_GET_MEMORY)
export { broadcast as broadcastGetMemory }

export async function getMemoryAllTabs() {
  broadcast.postMessage({ id: v4(), type: "get" })
  await sleep(700)

  // update $memoryTabs to memoryTabs
  memoryTabs.forEach(({ tabId }) => {
    if (!$memoryTabs.has(tabId)) memoryTabs.delete(tabId)
  })
  $memoryTabs.forEach((tab, id) => {
    memoryTabs.set(id, tab)
  })
  $memoryTabs.clear()
}

export default boot(() => {
  const tabId = v4()

  let inited = false
  function initControlBroadcast() {
    if (!inited) return
    inited = true

    broadcast.onmessage = ({
      data
    }: MessageEvent<
      MessageGetMemory | MessageReturnMemory | MessagePlayer | MessagePauseAll
    >) => {
      if (!data?.id) return

      if (data.type === "get") {
        const { jsHeapSizeLimit, totalJSHeapSize, usedJSHeapSize } = (
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          performance as unknown as any
        ).memory as MemoryInfo

        const video = document.querySelector("video")
        broadcast.postMessage(<MessageReturnMemory>{
          id: data.id,
          type: "return",
          url: location.toString(),
          title: document.title,
          tabId,
          memory: {
            jsHeapSizeLimit,
            totalJSHeapSize,
            usedJSHeapSize
          },
          canPlay: !!video,
          poster: video && video.getAttribute("poster"),
          playing: (video ? !video.paused : null) ?? false
        })

        return
      }

      if (data.type === "return") {
        if ($memoryTabs.has(data.tabId)) {
          // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
          Object.assign($memoryTabs.get(data.tabId)!, data)
        } else {
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          delete (data as unknown as any).id
          $memoryTabs.set(data.tabId, shallowReactive(data))
        }

        return
      }

      if (data.type === "player" && data.tabId === tabId) {
        if (data.play) document.querySelector("video")?.play()
        else document.querySelector("video")?.pause()

        return
      }

      if (data.type === "pause all") document.querySelector("video")?.pause()
    }
  }

  window.addEventListener("keydown", (event: KeyboardEvent) => {
    if (event.shiftKey && event.altKey && event.code === "KeyX") {
      event.preventDefault()

      void initControlBroadcast()

      const width = 650
      const height = 650

      const left = innerWidth / 2 - width / 2
      const top = innerHeight / 2 - height / 2

      window.open(
        "/task-manager",
        "Task Manager",
        "popup;menubar=no,toolbar=no,location=no,width=" +
          width +
          ",height=" +
          height +
          ",left=" +
          left +
          ",top=" +
          top
      )
    }
  })
})
