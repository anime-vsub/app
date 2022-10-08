import { describe, expect, test } from "vitest"

import AjaxNotification from "./notification"

describe("notification", () => {
  test("normal", () => {
    expect(
      AjaxNotification(`<div class="notifi-item" id="notifi-item-5606932">
			<a href="/phim/kakkou-no-iinazuke-a4260/tap-24end-86965.html" class="not-read">
				<div class="notifi-item-thumbnail"><img src="http://cdn.animevietsub.cc/data/banner/2021/08/31/animevsub-4WKr3mgTnu.jpg"></div>
				<div class="notifi-item-info">
					<div class="notification-text"><strong>Hôn Thê Cúc Cu</strong> đã cập nhật <strong>Tập 24_END</strong></div>
					<div class="notification-time"><i class="fa fa-clock-o" aria-hidden="true"></i> 5 ngày trước</div>
				</div>
			</a>

			<i class="fa fa-times notification-delete" id="notification-delete" data-id="5606932"></i>

		</div><div class="notifi-item" id="notifi-item-5589667">
			<a href="/phim/summertime-render-a4504/tap-25end-86906.html" class="not-read">
				<div class="notifi-item-thumbnail"><img src="http://cdn.animevietsub.cc/data/banner/2022/04/15/animevsub-wo4DTBXCyd.png"></div>
				<div class="notifi-item-info">
					<div class="notification-text"><strong>Summertime Render</strong> đã cập nhật <strong>Tập 25_END</strong></div>
					<div class="notification-time"><i class="fa fa-clock-o" aria-hidden="true"></i> 6 ngày trước</div>
				</div>
			</a>

			<i class="fa fa-times notification-delete" id="notification-delete" data-id="5589667"></i>

		</div><div class="notifi-item" id="notifi-item-5581144">
			<a href="/phim/call-of-the-night-a4536/tap-13end-86895.html" class="not-read">
				<div class="notifi-item-thumbnail"><img src="http://cdn.animevietsub.cc/data/banner/2022/07/08/animevsub-gvZ0OWMdaI.jpg"></div>
				<div class="notifi-item-info">
					<div class="notification-text"><strong>Yofukashi no Uta</strong> đã cập nhật <strong>Tập 13_END</strong></div>
					<div class="notification-time"><i class="fa fa-clock-o" aria-hidden="true"></i> 1 tuần trước</div>
				</div>
			</a>

			<i class="fa fa-times notification-delete" id="notification-delete" data-id="5581144"></i>

		</div><div class="notifi-item" id="notifi-item-5577127">
			<a href="/phim/danmachi-iv-a4289/tap-11end-86893.html" class="not-read">
				<div class="notifi-item-thumbnail"><img src="http://cdn.animevietsub.cc/data/banner/2022/09/08/animevsub-MThn4jJ9eT.jpg"></div>
				<div class="notifi-item-info">
					<div class="notification-text"><strong>DanMachi IV</strong> đã cập nhật <strong>Tập 11_END</strong></div>
					<div class="notification-time"><i class="fa fa-clock-o" aria-hidden="true"></i> 1 tuần trước</div>
				</div>
			</a>

			<i class="fa fa-times notification-delete" id="notification-delete" data-id="5577127"></i>

		</div><div class="notifi-item" id="notifi-item-5573509">
			<a href="/phim/hataraku-maou-sama-2nd-season-a4257/tap-12end-86892.html" class="not-read">
				<div class="notifi-item-thumbnail"><img src="http://cdn.animevietsub.cc/data/banner/2022/06/03/animevsub-ixE7rWSfjK.jpg"></div>
				<div class="notifi-item-info">
					<div class="notification-text"><strong>Ma Vương Đi Làm Mùa 2</strong> đã cập nhật <strong>Tập 12_END</strong></div>
					<div class="notification-time"><i class="fa fa-clock-o" aria-hidden="true"></i> 1 tuần trước</div>
				</div>
			</a>

			<i class="fa fa-times notification-delete" id="notification-delete" data-id="5573509"></i>

		</div><div class="notifi-item" id="notifi-item-5559895">
			<a href="/phim/overlord-iv-a4263/tap-13end-86859.html" class="not-read">
				<div class="notifi-item-thumbnail"><img src="http://cdn.animevietsub.cc/data/banner/2022/06/16/animevsub-WTbLjxxodb.jpg"></div>
				<div class="notifi-item-info">
					<div class="notification-text"><strong>Overlord IV</strong> đã cập nhật <strong>Tập 13_END</strong></div>
					<div class="notification-time"><i class="fa fa-clock-o" aria-hidden="true"></i> 1 tuần trước</div>
				</div>
			</a>

			<i class="fa fa-times notification-delete" id="notification-delete" data-id="5559895"></i>

		</div>`)
    ).toEqual([
      {
        image:
          "http://cdn.animevietsub.cc/data/banner/2021/08/31/animevsub-4WKr3mgTnu.jpg",
        name: "Hôn Thê Cúc Cu",
        chap: "Tập 24_END",
        time: " 5 ngày trước",
        path: "/phim/kakkou-no-iinazuke-a4260/tap-24end-86965.html",
        id: "5606932",
      },
      {
        image:
          "http://cdn.animevietsub.cc/data/banner/2022/04/15/animevsub-wo4DTBXCyd.png",
        name: "Summertime Render",
        chap: "Tập 25_END",
        time: " 6 ngày trước",
        path: "/phim/summertime-render-a4504/tap-25end-86906.html",
        id: "5589667",
      },
      {
        image:
          "http://cdn.animevietsub.cc/data/banner/2022/07/08/animevsub-gvZ0OWMdaI.jpg",
        name: "Yofukashi no Uta",
        chap: "Tập 13_END",
        time: " 1 tuần trước",
        path: "/phim/call-of-the-night-a4536/tap-13end-86895.html",
        id: "5581144",
      },
      {
        image:
          "http://cdn.animevietsub.cc/data/banner/2022/09/08/animevsub-MThn4jJ9eT.jpg",
        name: "DanMachi IV",
        chap: "Tập 11_END",
        time: " 1 tuần trước",
        path: "/phim/danmachi-iv-a4289/tap-11end-86893.html",
        id: "5577127",
      },
      {
        image:
          "http://cdn.animevietsub.cc/data/banner/2022/06/03/animevsub-ixE7rWSfjK.jpg",
        name: "Ma Vương Đi Làm Mùa 2",
        chap: "Tập 12_END",
        time: " 1 tuần trước",
        path: "/phim/hataraku-maou-sama-2nd-season-a4257/tap-12end-86892.html",
        id: "5573509",
      },
      {
        image:
          "http://cdn.animevietsub.cc/data/banner/2022/06/16/animevsub-WTbLjxxodb.jpg",
        name: "Overlord IV",
        chap: "Tập 13_END",
        time: " 1 tuần trước",
        path: "/phim/overlord-iv-a4263/tap-13end-86859.html",
        id: "5559895",
      },
    ])
  })
})
