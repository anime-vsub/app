import type AjaxPlayerFBParser from "src/apis/parser/ajax/player-fb"
import Worker from "src/apis/workers/ajax/player-fb?worker"
import { PostWorker } from "src/apis/wrap-worker"
import { post } from "src/logic/http"

import { PlayerLink } from "./player-link"

export async function PlayerFB(episodeId: string) {
  const { data: json } = await post("/ajax/player?v=2019a", {
    episodeId,
    backup: 1
  })

  const data = JSON.parse(json)

  if (!data.success) throw new Error("Failed load player facebook")

  const config = await PostWorker<typeof AjaxPlayerFBParser>(Worker, data.html)

  return await PlayerLink(config)
}
