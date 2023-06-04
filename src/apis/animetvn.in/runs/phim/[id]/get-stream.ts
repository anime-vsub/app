import type { GetStreamReturns } from "src/apis/types/phim/[id]/get-stream"
import { post } from "src/logic/http"

import type { PropsServer } from "./get-list-servers"

export default async function getStream(
  props: PropsServer
): Promise<GetStreamReturns> {
  const data = JSON.parse(
    await post("https://animetvn.in/ajax/getExtraLinks", { ...props }).then(
      (res) => res.data
    )
  ) as {
    link: string
  }

  return {
    link: [
      {
        file: data.link,
        label: "HD",
        qualityCode: "720p",
        type: "m3u8",
      },
    ],
    playTech: "api",
  }
}
