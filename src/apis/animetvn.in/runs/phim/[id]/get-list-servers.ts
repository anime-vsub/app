import type { GetListServersReturns } from "src/apis/types/phim/[id]/get-list-servers"
import { post } from "src/logic/http"

export interface PropsServer {
  readonly link: string
  readonly id: string
}

export default async function (
  epId: string
): Promise<GetListServersReturns<PropsServer>> {
  fetch("https://animetvn.in/ajax/getExtraLinks", {})

  const { data } = await post("https://animetvn.in/ajax/getExtraLinks", {
    epid: epId,
  })

  const { links } = JSON.parse(data)

  const servers = links.map(
    (item: { name: string; id: string; link: string }) => {
      return {
        name: item.name,
        props: {
          id: item.id,
          link: item.link,
        },
      }
    }
  )

  return { servers }
}
