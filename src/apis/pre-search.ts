import { getPathName } from "./utils/getPathName"
import { parserDOM } from "./utils/parserDOM"

interface DataItem {
  poster: string
  path: string
  name: string
  status: string
}
/**
  * const form = new FormData()
  form.append("ajaxSearch", "1")
  form.append("keysearch", query.value)
  loading.value = true
  const html = await fetch(
    props.domain
      ? `${props.domain}/ajax/suggest`
      : `https://cors-any.vercel.app/api/ajax/${encodeURIComponent(
          "https://animevietsub.tv/ajax/suggest"
        )}`,
    {
      method: "POST",
      body: form
    }
  ).then((e) => e.text())

  */
export async function preSearch(key: string) {
  const dom = parserDOM(`<li><a style="background-image: url('http://cdn.animevietsub.cc/data/poster/2022/02/24/animevsub-vziyRxKpU1.jpg')" class="thumb" href="http://animevietsub.cc/phim/tonikaku-kawaii-2nd-season-a4476/"></a>
  <div class="ss-info">
  <a href="http://animevietsub.cc/phim/tonikaku-kawaii-2nd-season-a4476/" class="ss-title">Tonikaku Kawaii 2nd Season</a>
  
  <p>Preview VietSub</p>
  </div>
  <div class="clearfix"></div>
  </li><li><a style="background-image: url('http://cdn.animevietsub.cc/data/poster/2021/08/18/animevsub-HXKAyrZo5i.jpg')" class="thumb" href="http://animevietsub.cc/phim/tonikaku-kawaii-sns-a4229/"></a>
  <div class="ss-info">
  <a href="http://animevietsub.cc/phim/tonikaku-kawaii-sns-a4229/" class="ss-title">Tonikaku Kawaii: SNS</a>
  
  <p>HD-VietSub</p>
  </div>
  <div class="clearfix"></div>
  </li><li><a style="background-image: url('http://cdn.animevietsub.cc/data/poster/2020/09/30/animevsub-1NmQPMC9x0.jpg')" class="thumb" href="http://animevietsub.cc/phim/tonikaku-kawaii-a3860/"></a>
  <div class="ss-info">
  <a href="http://animevietsub.cc/phim/tonikaku-kawaii-a3860/" class="ss-title">Tonikaku Kawaii [BD]</a>
  
  <p>Full VietSub</p>
  </div>
  <div class="clearfix"></div>
  </li><li class="ss-bottom" style="padding: 0; border-bottom: none;"><a href="/tim-kiem/toni/" id="suggest-all">Enter để tìm kiếm</a></li>`)

  return Array.from(dom.querySelectorAll("li:not(.ss-bottom)")).map(
    (li): DataItem => {
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      const anchor = li.querySelector("a")!
      const poster = anchor.style.backgroundImage
        .replace(/^url\((?:'|")?/, "")
        .replace(/(?:'|")?\)$/, "")
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      const path = getPathName(anchor.getAttribute("href")!)
      const name = li.querySelector(".ss-title")?.textContent ?? "unknown"
      const status = li.querySelector("p")?.textContent ?? "unknown"
      return { poster, path, name, status }
    }
  )
}
