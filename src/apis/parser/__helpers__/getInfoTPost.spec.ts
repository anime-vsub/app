import { load } from "cheerio"
import { describe, expect, test } from "vitest"

import { getInfoTPost } from "./getInfoTPost"

describe("getInfoTPost", () => {
  test("simple", () => {
    const $ = load(`<div class="TPost B">
    <a href="http://animevietsub.cc/phim/call-of-the-night-a4536/" title="Yofukashi no Uta (2022)">
    <div class="Image">
    <figure class="Objf TpMvPlay AAIco-play_arrow"><img width="180" height="260" src="http://cdn.animevietsub.cc/data/poster/2022/06/03/animevsub-FOWOKDAhpl.jpg" class="attachment-img-mov-md size-img-mov-md wp-post-image" alt="Yofukashi no Uta (2022)"></figure>
    <span class="mli-eps">TẬP<i>11</i></span>
    <div class="anime-extras"><div class="anime-avg-user-rating" title="9.4 trong số 10 dựa trên 480 thành viên đánh giá" data-action="click->anime-card#showLibraryEditor"><i class="fa fa-star"></i>9.4</div></div>
    </div>
    <div class="Title">Yofukashi no Uta</div>
    </a>
    </div>`)
    $("div")
    expect(
      JSON.parse(JSON.stringify(getInfoTPost($("div"), Date.now())))
    ).toEqual({
      path: "/phim/call-of-the-night-a4536/",
      image:
        "http://cdn.animevietsub.cc/data/poster/2022/06/03/animevsub-FOWOKDAhpl.jpg",
      name: "Yofukashi no Uta",
      chap: "11",
      rate: 9.4,
      genre: [],
      description: "",
      process: "",
      quality: "",
      year: null
    })
  })
  test("full", () => {
    const $ =
      load(`<article id="post-3396" class="TPost C post-3396 post type-post status-publish format-standard has-post-thumbnail hentry">
    <a href="http://animevietsub.cc/phim/linh-kiem-ton-a3396/">
    <div class="Image">
    <figure class="Objf TpMvPlay AAIco-play_arrow"><img width="215" height="320" src="http://cdn.animevietsub.cc/data/poster/2019/01/15/animevsub-oDJnM8xrdd.jpg" class="attachment-thumbnail size-thumbnail wp-post-image" alt="Linh Kiếm Tôn - Ling Jian Zun, Spirit Sword Sovereign (2019)" title="Linh Kiếm Tôn (2019)"></figure>
    <span class="mli-eps">TẬP<i>315</i></span>
    <div class="anime-extras"><div class="anime-avg-user-rating" title="7.4 trong số 10 dựa trên 131 thành viên đánh giá" data-action="click->anime-card#showLibraryEditor"><i class="fa fa-star"></i>7.4</div></div>
    </div>
    <h2 class="Title">Linh Kiếm Tôn</h2> <span class="Year">Lượt xem: 4,066,095</span> </a>
    <div class="TPMvCn anmt">
    <div class="Title">Linh Kiếm Tôn</div>
    <p class="Info"><span class="Qlty">HD</span> <span class="Vote AAIco-star">7.4</span> <span class="Time AAIco-access_time">315/???</span> <span class="Date AAIco-date_range">2019</span></p>
    <div class="Description">
    <p>Cường giả bị tập kích, phản lão hoàn đồng, trở thành thiếu chủ phế vật. Kẻ thù từ đời trước kiếp này,...</p>
    <p class="Studio AAIco-videocam"><span>Studio:</span> Soyep <i class="Button STPa AAIco-more_horiz"></i></p>
    <p class="Genre AAIco-movie_creation"><span>Thể loại:</span> <a href="http://animevietsub.cc/the-loai/phep-thuat/" title="Fantasy">Fantasy</a>, <a href="http://animevietsub.cc/the-loai/hanh-dong/" title="Action">Action</a>, <i class="Button STPa AAIco-more_horiz"></i></p>
    <p class="Actors AAIco-person"><span>Diễn viên:</span> </p><div class="alert alert-warning">Nhân vật đang được cập nhật</div> <i class="Button STPa AAIco-more_horiz"></i><p></p>
    </div>
    </div>
    </article>`)
    expect(
      JSON.parse(JSON.stringify(getInfoTPost($("article"), Date.now())))
    ).toEqual({
      path: "/phim/linh-kiem-ton-a3396/",
      image:
        "http://cdn.animevietsub.cc/data/poster/2019/01/15/animevsub-oDJnM8xrdd.jpg",
      name: "Linh Kiếm Tôn",
      chap: "315",
      rate: 7.4,
      views: 4066095,
      quality: "HD",
      process: "315/???",
      year: 2019,
      description:
        "Cường giả bị tập kích, phản lão hoàn đồng, trở thành thiếu chủ phế vật. Kẻ thù từ đời trước kiếp này,...",
      studio: "Soyep",
      genre: [
        { path: "/the-loai/phep-thuat/", name: "Fantasy" },
        { path: "/the-loai/hanh-dong/", name: "Action" }
      ]
    })
  })
  test("countdown", () => {
    const $ =
      load(`<article id="post-4418" class="TPost C post-4418 post type-post status-publish format-standard has-post-thumbnail hentry">
    <a href="http://animevietsub.cc/phim/detective-conan-movie-25-halloween-no-hanayome-a4418/">
    <div class="Image">
    <figure class="Objf TpMvPlay AAIco-play_arrow"><img width="215" height="320" src="http://cdn.animevietsub.cc/data/poster/2022/02/14/animevsub-hvhxNxwXwf.jpg" class="attachment-thumbnail size-thumbnail wp-post-image" alt="Thám Tử Lừng Danh Conan: Nàng dâu Halloween - Detective Conan Movie 25: Halloween no Hanayome, Meitantei Conan: Halloween no Hanayome (2022)" title="Thám Tử Lừng Danh Conan: Nàng dâu Halloween (2022)"></figure>
    <span class="mli-timeschedule" data-timer_second="4601735">53d 6h 1m 2s</span><span class="b">2022-11-09</span>
    <div class="anime-extras"><div class="anime-avg-user-rating" title="9.6 trong số 10 dựa trên 75 thành viên đánh giá" data-action="click->anime-card#showLibraryEditor"><i class="fa fa-star"></i>9.6</div></div>
    </div>
    <h2 class="Title">Thám Tử Lừng Danh Conan: Nàng dâu Halloween</h2> <span class="Year">Lượt xem: 1,130,021</span> </a>
    <div class="TPMvCn anmt">
    <div class="Title">Thám Tử Lừng Danh Conan: Nàng dâu Halloween</div>
    <p class="Info"><span class="Qlty">HD</span> <span class="Vote AAIco-star">9.6</span> <span class="Time AAIco-access_time">110 Phút</span> <span class="Date AAIco-date_range">2022</span></p>
    <div class="Description">
    <p>Bối cảnh lần này diễn ra tại Shibuya, Tokyo đang nhộn nhịp mùa Halloween.Một hôn lễ được tổ chức ở Shibuya Hikarie....</p>
    <p class="Studio AAIco-videocam"><span>Studio:</span> TMS Entertainment <i class="Button STPa AAIco-more_horiz"></i></p>
    <p class="Genre AAIco-movie_creation"><span>Thể loại:</span> <a href="http://animevietsub.cc/the-loai/shounen/" title="Shounen">Shounen</a>, <a href="http://animevietsub.cc/the-loai/tinh-cam/" title="Romance">Romance</a>, <a href="http://animevietsub.cc/the-loai/police/" title="Police">Police</a>, <a href="http://animevietsub.cc/the-loai/drama/" title="Drama">Drama</a>, <a href="http://animevietsub.cc/the-loai/hai-huoc/" title="Comedy">Comedy</a>, <a href="http://animevietsub.cc/the-loai/mystery/" title="Mystery">Mystery</a>, <a href="http://animevietsub.cc/the-loai/hanh-dong/" title="Action">Action</a>, <i class="Button STPa AAIco-more_horiz"></i></p>
    <p class="Actors AAIco-person"><span>Diễn viên:</span> </p><div class="alert alert-warning">Nhân vật đang được cập nhật</div> <i class="Button STPa AAIco-more_horiz"></i><p></p>
    </div>
    </div>
    </article>`)
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    let tmp: any
    expect(
      JSON.parse(JSON.stringify((tmp = getInfoTPost($("article"), Date.now()))))
    ).toEqual({
      path: "/phim/detective-conan-movie-25-halloween-no-hanayome-a4418/",
      image:
        "http://cdn.animevietsub.cc/data/poster/2022/02/14/animevsub-hvhxNxwXwf.jpg",
      name: "Thám Tử Lừng Danh Conan: Nàng dâu Halloween",
      rate: 9.6,
      views: 1130021,
      quality: "HD",
      chap: "",
      process: "110 Phút",
      year: 2022,
      description:
        "Bối cảnh lần này diễn ra tại Shibuya, Tokyo đang nhộn nhịp mùa Halloween.Một hôn lễ được tổ chức ở Shibuya Hikarie....",
      studio: "TMS Entertainment",
      genre: [
        { path: "/the-loai/shounen/", name: "Shounen" },
        { path: "/the-loai/tinh-cam/", name: "Romance" },
        { path: "/the-loai/police/", name: "Police" },
        { path: "/the-loai/drama/", name: "Drama" },
        { path: "/the-loai/hai-huoc/", name: "Comedy" },
        { path: "/the-loai/mystery/", name: "Mystery" },
        { path: "/the-loai/hanh-dong/", name: "Action" }
      ],
      time_release: tmp.time_release
    })
  })
  test("countdown not absolute", () => {
    const $ =
      load(`<article id="post-4418" class="TPost C post-4418 post type-post status-publish format-standard has-post-thumbnail hentry">
    <a href="http://animevietsub.cc/phim/detective-conan-movie-25-halloween-no-hanayome-a4418/">
    <div class="Image">
    <figure class="Objf TpMvPlay AAIco-play_arrow"><img width="215" height="320" src="http://cdn.animevietsub.cc/data/poster/2022/02/14/animevsub-hvhxNxwXwf.jpg" class="attachment-thumbnail size-thumbnail wp-post-image" alt="Thám Tử Lừng Danh Conan: Nàng dâu Halloween - Detective Conan Movie 25: Halloween no Hanayome, Meitantei Conan: Halloween no Hanayome (2022)" title="Thám Tử Lừng Danh Conan: Nàng dâu Halloween (2022)"></figure>
    <span class="mli-timeschedule" >53d 6h 1m 2s</span><span class="b">2022-11-09</span>
    <div class="anime-extras"><div class="anime-avg-user-rating" title="9.6 trong số 10 dựa trên 75 thành viên đánh giá" data-action="click->anime-card#showLibraryEditor"><i class="fa fa-star"></i>9.6</div></div>
    </div>
    <h2 class="Title">Thám Tử Lừng Danh Conan: Nàng dâu Halloween</h2> <span class="Year">Lượt xem: 1,130,021</span> </a>
    <div class="TPMvCn anmt">
    <div class="Title">Thám Tử Lừng Danh Conan: Nàng dâu Halloween</div>
    <p class="Info"><span class="Qlty">HD</span> <span class="Vote AAIco-star">9.6</span> <span class="Time AAIco-access_time">110 Phút</span> <span class="Date AAIco-date_range">2022</span></p>
    <div class="Description">
    <p>Bối cảnh lần này diễn ra tại Shibuya, Tokyo đang nhộn nhịp mùa Halloween.Một hôn lễ được tổ chức ở Shibuya Hikarie....</p>
    <p class="Studio AAIco-videocam"><span>Studio:</span> TMS Entertainment <i class="Button STPa AAIco-more_horiz"></i></p>
    <p class="Genre AAIco-movie_creation"><span>Thể loại:</span> <a href="http://animevietsub.cc/the-loai/shounen/" title="Shounen">Shounen</a>, <a href="http://animevietsub.cc/the-loai/tinh-cam/" title="Romance">Romance</a>, <a href="http://animevietsub.cc/the-loai/police/" title="Police">Police</a>, <a href="http://animevietsub.cc/the-loai/drama/" title="Drama">Drama</a>, <a href="http://animevietsub.cc/the-loai/hai-huoc/" title="Comedy">Comedy</a>, <a href="http://animevietsub.cc/the-loai/mystery/" title="Mystery">Mystery</a>, <a href="http://animevietsub.cc/the-loai/hanh-dong/" title="Action">Action</a>, <i class="Button STPa AAIco-more_horiz"></i></p>
    <p class="Actors AAIco-person"><span>Diễn viên:</span> </p><div class="alert alert-warning">Nhân vật đang được cập nhật</div> <i class="Button STPa AAIco-more_horiz"></i><p></p>
    </div>
    </div>
    </article>`)
    expect(
      JSON.parse(JSON.stringify(getInfoTPost($("article"), Date.now())))
    ).toEqual({
      path: "/phim/detective-conan-movie-25-halloween-no-hanayome-a4418/",
      image:
        "http://cdn.animevietsub.cc/data/poster/2022/02/14/animevsub-hvhxNxwXwf.jpg",
      name: "Thám Tử Lừng Danh Conan: Nàng dâu Halloween",
      rate: 9.6,
      views: 1130021,
      quality: "HD",
      chap: "",
      process: "110 Phút",
      year: 2022,
      description:
        "Bối cảnh lần này diễn ra tại Shibuya, Tokyo đang nhộn nhịp mùa Halloween.Một hôn lễ được tổ chức ở Shibuya Hikarie....",
      studio: "TMS Entertainment",
      genre: [
        { path: "/the-loai/shounen/", name: "Shounen" },
        { path: "/the-loai/tinh-cam/", name: "Romance" },
        { path: "/the-loai/police/", name: "Police" },
        { path: "/the-loai/drama/", name: "Drama" },
        { path: "/the-loai/hai-huoc/", name: "Comedy" },
        { path: "/the-loai/mystery/", name: "Mystery" },
        { path: "/the-loai/hanh-dong/", name: "Action" }
      ],
      time_release: null
    })
  })
})
