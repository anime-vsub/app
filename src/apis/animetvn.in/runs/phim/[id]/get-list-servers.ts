import type { PropsGetListServers } from "src/apis/animetvn.in/parsers/phim/[id]/[chap]"
import type { PhimIdChapReturns } from "src/apis/types/phim/[id]/[chap]"
import type { GetListServersReturns } from "src/apis/types/phim/[id]/get-list-servers"
import { post } from "src/logic/http"

export interface PropsServer {
  readonly link: string
  readonly id: string
  readonly csrf: string
}

export default async function (
  ep: PhimIdChapReturns<PropsGetListServers>["chaps"][0]
): Promise<GetListServersReturns<PropsServer>> {
  const { data } = await post(
    "https://animetvn.in/ajax/getExtraLinks",
    {
      epid: ep.epId,
    },
    {
      "x-csrf-token": ep.props.csrf,
      "x-requested-with": "XMLHttpRequest",
    }
  )

  const { links } = JSON.parse(data)

  const servers = links.map(
    (item: { name: string; id: string; link: string }) => {
      return {
        name: item.name,
        props: {
          id: item.id,
          link: item.link,
          csrf: ep.props.csrf
        },
      }
    }
  )

  return { servers }
}
