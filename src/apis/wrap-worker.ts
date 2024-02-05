/* eslint-disable @typescript-eslint/no-explicit-any */
import { v4 } from "uuid"
import type { ArgumentsType } from "vitest"

/**
 * be a responsible person! kill the worker after you get what you need
 **/

export function WrapWorker<Fn extends (...args: any) => any>(fn: Fn) {
  addEventListener(
    "message",
    (
      event: MessageEvent<{
        id: string
        args: unknown[]
      }>
    ) => {
      postMessage({
        id: event.data.id,
        result: fn(...event.data.args)
      })
    }
  )
}
export function PostWorker<Fn extends (...args: any) => any>(
  createWorker: new () => Worker,
  // eslint-disable-next-line functional/functional-parameters
  ...args: ArgumentsType<Fn>
) {
  return new Promise<ReturnType<Fn>>((resolve, reject) => {
    const id = v4()

    // eslint-disable-next-line new-cap
    const worker = new createWorker()
    worker.addEventListener("message", (event: MessageEvent) => {
      if (event.data.id === id) {
        worker.terminate()
        resolve(event.data.result)
      }
    })
    worker.removeEventListener("message", () => {
      worker.terminate()
      reject()
    })

    worker.postMessage({ id, args })
  })
}
