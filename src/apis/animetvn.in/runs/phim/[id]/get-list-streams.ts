import type { GetListServersReturns } from "src/apis/types/phim/[id]/get-list-servers"

import type { PropsServer } from "./get-list-servers"

export default async function (
  server: GetListServersReturns<PropsServer>["servers"][0]
) {
  const { data } = await post(
    "https://animetvn.in/ajax/getExtraLink",
    {
      id: server.props.id,
      link: server.props.link,
    },
    {
      "x-csrf-token": server.props.csrf,
      "x-requested-with": "XMLHttpRequest",
    }
  )

  const { link } = JSON.parse(data) as { link: string }

}
