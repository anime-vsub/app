import { Index } from "./index"

addEventListener("message", async () => {
  postMessage({
    data: await Index(),
  })
})
