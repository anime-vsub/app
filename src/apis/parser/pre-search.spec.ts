import { describe, expect, test } from "vitest"

import PreSearch from "./pre-search"

describe("pre-search", () => {
  test("normal", async () => {
    expect(
      await PreSearch(`<li><a style="background-image: url('http://cdn.animevietsub.cc/data/poster/2022/02/24/animevsub-vziyRxKpU1.jpg')" class="thumb" href="http://animevietsub.cc/phim/tonikaku-kawaii-2nd-season-a4476/"></a>
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
    ).toEqual([
      {
        image:
          "http://cdn.animevietsub.cc/data/poster/2022/02/24/animevsub-vziyRxKpU1.jpg",
        path: "/phim/tonikaku-kawaii-2nd-season-a4476/",
        name: "Tonikaku Kawaii 2nd Season",
        status: "Preview VietSub"
      },
      {
        image:
          "http://cdn.animevietsub.cc/data/poster/2021/08/18/animevsub-HXKAyrZo5i.jpg",
        path: "/phim/tonikaku-kawaii-sns-a4229/",
        name: "Tonikaku Kawaii: SNS",
        status: "HD-VietSub"
      },
      {
        image:
          "http://cdn.animevietsub.cc/data/poster/2020/09/30/animevsub-1NmQPMC9x0.jpg",
        path: "/phim/tonikaku-kawaii-a3860/",
        name: "Tonikaku Kawaii [BD]",
        status: "Full VietSub"
      }
    ])
  })
})
