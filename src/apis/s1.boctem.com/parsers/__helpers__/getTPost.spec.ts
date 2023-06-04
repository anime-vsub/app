import { parserDom } from "src/apis/parser/__helpers__/parserDom"

import { getTPost } from "./getTPost"

describe("getTPost", () => {
  test("should post from isekai", async () => {
    const $ =
      parserDom(`<div id="halim-carousel-widget-3" class="owl-carousel owl-theme owl-loaded owl-drag">
      <div class="owl-stage-outer"><div class="owl-stage" style="transform: translate3d(-724px, 0px, 0px); transition: all 0s ease 0s; width: 2896px;"><div class="owl-item cloned" style="width: 177px; margin-right: 4px;"><article class="thumb grid-item post-15921">
    <div class="halim-item">
        <a class="halim-thumb" href="https://s1.boctem.com/isekai-de-cheat-skill-wo-te-ni-shita-ore-wa-genjitsu-sekai-wo-mo-musou-suru-level-up-wa-jinsei-wo-kaeta/" title="Sau khi có được năng lực bá đạo ở dị giới, tôi cũng vô đối ở thế giới thực ~Thăng cấp xong thì cuộc đời cũng thay đổi~">
            <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/03/Isekai-de-Cheat-Skill-wo-Te-ni-Shita-Ore-wa-Genjitsu-Sekai-wo-mo-Musou-Suru-Level-Up-wa-Jinsei-wo-Kaeta.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/03/Isekai-de-Cheat-Skill-wo-Te-ni-Shita-Ore-wa-Genjitsu-Sekai-wo-mo-Musou-Suru-Level-Up-wa-Jinsei-wo-Kaeta.jpg" alt="Sau khi có được năng lực bá đạo ở dị giới, tôi cũng vô đối ở thế giới thực ~Thăng cấp xong thì cuộc đời cũng thay đổi~" title="Sau khi có được năng lực bá đạo ở dị giới, tôi cũng vô đối ở thế giới thực ~Thăng cấp xong thì cuộc đời cũng thay đổi~" data-was-processed="true"></figure>
            <span class="status">Tập 9-9TM</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
            <div class="halim-post-title-box">
                <div class="halim-post-title ">
                    <h2 class="entry-title">Sau khi có được năng lực bá đạo ở dị giới, tôi cũng vô đối ở thế giới thực ~Thăng cấp xong thì cuộc đời cũng thay đổi~</h2><p class="original_title">Isekai de Cheat Skill wo Te ni Shita Ore wa, Genjitsu Sekai wo mo Musou Suru: Level Up wa Jinsei wo Kaeta</p>
                </div>
            </div>
        </a>
    </div>
</article></div><div class="owl-item cloned" style="width: 177px; margin-right: 4px;"><article class="thumb grid-item post-15900">
    <div class="halim-item">
        <a class="halim-thumb" href="https://s1.boctem.com/isekai-wa-smartphone-to-tomo-ni-ss2/" title="Đến thế giới mới với smartphone! SS2">
            <figure><img class="lazy img-responsive" src="data:image/svg+xml,%3Csvg%20xmlns='http://www.w3.org/2000/svg'%20viewBox='0%200%20225%20318'%3E%3C/svg%3E" data-src="https://s1.boctem.com/wp-content/uploads/2023/02/Isekai-wa-Smartphone-to-Tomo-ni..jpg" alt="Đến thế giới mới với smartphone! SS2" title="Đến thế giới mới với smartphone! SS2"></figure>
            <span class="status">Tập 9</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
            <div class="halim-post-title-box">
                <div class="halim-post-title ">
                    <h2 class="entry-title">Đến thế giới mới với smartphone! SS2</h2><p class="original_title">Isekai wa Smartphone to Tomo ni. SS2</p>
                </div>
            </div>
        </a>
    </div>
</article></div><div class="owl-item cloned" style="width: 177px; margin-right: 4px;"><article class="thumb grid-item post-15915">
    <div class="halim-item">
        <a class="halim-thumb" href="https://s1.boctem.com/kuma-kuma-kuma-bear-ss2/" title="Kuma Kuma Kuma Bear SS2">
            <figure><img class="lazy img-responsive" src="data:image/svg+xml,%3Csvg%20xmlns='http://www.w3.org/2000/svg'%20viewBox='0%200%20225%20305'%3E%3C/svg%3E" data-src="https://s1.boctem.com/wp-content/uploads/2023/03/Kuma-Kuma-Kuma-Bear.jpg" alt="Kuma Kuma Kuma Bear SS2" title="Kuma Kuma Kuma Bear SS2"></figure>
            <span class="status">Tập 9</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
            <div class="halim-post-title-box">
                <div class="halim-post-title ">
                    <h2 class="entry-title">Kuma Kuma Kuma Bear SS2</h2><p class="original_title">Kuma Kuma Kuma Bear Punch!</p>
                </div>
            </div>
        </a>
    </div>
</article></div><div class="owl-item cloned" style="width: 177px; margin-right: 4px;"><article class="thumb grid-item post-15969">
    <div class="halim-item">
        <a class="halim-thumb" href="https://s1.boctem.com/kanojo-ga-koushaku-tei-ni-itta-riyuu/" title="Vị Hôn Thê Khế Ước Của Công Tước">
            <figure><img class="lazy img-responsive" src="data:image/svg+xml,%3Csvg%20xmlns='http://www.w3.org/2000/svg'%20viewBox='0%200%201%201'%3E%3C/svg%3E" data-src="https://s1.boctem.com/wp-content/uploads/2023/03/Kanojo-ga-Koushaku-tei-ni-Itta-Riyuu.jpg" alt="Vị Hôn Thê Khế Ước Của Công Tước" title="Vị Hôn Thê Khế Ước Của Công Tước"></figure>
            <span class="status">Tập 8</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
            <div class="halim-post-title-box">
                <div class="halim-post-title ">
                    <h2 class="entry-title">Vị Hôn Thê Khế Ước Của Công Tước</h2><p class="original_title">Kanojo ga Koushaku-tei ni Itta Riyuu</p>
                </div>
            </div>
        </a>
    </div>
</article></div><div class="owl-item active" style="width: 177px; margin-right: 4px;"><article class="thumb grid-item post-16419">
    <div class="halim-item">
        <a class="halim-thumb" href="https://s1.boctem.com/that-nghiep-chuyen-sinh-lam-lai-cuoc-doi-ss3/" title="Thất Nghiệp Chuyển Sinh: Làm Lại Cuộc Đời SS3">
            <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/05/That-Nghiep-Chuyen-Sinh-Lam-Lai-Cuoc-Doi-SS3.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/05/That-Nghiep-Chuyen-Sinh-Lam-Lai-Cuoc-Doi-SS3.jpg" alt="Thất Nghiệp Chuyển Sinh: Làm Lại Cuộc Đời SS3" title="Thất Nghiệp Chuyển Sinh: Làm Lại Cuộc Đời SS3" data-was-processed="true"></figure>
            <span class="status">03/07/23</span><span class="is_trailer"><i class="hl-play"></i> Trailer</span>                    <div class="icon_overlay"></div>
            <div class="halim-post-title-box">
                <div class="halim-post-title ">
                    <h2 class="entry-title">Thất Nghiệp Chuyển Sinh: Làm Lại Cuộc Đời SS3</h2><p class="original_title">Mushoku Tensei: Jobless Reincarnation SS3</p>
                </div>
            </div>
        </a>
    </div>
</article></div><div class="owl-item active" style="width: 177px; margin-right: 4px;"><article class="thumb grid-item post-16388">
    <div class="halim-item">
        <a class="halim-thumb" href="https://s1.boctem.com/di-toc-trung-sinh/" title="Dị Tộc Trùng Sinh">
            <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/05/di-toc-trung-sinh.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/05/di-toc-trung-sinh.jpg" alt="Dị Tộc Trùng Sinh" title="Dị Tộc Trùng Sinh" data-was-processed="true"></figure>
            <span class="status">Tập 3</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
            <div class="halim-post-title-box">
                <div class="halim-post-title title-2-line">
                    <h2 class="entry-title">Dị Tộc Trùng Sinh</h2>
                </div>
            </div>
        </a>
    </div>
</article></div><div class="owl-item active" style="width: 177px; margin-right: 4px;"><article class="thumb grid-item post-16033">
    <div class="halim-item">
        <a class="halim-thumb" href="https://s1.boctem.com/kaminaki-sekai-no-kamisama-katsudou/" title="Là Thần Trong Thế Giới Vô Thần">
            <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/03/Kaminaki-Sekai-no-Kamisama-Katsudou.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/03/Kaminaki-Sekai-no-Kamisama-Katsudou.jpg" alt="Là Thần Trong Thế Giới Vô Thần" title="Là Thần Trong Thế Giới Vô Thần" data-was-processed="true"></figure>
            <span class="status">Tập 8</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
            <div class="halim-post-title-box">
                <div class="halim-post-title ">
                    <h2 class="entry-title">Là Thần Trong Thế Giới Vô Thần</h2><p class="original_title">Kaminaki Sekai no Kamisama Katsudou</p>
                </div>
            </div>
        </a>
    </div>
</article></div><div class="owl-item active" style="width: 177px; margin-right: 4px;"><article class="thumb grid-item post-15984">
    <div class="halim-item">
        <a class="halim-thumb" href="https://s1.boctem.com/dead-mount-death-play/" title="Dead Mount Death Play">
            <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/03/Dead-Mount-Death-Play.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/03/Dead-Mount-Death-Play.jpg" alt="Dead Mount Death Play" title="Dead Mount Death Play" data-was-processed="true"></figure>
            <span class="status">Tập 8</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
            <div class="halim-post-title-box">
                <div class="halim-post-title title-2-line">
                    <h2 class="entry-title">Dead Mount Death Play</h2>
                </div>
            </div>
        </a>
    </div>
</article></div><div class="owl-item" style="width: 177px; margin-right: 4px;"><article class="thumb grid-item post-15921">
    <div class="halim-item">
        <a class="halim-thumb" href="https://s1.boctem.com/isekai-de-cheat-skill-wo-te-ni-shita-ore-wa-genjitsu-sekai-wo-mo-musou-suru-level-up-wa-jinsei-wo-kaeta/" title="Sau khi có được năng lực bá đạo ở dị giới, tôi cũng vô đối ở thế giới thực ~Thăng cấp xong thì cuộc đời cũng thay đổi~">
            <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/03/Isekai-de-Cheat-Skill-wo-Te-ni-Shita-Ore-wa-Genjitsu-Sekai-wo-mo-Musou-Suru-Level-Up-wa-Jinsei-wo-Kaeta.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/03/Isekai-de-Cheat-Skill-wo-Te-ni-Shita-Ore-wa-Genjitsu-Sekai-wo-mo-Musou-Suru-Level-Up-wa-Jinsei-wo-Kaeta.jpg" alt="Sau khi có được năng lực bá đạo ở dị giới, tôi cũng vô đối ở thế giới thực ~Thăng cấp xong thì cuộc đời cũng thay đổi~" title="Sau khi có được năng lực bá đạo ở dị giới, tôi cũng vô đối ở thế giới thực ~Thăng cấp xong thì cuộc đời cũng thay đổi~" data-was-processed="true"></figure>
            <span class="status">Tập 9-9TM</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
            <div class="halim-post-title-box">
                <div class="halim-post-title ">
                    <h2 class="entry-title">Sau khi có được năng lực bá đạo ở dị giới, tôi cũng vô đối ở thế giới thực ~Thăng cấp xong thì cuộc đời cũng thay đổi~</h2><p class="original_title">Isekai de Cheat Skill wo Te ni Shita Ore wa, Genjitsu Sekai wo mo Musou Suru: Level Up wa Jinsei wo Kaeta</p>
                </div>
            </div>
        </a>
    </div>
</article></div><div class="owl-item" style="width: 177px; margin-right: 4px;"><article class="thumb grid-item post-15900">
    <div class="halim-item">
        <a class="halim-thumb" href="https://s1.boctem.com/isekai-wa-smartphone-to-tomo-ni-ss2/" title="Đến thế giới mới với smartphone! SS2">
            <figure><img class="lazy img-responsive" src="data:image/svg+xml,%3Csvg%20xmlns='http://www.w3.org/2000/svg'%20viewBox='0%200%20225%20318'%3E%3C/svg%3E" data-src="https://s1.boctem.com/wp-content/uploads/2023/02/Isekai-wa-Smartphone-to-Tomo-ni..jpg" alt="Đến thế giới mới với smartphone! SS2" title="Đến thế giới mới với smartphone! SS2"></figure>
            <span class="status">Tập 9</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
            <div class="halim-post-title-box">
                <div class="halim-post-title ">
                    <h2 class="entry-title">Đến thế giới mới với smartphone! SS2</h2><p class="original_title">Isekai wa Smartphone to Tomo ni. SS2</p>
                </div>
            </div>
        </a>
    </div>
</article></div><div class="owl-item" style="width: 177px; margin-right: 4px;"><article class="thumb grid-item post-15915">
    <div class="halim-item">
        <a class="halim-thumb" href="https://s1.boctem.com/kuma-kuma-kuma-bear-ss2/" title="Kuma Kuma Kuma Bear SS2">
            <figure><img class="lazy img-responsive" src="data:image/svg+xml,%3Csvg%20xmlns='http://www.w3.org/2000/svg'%20viewBox='0%200%20225%20305'%3E%3C/svg%3E" data-src="https://s1.boctem.com/wp-content/uploads/2023/03/Kuma-Kuma-Kuma-Bear.jpg" alt="Kuma Kuma Kuma Bear SS2" title="Kuma Kuma Kuma Bear SS2"></figure>
            <span class="status">Tập 9</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
            <div class="halim-post-title-box">
                <div class="halim-post-title ">
                    <h2 class="entry-title">Kuma Kuma Kuma Bear SS2</h2><p class="original_title">Kuma Kuma Kuma Bear Punch!</p>
                </div>
            </div>
        </a>
    </div>
</article></div><div class="owl-item" style="width: 177px; margin-right: 4px;"><article class="thumb grid-item post-15969">
    <div class="halim-item">
        <a class="halim-thumb" href="https://s1.boctem.com/kanojo-ga-koushaku-tei-ni-itta-riyuu/" title="Vị Hôn Thê Khế Ước Của Công Tước">
            <figure><img class="lazy img-responsive" src="data:image/svg+xml,%3Csvg%20xmlns='http://www.w3.org/2000/svg'%20viewBox='0%200%201%201'%3E%3C/svg%3E" data-src="https://s1.boctem.com/wp-content/uploads/2023/03/Kanojo-ga-Koushaku-tei-ni-Itta-Riyuu.jpg" alt="Vị Hôn Thê Khế Ước Của Công Tước" title="Vị Hôn Thê Khế Ước Của Công Tước"></figure>
            <span class="status">Tập 8</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
            <div class="halim-post-title-box">
                <div class="halim-post-title ">
                    <h2 class="entry-title">Vị Hôn Thê Khế Ước Của Công Tước</h2><p class="original_title">Kanojo ga Koushaku-tei ni Itta Riyuu</p>
                </div>
            </div>
        </a>
    </div>
</article></div><div class="owl-item cloned" style="width: 177px; margin-right: 4px;"><article class="thumb grid-item post-16419">
    <div class="halim-item">
        <a class="halim-thumb" href="https://s1.boctem.com/that-nghiep-chuyen-sinh-lam-lai-cuoc-doi-ss3/" title="Thất Nghiệp Chuyển Sinh: Làm Lại Cuộc Đời SS3">
            <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/05/That-Nghiep-Chuyen-Sinh-Lam-Lai-Cuoc-Doi-SS3.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/05/That-Nghiep-Chuyen-Sinh-Lam-Lai-Cuoc-Doi-SS3.jpg" alt="Thất Nghiệp Chuyển Sinh: Làm Lại Cuộc Đời SS3" title="Thất Nghiệp Chuyển Sinh: Làm Lại Cuộc Đời SS3" data-was-processed="true"></figure>
            <span class="status">03/07/23</span><span class="is_trailer"><i class="hl-play"></i> Trailer</span>                    <div class="icon_overlay"></div>
            <div class="halim-post-title-box">
                <div class="halim-post-title ">
                    <h2 class="entry-title">Thất Nghiệp Chuyển Sinh: Làm Lại Cuộc Đời SS3</h2><p class="original_title">Mushoku Tensei: Jobless Reincarnation SS3</p>
                </div>
            </div>
        </a>
    </div>
</article></div><div class="owl-item cloned" style="width: 177px; margin-right: 4px;"><article class="thumb grid-item post-16388">
    <div class="halim-item">
        <a class="halim-thumb" href="https://s1.boctem.com/di-toc-trung-sinh/" title="Dị Tộc Trùng Sinh">
            <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/05/di-toc-trung-sinh.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/05/di-toc-trung-sinh.jpg" alt="Dị Tộc Trùng Sinh" title="Dị Tộc Trùng Sinh" data-was-processed="true"></figure>
            <span class="status">Tập 3</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
            <div class="halim-post-title-box">
                <div class="halim-post-title title-2-line">
                    <h2 class="entry-title">Dị Tộc Trùng Sinh</h2>
                </div>
            </div>
        </a>
    </div>
</article></div><div class="owl-item cloned" style="width: 177px; margin-right: 4px;"><article class="thumb grid-item post-16033">
    <div class="halim-item">
        <a class="halim-thumb" href="https://s1.boctem.com/kaminaki-sekai-no-kamisama-katsudou/" title="Là Thần Trong Thế Giới Vô Thần">
            <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/03/Kaminaki-Sekai-no-Kamisama-Katsudou.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/03/Kaminaki-Sekai-no-Kamisama-Katsudou.jpg" alt="Là Thần Trong Thế Giới Vô Thần" title="Là Thần Trong Thế Giới Vô Thần" data-was-processed="true"></figure>
            <span class="status">Tập 8</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
            <div class="halim-post-title-box">
                <div class="halim-post-title ">
                    <h2 class="entry-title">Là Thần Trong Thế Giới Vô Thần</h2><p class="original_title">Kaminaki Sekai no Kamisama Katsudou</p>
                </div>
            </div>
        </a>
    </div>
</article></div><div class="owl-item cloned" style="width: 177px; margin-right: 4px;"><article class="thumb grid-item post-15984">
    <div class="halim-item">
        <a class="halim-thumb" href="https://s1.boctem.com/dead-mount-death-play/" title="Dead Mount Death Play">
            <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/03/Dead-Mount-Death-Play.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/03/Dead-Mount-Death-Play.jpg" alt="Dead Mount Death Play" title="Dead Mount Death Play" data-was-processed="true"></figure>
            <span class="status">Tập 8</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
            <div class="halim-post-title-box">
                <div class="halim-post-title title-2-line">
                    <h2 class="entry-title">Dead Mount Death Play</h2>
                </div>
            </div>
        </a>
    </div>
</article></div></div></div><div class="owl-nav disabled"><div class="owl-prev"><i class="hl-down-open rotate-left"></i></div><div class="owl-next"><i class="hl-down-open rotate-right"></i></div></div><div class="owl-dots"><div class="owl-dot active"><span></span></div><div class="owl-dot"><span></span></div></div></div>`)

    const result = $("#halim-carousel-widget-3 .owl-item")
      .toArray()
      .map((item) => getTPost($(item)))

    expect(result).toEqual([
      {
        path: "/isekai-de-cheat-skill-wo-te-ni-shita-ore-wa-genjitsu-sekai-wo-mo-musou-suru-level-up-wa-jinsei-wo-kaeta/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/03/Isekai-de-Cheat-Skill-wo-Te-ni-Shita-Ore-wa-Genjitsu-Sekai-wo-mo-Musou-Suru-Level-Up-wa-Jinsei-wo-Kaeta.jpg",
        name: "Sau khi có được năng lực bá đạo ở dị giới, tôi cũng vô đối ở thế giới thực ~Thăng cấp xong thì cuộc đời cũng thay đổi~",
        originName:
          "Isekai de Cheat Skill wo Te ni Shita Ore wa, Genjitsu Sekai wo mo Musou Suru: Level Up wa Jinsei wo Kaeta",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/isekai-wa-smartphone-to-tomo-ni-ss2/",
        image:
          "data:image/svg+xml,%3Csvg%20xmlns='http://www.w3.org/2000/svg'%20viewBox='0%200%20225%20318'%3E%3C/svg%3E",
        name: "Đến thế giới mới với smartphone! SS2",
        originName: "Isekai wa Smartphone to Tomo ni. SS2",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/kuma-kuma-kuma-bear-ss2/",
        image:
          "data:image/svg+xml,%3Csvg%20xmlns='http://www.w3.org/2000/svg'%20viewBox='0%200%20225%20305'%3E%3C/svg%3E",
        name: "Kuma Kuma Kuma Bear SS2",
        originName: "Kuma Kuma Kuma Bear Punch!",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/kanojo-ga-koushaku-tei-ni-itta-riyuu/",
        image:
          "data:image/svg+xml,%3Csvg%20xmlns='http://www.w3.org/2000/svg'%20viewBox='0%200%201%201'%3E%3C/svg%3E",
        name: "Vị Hôn Thê Khế Ước Của Công Tước",
        originName: "Kanojo ga Koushaku-tei ni Itta Riyuu",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/that-nghiep-chuyen-sinh-lam-lai-cuoc-doi-ss3/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/05/That-Nghiep-Chuyen-Sinh-Lam-Lai-Cuoc-Doi-SS3.jpg",
        name: "Thất Nghiệp Chuyển Sinh: Làm Lại Cuộc Đời SS3",
        originName: "Mushoku Tensei: Jobless Reincarnation SS3",
        timeRelease: "Sun Jul 02 2023 17:00:00 GMT+0000 (GMT)",
        views: undefined,
        isTrailer: true,
      },
      {
        path: "/di-toc-trung-sinh/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/05/di-toc-trung-sinh.jpg",
        name: "Dị Tộc Trùng Sinh",
        originName: "",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/kaminaki-sekai-no-kamisama-katsudou/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/03/Kaminaki-Sekai-no-Kamisama-Katsudou.jpg",
        name: "Là Thần Trong Thế Giới Vô Thần",
        originName: "Kaminaki Sekai no Kamisama Katsudou",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/dead-mount-death-play/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/03/Dead-Mount-Death-Play.jpg",
        name: "Dead Mount Death Play",
        originName: "",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/isekai-de-cheat-skill-wo-te-ni-shita-ore-wa-genjitsu-sekai-wo-mo-musou-suru-level-up-wa-jinsei-wo-kaeta/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/03/Isekai-de-Cheat-Skill-wo-Te-ni-Shita-Ore-wa-Genjitsu-Sekai-wo-mo-Musou-Suru-Level-Up-wa-Jinsei-wo-Kaeta.jpg",
        name: "Sau khi có được năng lực bá đạo ở dị giới, tôi cũng vô đối ở thế giới thực ~Thăng cấp xong thì cuộc đời cũng thay đổi~",
        originName:
          "Isekai de Cheat Skill wo Te ni Shita Ore wa, Genjitsu Sekai wo mo Musou Suru: Level Up wa Jinsei wo Kaeta",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/isekai-wa-smartphone-to-tomo-ni-ss2/",
        image:
          "data:image/svg+xml,%3Csvg%20xmlns='http://www.w3.org/2000/svg'%20viewBox='0%200%20225%20318'%3E%3C/svg%3E",
        name: "Đến thế giới mới với smartphone! SS2",
        originName: "Isekai wa Smartphone to Tomo ni. SS2",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/kuma-kuma-kuma-bear-ss2/",
        image:
          "data:image/svg+xml,%3Csvg%20xmlns='http://www.w3.org/2000/svg'%20viewBox='0%200%20225%20305'%3E%3C/svg%3E",
        name: "Kuma Kuma Kuma Bear SS2",
        originName: "Kuma Kuma Kuma Bear Punch!",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/kanojo-ga-koushaku-tei-ni-itta-riyuu/",
        image:
          "data:image/svg+xml,%3Csvg%20xmlns='http://www.w3.org/2000/svg'%20viewBox='0%200%201%201'%3E%3C/svg%3E",
        name: "Vị Hôn Thê Khế Ước Của Công Tước",
        originName: "Kanojo ga Koushaku-tei ni Itta Riyuu",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/that-nghiep-chuyen-sinh-lam-lai-cuoc-doi-ss3/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/05/That-Nghiep-Chuyen-Sinh-Lam-Lai-Cuoc-Doi-SS3.jpg",
        name: "Thất Nghiệp Chuyển Sinh: Làm Lại Cuộc Đời SS3",
        originName: "Mushoku Tensei: Jobless Reincarnation SS3",
        timeRelease: "Sun Jul 02 2023 17:00:00 GMT+0000 (GMT)",
        views: undefined,
        isTrailer: true,
      },
      {
        path: "/di-toc-trung-sinh/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/05/di-toc-trung-sinh.jpg",
        name: "Dị Tộc Trùng Sinh",
        originName: "",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/kaminaki-sekai-no-kamisama-katsudou/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/03/Kaminaki-Sekai-no-Kamisama-Katsudou.jpg",
        name: "Là Thần Trong Thế Giới Vô Thần",
        originName: "Kaminaki Sekai no Kamisama Katsudou",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/dead-mount-death-play/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/03/Dead-Mount-Death-Play.jpg",
        name: "Dead Mount Death Play",
        originName: "",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
    ])
  })

  test("should anime last update", async () => {
    const $ =
      parserDom(`<div id="halim-advanced-widget-2-ajax-box" class="halim_box" style="position: relative; height: 1722px;">
    <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-15894" style="position: absolute; left: 0px; top: 0px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/ousama-ranking-ss2/" title="Câu Chuyện Về Hoàng Tử Vô Dụng SS2">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/02/Ousama-Ranking-Yuuki-no-Takarabako.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/02/Ousama-Ranking-Yuuki-no-Takarabako.jpg" alt="Câu Chuyện Về Hoàng Tử Vô Dụng SS2" title="Câu Chuyện Về Hoàng Tử Vô Dụng SS2" data-was-processed="true"></figure>
          <span class="status">Tập 8</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title ">
                  <h2 class="entry-title">Câu Chuyện Về Hoàng Tử Vô Dụng SS2</h2><p class="original_title">Ousama Ranking SS2</p>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-15990" style="position: absolute; left: 181px; top: 0px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/yuusha-ga-shinda/" title="Dũng sĩ đã chết!">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/03/Yuusha-ga-Shinda.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/03/Yuusha-ga-Shinda.jpg" alt="Dũng sĩ đã chết!" title="Dũng sĩ đã chết!" data-was-processed="true"></figure>
          <span class="status">Tập 9</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title ">
                  <h2 class="entry-title">Dũng sĩ đã chết!</h2><p class="original_title">Yuusha ga Shinda!</p>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-15873" style="position: absolute; left: 362px; top: 0px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/dr-stone-ss3/" title="Dr. Stone SS3">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/02/Dr.-Stone.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/02/Dr.-Stone.jpg" alt="Dr. Stone SS3" title="Dr. Stone SS3" data-was-processed="true"></figure>
          <span class="status">Tập 9-8TM</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title ">
                  <h2 class="entry-title">Dr. Stone SS3</h2><p class="original_title">Tiến Sĩ Đá SS3</p>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-15888" style="position: absolute; left: 543px; top: 0px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/mahoutsukai-no-yome-ss2/" title="Cô Dâu Pháp Sư SS2">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/02/Mahoutsukai-no-Yome-SS2.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/02/Mahoutsukai-no-Yome-SS2.jpg" alt="Cô Dâu Pháp Sư SS2" title="Cô Dâu Pháp Sư SS2" data-was-processed="true"></figure>
          <span class="status">Tập 9</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title ">
                  <h2 class="entry-title">Cô Dâu Pháp Sư SS2</h2><p class="original_title">Mahoutsukai no Yome SS2</p>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-6940" style="position: absolute; left: 0px; top: 246px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/kore-wa-zombie-desu-ka/" title="Kore wa Zombie Desu ka">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2021/03/75521.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2021/03/75521.jpg" alt="Kore wa Zombie Desu ka" title="Kore wa Zombie Desu ka" data-was-processed="true"></figure>
          <span class="status">Tập 12/12</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title ">
                  <h2 class="entry-title">Kore wa Zombie Desu ka</h2><p class="original_title">Is This a Zombie?</p>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-16310" style="position: absolute; left: 181px; top: 246px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/o-re-id1/" title="Ở Rể">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/04/O-Re-250x350.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/04/O-Re-250x350.jpg" alt="Ở Rể" title="Ở Rể" data-was-processed="true"></figure>
          <span class="status">Tập 7-3TM</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title ">
                  <h2 class="entry-title">Ở Rể</h2><p class="original_title">My Heroic Husband</p>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-16351" style="position: absolute; left: 362px; top: 246px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/gia-thien/" title="Già Thiên">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/05/Gia-Thien-1.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/05/Gia-Thien-1.jpg" alt="Già Thiên" title="Già Thiên" data-was-processed="true"></figure>
          <span class="status">Tập 7-7TM</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title ">
                  <h2 class="entry-title">Già Thiên</h2><p class="original_title">Zhe Tian, Shrouding The Heavens</p>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-16083" style="position: absolute; left: 543px; top: 246px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/kizuna-no-allele/" title="Kizuna no Allele">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/04/Kizuna-no-Allele.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/04/Kizuna-no-Allele.jpg" alt="Kizuna no Allele" title="Kizuna no Allele" data-was-processed="true"></figure>
          <span class="status">Tập 9</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title title-2-line">
                  <h2 class="entry-title">Kizuna no Allele</h2>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-3711" style="position: absolute; left: 0px; top: 492px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/kyou-no-asuka-show/" title="Kyou no Asuka Show">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2021/02/77066.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2021/02/77066.jpg" alt="Kyou no Asuka Show" title="Kyou no Asuka Show" data-was-processed="true"></figure>
          <span class="status">Tập 20/20</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title ">
                  <h2 class="entry-title">Kyou no Asuka Show</h2><p class="original_title">Kyou no Asuka Show</p>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-16435" style="position: absolute; left: 181px; top: 492px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/tieu-nhan/" title="Tiêu Nhân">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/06/Tieu-Nhan.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/06/Tieu-Nhan.jpg" alt="Tiêu Nhân" title="Tiêu Nhân" data-was-processed="true"></figure>
          <span class="status">Tập 2</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title ">
                  <h2 class="entry-title">Tiêu Nhân</h2><p class="original_title">Biao Ren: Blades of the Guardians</p>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-4040" style="position: absolute; left: 362px; top: 492px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/white-album-2/" title="White Album 2">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2021/02/53561.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2021/02/53561.jpg" alt="White Album 2" title="White Album 2" data-was-processed="true"></figure>
          <span class="status">Tập 13/13</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title ">
                  <h2 class="entry-title">White Album 2</h2><p class="original_title">White Album 2</p>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-3568" style="position: absolute; left: 543px; top: 492px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/hoshizora-e-kakaru-hashi/" title="Hoshizora e Kakaru Hashi">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2021/01/73521.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2021/01/73521.jpg" alt="Hoshizora e Kakaru Hashi" title="Hoshizora e Kakaru Hashi" data-was-processed="true"></figure>
          <span class="status">Tập 12/12</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title ">
                  <h2 class="entry-title">Hoshizora e Kakaru Hashi</h2><p class="original_title">A Bridge to the Starry Skies</p>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-4761" style="position: absolute; left: 0px; top: 738px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/barakamon/" title="Barakamon">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2021/02/65427.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2021/02/65427.jpg" alt="Barakamon" title="Barakamon" data-was-processed="true"></figure>
          <span class="status">Tập 12/12</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title ">
                  <h2 class="entry-title">Barakamon</h2><p class="original_title">Barakamon</p>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-16408" style="position: absolute; left: 181px; top: 738px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/tuan-san-vo-lam-bat-nhi/" title="Tuần San Võ Lâm Bất Nhị">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/05/Tuan-San-Vo-Lam-Bat-Nhi.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/05/Tuan-San-Vo-Lam-Bat-Nhi.jpg" alt="Tuần San Võ Lâm Bất Nhị" title="Tuần San Võ Lâm Bất Nhị" data-was-processed="true"></figure>
          <span class="status">Tập 14</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title ">
                  <h2 class="entry-title">Tuần San Võ Lâm Bất Nhị</h2><p class="original_title">Wu Lin Bu Er Zhou Kan</p>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-16156" style="position: absolute; left: 362px; top: 738px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/son-hai-te-hoi/" title="Sơn Hải Tế Hội">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/04/son-hai-te-hoi_250x350.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/04/son-hai-te-hoi_250x350.jpg" alt="Sơn Hải Tế Hội" title="Sơn Hải Tế Hội" data-was-processed="true"></figure>
          <span class="status">Tập 9</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title title-2-line">
                  <h2 class="entry-title">Sơn Hải Tế Hội</h2>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-15842" style="position: absolute; left: 543px; top: 738px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/khoi-dau-co-kiem-vuc-ta-se-tro-thanh-kiem-than/" title="Khởi Đầu Có Kiếm Vực, Ta Sẽ Trở Thành Kiếm Thần">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/02/khoi-dau-co-kiem-vuc-ta-se-tro-thanh-kiem-than_250x350.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/02/khoi-dau-co-kiem-vuc-ta-se-tro-thanh-kiem-than_250x350.jpg" alt="Khởi Đầu Có Kiếm Vực, Ta Sẽ Trở Thành Kiếm Thần" title="Khởi Đầu Có Kiếm Vực, Ta Sẽ Trở Thành Kiếm Thần" data-was-processed="true"></figure>
          <span class="status">Tập 23</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title title-2-line">
                  <h2 class="entry-title">Khởi Đầu Có Kiếm Vực, Ta Sẽ Trở Thành Kiếm Thần</h2>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-2687" style="position: absolute; left: 0px; top: 984px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/van-gioi-tien-tung/" title="Vạn Giới Tiên Tung">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2021/01/download-32.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2021/01/download-32.jpg" alt="Vạn Giới Tiên Tung" title="Vạn Giới Tiên Tung" data-was-processed="true"></figure>
          <span class="status">Tập 380</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title title-2-line">
                  <h2 class="entry-title">Vạn Giới Tiên Tung</h2>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-14935" style="position: absolute; left: 181px; top: 984px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/van-co-than-thoai/" title="Vạn Cổ Thần Thoại">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/01/van-co-than-thoai-14935-250x350.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/01/van-co-than-thoai-14935-250x350.jpg" alt="Vạn Cổ Thần Thoại" title="Vạn Cổ Thần Thoại" data-was-processed="true"></figure>
          <span class="status">Tập 87</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title title-2-line">
                  <h2 class="entry-title">Vạn Cổ Thần Thoại</h2>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-16307" style="position: absolute; left: 362px; top: 984px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/thanh-lien-kiem-tien-truyen/" title="Thanh Liên Kiếm Tiên Truyện">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/04/Thanh-Lien-Kiem-Tien-Truyen-250x350.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/04/Thanh-Lien-Kiem-Tien-Truyen-250x350.jpg" alt="Thanh Liên Kiếm Tiên Truyện" title="Thanh Liên Kiếm Tiên Truyện" data-was-processed="true"></figure>
          <span class="status">Tập 15</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title ">
                  <h2 class="entry-title">Thanh Liên Kiếm Tiên Truyện</h2><p class="original_title">Legend Of Lotus Sword Fairy</p>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-14546" style="position: absolute; left: 543px; top: 984px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/than-an-vuong-toa/" title="Thần Ấn Vương Tọa">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2022/07/Throne-Of-Seal.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2022/07/Throne-Of-Seal.jpg" alt="Thần Ấn Vương Tọa" title="Thần Ấn Vương Tọa" data-was-processed="true"></figure>
          <span class="status">Tập 57</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title ">
                  <h2 class="entry-title">Thần Ấn Vương Tọa</h2><p class="original_title">Throne of Seal</p>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-14880" style="position: absolute; left: 0px; top: 1230px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/nguyen-ton/" title="Nguyên Tôn">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2022/08/nguyen-ton-phan-1-2.png" data-src="https://s1.boctem.com/wp-content/uploads/2022/08/nguyen-ton-phan-1-2.png" alt="Nguyên Tôn" title="Nguyên Tôn" data-was-processed="true"></figure>
          <span class="status">Tập 47</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title title-2-line">
                  <h2 class="entry-title">Nguyên Tôn</h2>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-15805" style="position: absolute; left: 181px; top: 1230px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/kiem-vuc-phong-van-phan-2/" title="Kiếm Vực Phong Vân Phần 2">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/01/kiem-vuc-phong-van-phan-2-15805.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/01/kiem-vuc-phong-van-phan-2-15805.jpg" alt="Kiếm Vực Phong Vân Phần 2" title="Kiếm Vực Phong Vân Phần 2" data-was-processed="true"></figure>
          <span class="status">Tập 41</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title title-2-line">
                  <h2 class="entry-title">Kiếm Vực Phong Vân Phần 2</h2>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-2683" style="position: absolute; left: 362px; top: 1230px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/doc-bo-tieu-dao/" title="Độc Bộ Tiêu Dao">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/01/doc-bo-tieu-dao-2683.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/01/doc-bo-tieu-dao-2683.jpg" alt="Độc Bộ Tiêu Dao" title="Độc Bộ Tiêu Dao" data-was-processed="true"></figure>
          <span class="status">Tập 317</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title title-2-line">
                  <h2 class="entry-title">Độc Bộ Tiêu Dao</h2>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-16223" style="position: absolute; left: 543px; top: 1230px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/bat-diet-than-vuong/" title="Bất Diệt Thần Vương">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/04/Bat-Diet-Than-Vuong.png" data-src="https://s1.boctem.com/wp-content/uploads/2023/04/Bat-Diet-Than-Vuong.png" alt="Bất Diệt Thần Vương" title="Bất Diệt Thần Vương" data-was-processed="true"></figure>
          <span class="status">Tập 8</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title title-2-line">
                  <h2 class="entry-title">Bất Diệt Thần Vương</h2>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-5096" style="position: absolute; left: 0px; top: 1476px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/dao-kiem-than-vuc-sword-art-offline/" title="Đao Kiếm Thần Vực">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2021/02/43461.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2021/02/43461.jpg" alt="Đao Kiếm Thần Vực" title="Đao Kiếm Thần Vực" data-was-processed="true"></figure>
          <span class="status">Tập 5/5</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title ">
                  <h2 class="entry-title">Đao Kiếm Thần Vực</h2><p class="original_title">Sword Art Offline</p>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-16033" style="position: absolute; left: 181px; top: 1476px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/kaminaki-sekai-no-kamisama-katsudou/" title="Là Thần Trong Thế Giới Vô Thần">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/03/Kaminaki-Sekai-no-Kamisama-Katsudou.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/03/Kaminaki-Sekai-no-Kamisama-Katsudou.jpg" alt="Là Thần Trong Thế Giới Vô Thần" title="Là Thần Trong Thế Giới Vô Thần" data-was-processed="true"></figure>
          <span class="status">Tập 8</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title ">
                  <h2 class="entry-title">Là Thần Trong Thế Giới Vô Thần</h2><p class="original_title">Kaminaki Sekai no Kamisama Katsudou</p>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-16134" style="position: absolute; left: 362px; top: 1476px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/tousouchuu-great-mission/" title="Tousouchuu: Great Mission">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/04/Tousouchuu-Great-Mission.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/04/Tousouchuu-Great-Mission.jpg" alt="Tousouchuu: Great Mission" title="Tousouchuu: Great Mission" data-was-processed="true"></figure>
          <span class="status">Tập 8</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title title-2-line">
                  <h2 class="entry-title">Tousouchuu: Great Mission</h2>
              </div>
          </div>
      </a>
  </div>
</article>
      <article class="col-md-3 col-sm-3 col-xs-6 thumb grid-item post-16012" style="position: absolute; left: 543px; top: 1476px;">
  <div class="halim-item">
      <a class="halim-thumb" href="https://s1.boctem.com/niehime-to-kemono-no-ou/" title="Vua Quái Vật Và Nàng Công Chúa Hiến Tế">
          <figure><img class="lazy img-responsive loaded" src="https://s1.boctem.com/wp-content/uploads/2023/03/Niehime-to-Kemono-no-Ou.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/03/Niehime-to-Kemono-no-Ou.jpg" alt="Vua Quái Vật Và Nàng Công Chúa Hiến Tế" title="Vua Quái Vật Và Nàng Công Chúa Hiến Tế" data-was-processed="true"></figure>
          <span class="status">Tập 7</span><span class="episode"><i class="hl-play"></i>Vietsub</span>                    <div class="icon_overlay"></div>
          <div class="halim-post-title-box">
              <div class="halim-post-title ">
                  <h2 class="entry-title">Vua Quái Vật Và Nàng Công Chúa Hiến Tế</h2><p class="original_title">Niehime to Kemono no Ou</p>
              </div>
          </div>
      </a>
  </div>
</article>
    </div>`)

    const result = $("#halim-advanced-widget-2-ajax-box .grid-item")
      .toArray()
      .map((item) => getTPost($(item)))

    expect(result).toEqual([
      {
        path: "/ousama-ranking-ss2/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/02/Ousama-Ranking-Yuuki-no-Takarabako.jpg",
        name: "Câu Chuyện Về Hoàng Tử Vô Dụng SS2",
        originName: "Ousama Ranking SS2",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/yuusha-ga-shinda/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/03/Yuusha-ga-Shinda.jpg",
        name: "Dũng sĩ đã chết!",
        originName: "Yuusha ga Shinda!",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/dr-stone-ss3/",
        image: "https://s1.boctem.com/wp-content/uploads/2023/02/Dr.-Stone.jpg",
        name: "Dr. Stone SS3",
        originName: "Tiến Sĩ Đá SS3",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/mahoutsukai-no-yome-ss2/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/02/Mahoutsukai-no-Yome-SS2.jpg",
        name: "Cô Dâu Pháp Sư SS2",
        originName: "Mahoutsukai no Yome SS2",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/kore-wa-zombie-desu-ka/",
        image: "https://s1.boctem.com/wp-content/uploads/2021/03/75521.jpg",
        name: "Kore wa Zombie Desu ka",
        originName: "Is This a Zombie?",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/o-re-id1/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/04/O-Re-250x350.jpg",
        name: "Ở Rể",
        originName: "My Heroic Husband",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/gia-thien/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/05/Gia-Thien-1.jpg",
        name: "Già Thiên",
        originName: "Zhe Tian, Shrouding The Heavens",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/kizuna-no-allele/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/04/Kizuna-no-Allele.jpg",
        name: "Kizuna no Allele",
        originName: "",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/kyou-no-asuka-show/",
        image: "https://s1.boctem.com/wp-content/uploads/2021/02/77066.jpg",
        name: "Kyou no Asuka Show",
        originName: "Kyou no Asuka Show",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/tieu-nhan/",
        image: "https://s1.boctem.com/wp-content/uploads/2023/06/Tieu-Nhan.jpg",
        name: "Tiêu Nhân",
        originName: "Biao Ren: Blades of the Guardians",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/white-album-2/",
        image: "https://s1.boctem.com/wp-content/uploads/2021/02/53561.jpg",
        name: "White Album 2",
        originName: "White Album 2",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/hoshizora-e-kakaru-hashi/",
        image: "https://s1.boctem.com/wp-content/uploads/2021/01/73521.jpg",
        name: "Hoshizora e Kakaru Hashi",
        originName: "A Bridge to the Starry Skies",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/barakamon/",
        image: "https://s1.boctem.com/wp-content/uploads/2021/02/65427.jpg",
        name: "Barakamon",
        originName: "Barakamon",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/tuan-san-vo-lam-bat-nhi/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/05/Tuan-San-Vo-Lam-Bat-Nhi.jpg",
        name: "Tuần San Võ Lâm Bất Nhị",
        originName: "Wu Lin Bu Er Zhou Kan",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/son-hai-te-hoi/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/04/son-hai-te-hoi_250x350.jpg",
        name: "Sơn Hải Tế Hội",
        originName: "",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/khoi-dau-co-kiem-vuc-ta-se-tro-thanh-kiem-than/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/02/khoi-dau-co-kiem-vuc-ta-se-tro-thanh-kiem-than_250x350.jpg",
        name: "Khởi Đầu Có Kiếm Vực, Ta Sẽ Trở Thành Kiếm Thần",
        originName: "",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/van-gioi-tien-tung/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2021/01/download-32.jpg",
        name: "Vạn Giới Tiên Tung",
        originName: "",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/van-co-than-thoai/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/01/van-co-than-thoai-14935-250x350.jpg",
        name: "Vạn Cổ Thần Thoại",
        originName: "",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/thanh-lien-kiem-tien-truyen/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/04/Thanh-Lien-Kiem-Tien-Truyen-250x350.jpg",
        name: "Thanh Liên Kiếm Tiên Truyện",
        originName: "Legend Of Lotus Sword Fairy",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/than-an-vuong-toa/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2022/07/Throne-Of-Seal.jpg",
        name: "Thần Ấn Vương Tọa",
        originName: "Throne of Seal",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/nguyen-ton/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2022/08/nguyen-ton-phan-1-2.png",
        name: "Nguyên Tôn",
        originName: "",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/kiem-vuc-phong-van-phan-2/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/01/kiem-vuc-phong-van-phan-2-15805.jpg",
        name: "Kiếm Vực Phong Vân Phần 2",
        originName: "",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/doc-bo-tieu-dao/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/01/doc-bo-tieu-dao-2683.jpg",
        name: "Độc Bộ Tiêu Dao",
        originName: "",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/bat-diet-than-vuong/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/04/Bat-Diet-Than-Vuong.png",
        name: "Bất Diệt Thần Vương",
        originName: "",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/dao-kiem-than-vuc-sword-art-offline/",
        image: "https://s1.boctem.com/wp-content/uploads/2021/02/43461.jpg",
        name: "Đao Kiếm Thần Vực",
        originName: "Sword Art Offline",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/kaminaki-sekai-no-kamisama-katsudou/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/03/Kaminaki-Sekai-no-Kamisama-Katsudou.jpg",
        name: "Là Thần Trong Thế Giới Vô Thần",
        originName: "Kaminaki Sekai no Kamisama Katsudou",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/tousouchuu-great-mission/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/04/Tousouchuu-Great-Mission.jpg",
        name: "Tousouchuu: Great Mission",
        originName: "",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
      {
        path: "/niehime-to-kemono-no-ou/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/03/Niehime-to-Kemono-no-Ou.jpg",
        name: "Vua Quái Vật Và Nàng Công Chúa Hiến Tế",
        originName: "Niehime to Kemono no Ou",
        timeRelease: undefined,
        views: undefined,
        isTrailer: false,
      },
    ])
  })

  test("should hot", () => {
    const $ =
      parserDom(`<div id="halim-ajax-popular-post" class="popular-post">        <div class="item post-15897">
    <a href="https://s1.boctem.com/mashle-magic-and-muscles/" title="Mashle: Magic and Muscles">
        <div class="item-link">
            <img src="https://s1.boctem.com/wp-content/uploads/2023/02/Mashle-Magic-and-Muscles.jpg" class="lazy post-thumb" alt="Mashle: Magic and Muscles" title="Mashle: Magic and Muscles">

        </div>
        <h3 class="title">Mashle: Magic and Muscles</h3>
        <p class="original_title">Mashle: Ma Thuật Và Cơ Bắp</p>
    </a>
    <div class="viewsCount">4.5K lượt xem</div>
</div>
        <div class="item post-15873">
    <a href="https://s1.boctem.com/dr-stone-ss3/" title="Dr. Stone SS3">
        <div class="item-link">
            <img src="https://s1.boctem.com/wp-content/uploads/2023/02/Dr.-Stone.jpg" class="lazy post-thumb" alt="Dr. Stone SS3" title="Dr. Stone SS3">

        </div>
        <h3 class="title">Dr. Stone SS3</h3>
        <p class="original_title">Tiến Sĩ Đá SS3</p>
    </a>
    <div class="viewsCount">3K lượt xem</div>
</div>
        <div class="item post-15870">
    <a href="https://s1.boctem.com/luoi-guom-diet-quy-ss3/" title="Lưỡi Gươm Diệt Quỷ SS3">
        <div class="item-link">
            <img src="https://s1.boctem.com/wp-content/uploads/2023/02/Kimetsu-no-Yaiba-Katanakaji-no-Sato-hen.jpg" class="lazy post-thumb" alt="Lưỡi Gươm Diệt Quỷ SS3" title="Lưỡi Gươm Diệt Quỷ SS3">

        </div>
        <h3 class="title">Lưỡi Gươm Diệt Quỷ SS3</h3>
        <p class="original_title">Kimetsu no Yaiba: Katanakaji no Sato-hen</p>
    </a>
    <div class="viewsCount">2.4K lượt xem</div>
</div>
        <div class="item post-15885">
    <a href="https://s1.boctem.com/oshi-no-ko/" title="[Đứa Con Của Thần Tượng]">
        <div class="item-link">
            <img src="https://s1.boctem.com/wp-content/uploads/2023/02/Oshi-no-Ko.jpg" class="lazy post-thumb" alt="[Đứa Con Của Thần Tượng]" title="[Đứa Con Của Thần Tượng]">

        </div>
        <h3 class="title">[Đứa Con Của Thần Tượng]</h3>
        <p class="original_title">Oshi no Ko</p>
    </a>
    <div class="viewsCount">1.8K lượt xem</div>
</div>
        <div class="item post-15918">
    <a href="https://s1.boctem.com/yamada-kun-to-lv999-no-koi-wo-suru/" title="Yêu Yamada ở Lv999!">
        <div class="item-link">
            <img src="https://s1.boctem.com/wp-content/uploads/2023/03/Yamada-kun-to-Lv999-no-Koi-wo-Suru.jpg" class="lazy post-thumb" alt="Yêu Yamada ở Lv999!" title="Yêu Yamada ở Lv999!">

        </div>
        <h3 class="title">Yêu Yamada ở Lv999!</h3>
        <p class="original_title">Yamada-kun to Lv999 no Koi wo Suru</p>
    </a>
    <div class="viewsCount">1K lượt xem</div>
</div>
        <div class="item post-2112">
    <a href="https://s1.boctem.com/thu-linh-the-bai-ss2/" title="Thủ Lĩnh Thẻ Bài SS2">
        <div class="item-link">
            <img src="https://s1.boctem.com/wp-content/uploads/2021/01/90338.jpg" class="lazy post-thumb" alt="Thủ Lĩnh Thẻ Bài SS2" title="Thủ Lĩnh Thẻ Bài SS2">

        </div>
        <h3 class="title">Thủ Lĩnh Thẻ Bài SS2</h3>
        <p class="original_title">Cardcaptor Sakura: Clear Card-hen</p>
    </a>
    <div class="viewsCount">1K lượt xem</div>
</div>
        <div class="item post-15981">
    <a href="https://s1.boctem.com/skip-to-loafer/" title="Skip to Loafer">
        <div class="item-link">
            <img src="https://s1.boctem.com/wp-content/uploads/2023/03/Skip-to-Loafer.jpg" class="lazy post-thumb" alt="Skip to Loafer" title="Skip to Loafer">

        </div>
        <h3 class="title">Skip to Loafer</h3>
        <p class="original_title">Skip and Loafer</p>
    </a>
    <div class="viewsCount">624 lượt xem</div>
</div>
        <div class="item post-15315">
    <a href="https://s1.boctem.com/tokyo-revengers-ss2/" title="Tokyo Revengers SS2">
        <div class="item-link">
            <img src="https://s1.boctem.com/wp-content/uploads/2022/12/Tokyo-Revengers-Seiya-Kessen-hen.jpg" class="lazy post-thumb" alt="Tokyo Revengers SS2" title="Tokyo Revengers SS2">

        </div>
        <h3 class="title">Tokyo Revengers SS2</h3>
        <p class="original_title">Tokyo Revengers: Seiya Kessen-hen</p>
    </a>
    <div class="viewsCount">594 lượt xem</div>
</div>
        <div class="item post-15888">
    <a href="https://s1.boctem.com/mahoutsukai-no-yome-ss2/" title="Cô Dâu Pháp Sư SS2">
        <div class="item-link">
            <img src="https://s1.boctem.com/wp-content/uploads/2023/02/Mahoutsukai-no-Yome-SS2.jpg" class="lazy post-thumb" alt="Cô Dâu Pháp Sư SS2" title="Cô Dâu Pháp Sư SS2">

        </div>
        <h3 class="title">Cô Dâu Pháp Sư SS2</h3>
        <p class="original_title">Mahoutsukai no Yome SS2</p>
    </a>
    <div class="viewsCount">548 lượt xem</div>
</div>
</div>`)

    const result = $("#halim-ajax-popular-post > .item")
      .toArray()
      .map((item) => getTPost($(item)))

    expect(result).toEqual([
      {
        path: "/mashle-magic-and-muscles/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/02/Mashle-Magic-and-Muscles.jpg",
        name: "Mashle: Magic and Muscles",
        originName: "Mashle: Ma Thuật Và Cơ Bắp",
        timeRelease: undefined,
        views: 4500,
        isTrailer: false,
      },
      {
        path: "/dr-stone-ss3/",
        image: "https://s1.boctem.com/wp-content/uploads/2023/02/Dr.-Stone.jpg",
        name: "Dr. Stone SS3",
        originName: "Tiến Sĩ Đá SS3",
        timeRelease: undefined,
        views: 3000,
        isTrailer: false,
      },
      {
        path: "/luoi-guom-diet-quy-ss3/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/02/Kimetsu-no-Yaiba-Katanakaji-no-Sato-hen.jpg",
        name: "Lưỡi Gươm Diệt Quỷ SS3",
        originName: "Kimetsu no Yaiba: Katanakaji no Sato-hen",
        timeRelease: undefined,
        views: 2400,
        isTrailer: false,
      },
      {
        path: "/oshi-no-ko/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/02/Oshi-no-Ko.jpg",
        name: "[Đứa Con Của Thần Tượng]",
        originName: "Oshi no Ko",
        timeRelease: undefined,
        views: 1800,
        isTrailer: false,
      },
      {
        path: "/yamada-kun-to-lv999-no-koi-wo-suru/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/03/Yamada-kun-to-Lv999-no-Koi-wo-Suru.jpg",
        name: "Yêu Yamada ở Lv999!",
        originName: "Yamada-kun to Lv999 no Koi wo Suru",
        timeRelease: undefined,
        views: 1000,
        isTrailer: false,
      },
      {
        path: "/thu-linh-the-bai-ss2/",
        image: "https://s1.boctem.com/wp-content/uploads/2021/01/90338.jpg",
        name: "Thủ Lĩnh Thẻ Bài SS2",
        originName: "Cardcaptor Sakura: Clear Card-hen",
        timeRelease: undefined,
        views: 1000,
        isTrailer: false,
      },
      {
        path: "/skip-to-loafer/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/03/Skip-to-Loafer.jpg",
        name: "Skip to Loafer",
        originName: "Skip and Loafer",
        timeRelease: undefined,
        views: 624,
        isTrailer: false,
      },
      {
        path: "/tokyo-revengers-ss2/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2022/12/Tokyo-Revengers-Seiya-Kessen-hen.jpg",
        name: "Tokyo Revengers SS2",
        originName: "Tokyo Revengers: Seiya Kessen-hen",
        timeRelease: undefined,
        views: 594,
        isTrailer: false,
      },
      {
        path: "/mahoutsukai-no-yome-ss2/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/02/Mahoutsukai-no-Yome-SS2.jpg",
        name: "Cô Dâu Pháp Sư SS2",
        originName: "Mahoutsukai no Yome SS2",
        timeRelease: undefined,
        views: 548,
        isTrailer: false,
      },
    ])
  })

  test("should trailer", () => {
    const $ =
      parserDom(`<div id="halim_trailer-widget-2" class="widget halim_trailer-widget">
    <div class="section-bar clearfix">
      <h3 class="section-title"><span>Trailer</span></h3>
    </div>

    <div class="popular-post">
              <div class="item post-16431">
          <a href="https://s1.boctem.com/horimiya-ss2/" title="Horimiya SS2">
              <div class="item-link">
                  <img src="https://s1.boctem.com/wp-content/uploads/2023/05/Horimiya-Piece.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/05/Horimiya-Piece.jpg" class="lazy post-thumb loaded" alt="Horimiya SS2" title="Horimiya SS2" data-was-processed="true">
                  <span class="is_trailer">Trailer</span>
              </div>
              <h3 class="title">Horimiya SS2</h3>
              <p class="original_title">Horimiya: The Missing Pieces</p>
          </a>
          <div class="viewsCount">53 lượt xem</div>
      </div>
              <div class="item post-16428">
          <a href="https://s1.boctem.com/ban-gai-thue-ss3/" title="Bạn Gái Thuê SS3">
              <div class="item-link">
                  <img src="https://s1.boctem.com/wp-content/uploads/2023/05/Ban-Gai-Thue-SS3.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/05/Ban-Gai-Thue-SS3.jpg" class="lazy post-thumb loaded" alt="Bạn Gái Thuê SS3" title="Bạn Gái Thuê SS3" data-was-processed="true">
                  <span class="is_trailer">Trailer</span>
              </div>
              <h3 class="title">Bạn Gái Thuê SS3</h3>
              <p class="original_title">Kanojo, Okarishimasu SS3</p>
          </a>
          <div class="viewsCount">31 lượt xem</div>
      </div>
              <div class="item post-16425">
          <a href="https://s1.boctem.com/bleach-sennen-kessen-hen-ketsubetsu-tan/" title="Bleach: Sennen Kessen-hen - Ketsubetsu-tan">
              <div class="item-link">
                  <img src="https://s1.boctem.com/wp-content/uploads/2023/05/Bleach-Sennen-Kessen-hen-Ketsubetsu-tan.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/05/Bleach-Sennen-Kessen-hen-Ketsubetsu-tan.jpg" class="lazy post-thumb loaded" alt="Bleach: Sennen Kessen-hen - Ketsubetsu-tan" title="Bleach: Sennen Kessen-hen - Ketsubetsu-tan" data-was-processed="true">
                  <span class="is_trailer">Trailer</span>
              </div>
              <h3 class="title">Bleach: Sennen Kessen-hen - Ketsubetsu-tan</h3>
              <p class="original_title">Bleach: Thousand-Year Blood War Arc</p>
          </a>
          <div class="viewsCount">22 lượt xem</div>
      </div>
              <div class="item post-16422">
          <a href="https://s1.boctem.com/masamune-kun-no-revenge-r/" title="Masamune-kun no Revenge R">
              <div class="item-link">
                  <img src="https://s1.boctem.com/wp-content/uploads/2023/05/Masamune-kun-no-Revenge-R.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/05/Masamune-kun-no-Revenge-R.jpg" class="lazy post-thumb loaded" alt="Masamune-kun no Revenge R" title="Masamune-kun no Revenge R" data-was-processed="true">
                  <span class="is_trailer">Trailer</span>
              </div>
              <h3 class="title">Masamune-kun no Revenge R</h3>
              <p class="original_title">Masamune-kun's Revenge R</p>
          </a>
          <div class="viewsCount">22 lượt xem</div>
      </div>
              <div class="item post-16419">
          <a href="https://s1.boctem.com/that-nghiep-chuyen-sinh-lam-lai-cuoc-doi-ss3/" title="Thất Nghiệp Chuyển Sinh: Làm Lại Cuộc Đời SS3">
              <div class="item-link">
                  <img src="https://s1.boctem.com/wp-content/uploads/2023/05/That-Nghiep-Chuyen-Sinh-Lam-Lai-Cuoc-Doi-SS3.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/05/That-Nghiep-Chuyen-Sinh-Lam-Lai-Cuoc-Doi-SS3.jpg" class="lazy post-thumb loaded" alt="Thất Nghiệp Chuyển Sinh: Làm Lại Cuộc Đời SS3" title="Thất Nghiệp Chuyển Sinh: Làm Lại Cuộc Đời SS3" data-was-processed="true">
                  <span class="is_trailer">Trailer</span>
              </div>
              <h3 class="title">Thất Nghiệp Chuyển Sinh: Làm Lại Cuộc Đời SS3</h3>
              <p class="original_title">Mushoku Tensei: Jobless Reincarnation SS3</p>
          </a>
          <div class="viewsCount">54 lượt xem</div>
      </div>
              <div class="item post-16416">
          <a href="https://s1.boctem.com/jujutsu-kaisen-ss2/" title="Jujutsu Kaisen SS2">
              <div class="item-link">
                  <img src="https://s1.boctem.com/wp-content/uploads/2023/05/Vat-The-Bi-Nguyen-Rua.jpg" data-src="https://s1.boctem.com/wp-content/uploads/2023/05/Vat-The-Bi-Nguyen-Rua.jpg" class="lazy post-thumb loaded" alt="Jujutsu Kaisen SS2" title="Jujutsu Kaisen SS2" data-was-processed="true">
                  <span class="is_trailer">Trailer</span>
              </div>
              <h3 class="title">Jujutsu Kaisen SS2</h3>
              <p class="original_title">Vật Thể Bị Nguyền Rủa</p>
          </a>
          <div class="viewsCount">37 lượt xem</div>
      </div>

    </div>
    <div class="clearfix"></div>
  </div>`)

    const result = $("#halim_trailer-widget-2 .item")
      .toArray()
      .map((item) => getTPost($(item)))

    expect(result).toEqual([
      {
        path: "/horimiya-ss2/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/05/Horimiya-Piece.jpg",
        name: "Horimiya SS2",
        originName: "Horimiya: The Missing Pieces",
        timeRelease: undefined,
        views: 53,
        isTrailer: true,
      },
      {
        path: "/ban-gai-thue-ss3/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/05/Ban-Gai-Thue-SS3.jpg",
        name: "Bạn Gái Thuê SS3",
        originName: "Kanojo, Okarishimasu SS3",
        timeRelease: undefined,
        views: 31,
        isTrailer: true,
      },
      {
        path: "/bleach-sennen-kessen-hen-ketsubetsu-tan/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/05/Bleach-Sennen-Kessen-hen-Ketsubetsu-tan.jpg",
        name: "Bleach: Sennen Kessen-hen - Ketsubetsu-tan",
        originName: "Bleach: Thousand-Year Blood War Arc",
        timeRelease: undefined,
        views: 22,
        isTrailer: true,
      },
      {
        path: "/masamune-kun-no-revenge-r/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/05/Masamune-kun-no-Revenge-R.jpg",
        name: "Masamune-kun no Revenge R",
        originName: "Masamune-kun's Revenge R",
        timeRelease: undefined,
        views: 22,
        isTrailer: true,
      },
      {
        path: "/that-nghiep-chuyen-sinh-lam-lai-cuoc-doi-ss3/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/05/That-Nghiep-Chuyen-Sinh-Lam-Lai-Cuoc-Doi-SS3.jpg",
        name: "Thất Nghiệp Chuyển Sinh: Làm Lại Cuộc Đời SS3",
        originName: "Mushoku Tensei: Jobless Reincarnation SS3",
        timeRelease: undefined,
        views: 54,
        isTrailer: true,
      },
      {
        path: "/jujutsu-kaisen-ss2/",
        image:
          "https://s1.boctem.com/wp-content/uploads/2023/05/Vat-The-Bi-Nguyen-Rua.jpg",
        name: "Jujutsu Kaisen SS2",
        originName: "Vật Thể Bị Nguyền Rủa",
        timeRelease: undefined,
        views: 37,
        isTrailer: true,
      },
    ])
  })
})
