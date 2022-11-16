<template>
  <div v-if="loading" class="absolute w-full h-full overflow-hidden loader">
    <div class="swiper-hot mt-[-60px]">
      <q-responsive :ratio="aspectRatio" class="poster">
        <q-skeleton type="rect" width="100%" height="100%" />
      </q-responsive>
      <div
        class="mark-b w-full h-[30%] z-101 absolute bottom-0 pointer-events-none"
        :style="{
          'background-image': `linear-gradient(
            rgba(17, 19, 25, 0) 2%,
            rgb(17, 19, 25) 94%
          )`,
        }"
      />
    </div>

    <div class="px-4 md:px-13 relative">
      <div class="wpa-grid">
        <div class="ctnr">
          <SkeletonCard
            v-for="item in 12"
            :key="item"
            class="card-wrap inline-block"
          />
        </div>
      </div>
    </div>

    <div class="px-4 md:px-13 relative">
      <q-skeleton type="text" width="7rem" class="text-h6" />

      <SkeletonGridCard :count="6" />
    </div>

    <div class="px-4 md:px-13 relative">
      <q-skeleton type="text" width="7rem" class="text-h6" />

      <div class="wpa-grid">
        <div class="ctnr">
          <SkeletonCard
            v-for="item in 12"
            :key="item"
            class="card-wrap inline-block"
          />
        </div>
      </div>
    </div>

    <div class="px-4 md:px-13 relative">
      <q-skeleton type="text" width="7rem" class="text-h6" />

      <div class="wpa-grid">
        <q-skeleton type="text" width="100%" />
        <div class="ctnr">
          <SkeletonCard
            v-for="item in 12"
            :key="item"
            class="card-wrap inline-block"
          />
        </div>
      </div>
    </div>

    <div class="px-4 md:px-13 relative">
      <q-skeleton type="text" width="7rem" class="text-h6" />

      <SkeletonGridCard :count="6" />
    </div>
  </div>
  <template v-else-if="data">
    <swiper
      :slides-per-view="1"
      :space-between="0"
      :modules="[Autoplay]"
      :autoplay="{
        delay: 5000,
        disableOnInteraction: false,
      }"
      class="swiper-hot"
    >
      <swiper-slide
        v-for="(item, index) in data.carousel"
        :key="index"
        v-ripple
        @click="router.push(item.path)"
      >
        <q-img
          no-spinner
          :ratio="aspectRatio"
          :src="item.image!"
          class="poster"
        />
        <div class="drop-left"></div>
        <div class="drop-center"></div>
        <div class="drop-right"></div>
        <div class="info">
          <div class="flex line-clamp-2 items-center">
            <Quality>{{ item.quality }}</Quality>
            <div class="text-weight-medium">{{ item.name }}</div>
          </div>
          <div class="focus-item-info">
            <span class="focus-item-score">
              <Star :label="item.rate" />
            </span>
            <span class="focus-item-year">{{ item.year }}</span>
            <span class="focus-item-update">
              {{ item.process }}
            </span>
          </div>
          <div class="focus-item-tags" v-if="item.genre.length > 0">
            <template v-for="(tag, index) in item.genre" :key="tag.name">
              <router-link class="c--main" :to="tag.path">{{
                tag.name
              }}</router-link>
              <template v-if="index < item.genre.length - 1">, </template>
            </template>
          </div>
          <div class="focus-item-desc">
            {{ item.description }}
          </div>
        </div>
      </swiper-slide>

      <div
        class="mark-b w-full h-[30%] z-101 absolute bottom-0 pointer-events-none"
        :style="{
          'background-image': `linear-gradient(
            rgba(17, 19, 25, 0) 2%,
            rgb(17, 19, 25) 94%
          )`,
        }"
      />
    </swiper>

    <div class="px-4 md:px-13 relative">
      <swiper
        :slides-per-view="6"
        :navigation="{
          nextEl: '.swiper-button-next',
          prevEl: '.swiper-button-prev',
        }"
        :modules="[Navigation]"
        :breakpoints="{
          [$q.screen.sizes.sm]: {
            slidesPerView: 4,
          },
          [$q.screen.sizes.md]: {
            slidesPerView: 6,
          },
        }"
      >
        <swiper-slide
          v-for="item in data.thisSeason"
          :key="item.name"
          class="card-wrap"
        >
          <Card :data="item" />
        </swiper-slide>
      </swiper>

      <div class="nav-btn swiper-button-next" />
      <div class="nav-btn swiper-button-prev" />
    </div>

    <div class="px-4 mt-4 md:px-14">
      <router-link
        to="/bang-xep-hang/day"
        class="text-subtitle1 text-weight-normal mx-2 flex items-center justify-between"
      >
        {{ t("de-xuat") }}
        <Icon
          icon="fluent:chevron-right-24-regular"
          class="text-grey"
          width="18"
          height="18"
        />
      </router-link>

      <GridCard :items="data.nominate" />
    </div>

    <div class="mt-4">
      <router-link
        to="/bang-xep-hang/day"
        class="px-4 md:px-13 text-subtitle1 text-weight-normal mx-2 flex items-center justify-between"
      >
        {{ t("top") }}

        <Icon
          icon="fluent:chevron-right-24-regular"
          class="text-grey"
          width="18"
          height="18"
        />
      </router-link>

      <div class="relative px-4 md:px-13">
        <swiper
          :slides-per-view="6"
          :navigation="{
            nextEl: '.swiper-button-next',
            prevEl: '.swiper-button-prev',
          }"
          :modules="[Navigation]"
          :breakpoints="{
            [$q.screen.sizes.sm]: {
              slidesPerView: 4,
            },
            [$q.screen.sizes.md]: {
              slidesPerView: 6,
            },
          }"
        >
          <swiper-slide
            v-for="(item, index) in data.hotUpdate"
            :key="item.name"
            class="card-wrap"
          >
            <Card :data="item" :trending="index + 1" />
          </swiper-slide>
        </swiper>

        <div class="nav-btn swiper-button-next" />
        <div class="nav-btn swiper-button-prev" />
      </div>
    </div>

    <div class="mt-4">
      <router-link
        to="/anime-sap-chieu"
        class="px-4 md:px-13 text-subtitle1 text-weight-normal mx-2 flex items-center justify-between"
      >
        {{ t("sap-chieu") }}

        <Icon
          icon="fluent:chevron-right-24-regular"
          class="text-grey"
          width="18"
          height="18"
        />
      </router-link>

      <div class="relative px-4 md:px-13">
        <swiper
          :slides-per-view="6"
          :navigation="{
            nextEl: '.swiper-button-next',
            prevEl: '.swiper-button-prev',
          }"
          :modules="[Navigation]"
          :breakpoints="{
            [$q.screen.sizes.sm]: {
              slidesPerView: 4,
            },
            [$q.screen.sizes.md]: {
              slidesPerView: 6,
            },
          }"
        >
          <swiper-slide
            v-for="item in data.preRelease"
            :key="item.name"
            class="relative card-wrap"
          >
            <div class="coming_soon-timeline absolute top-0 left-0">
              <div class="coming_soon-line"></div>
              <div class="coming_soon-dot"></div>
              <div class="coming_soon-time-wrapper">
                <div
                  v-if="
                    (tmp = item.time_release ? dayjs(item.time_release) : null)
                  "
                >
                  <!-- if in today or tomorrow -->
                  <template
                    v-if="(isTodayF = tmp.isToday()) || tmp.isTomorrow()"
                  >
                    <div class="coming_soon-text-date">
                      {{ tmp.format("HH:mm") }}
                    </div>
                    <div class="coming_soon-text-day">
                      <template v-if="isTodayF"> {{ t("hom-nay") }} </template>
                      <template v-else> {{ t("ngay-mai") }} </template>
                    </div>
                  </template>
                  <template v-else>
                    <div class="coming_soon-text-date">
                      {{
                        tmp.format(
                          tmp.year() === new Date().getFullYear()
                            ? "M-DD"
                            : "YYYY-MM-DD"
                        )
                      }}
                    </div>
                    <div class="coming_soon-text-day capitalize">
                      {{ tmp.locale("vi").format("dddd") }}
                    </div>
                  </template>

                  <!-- <template v-else>{{ tmp.format("DD/MM") }}</template> -->
                </div>
                <span v-else class="coming_soon-text-unknown">{{
                  t("sap-chieu")
                }}</span>
              </div>
            </div>

            <Card :data="item" />
          </swiper-slide>
        </swiper>

        <div class="nav-btn swiper-button-next" />
        <div class="nav-btn swiper-button-prev" />
      </div>
    </div>

    <div class="px-4 mt-4 md:px-14">
      <router-link
        to="/anime-moi"
        class="text-subtitle1 text-weight-normal mx-2 flex items-center justify-between"
      >
        {{ t("moi-cap-nhat") }}

        <Icon
          icon="fluent:chevron-right-24-regular"
          class="text-grey"
          width="18"
          height="18"
        />
      </router-link>

      <GridCard :items="data.lastUpdate" />
    </div>
  </template>
  <ScreenError v-else class="absolute" />
</template>

<script setup lang="ts">
import { Icon } from "@iconify/vue"
import { useHead } from "@vueuse/head"
import Card from "components/Card.vue"
// eslint-disable-next-line import/order
import { useRequest } from "vue-request"
// eslint-disable-next-line import/order
import Star from "components/Star.vue"
import GridCard from "components/GridCard.vue"
import Quality from "components/Quality.vue"
import ScreenError from "components/ScreenError.vue"
import SkeletonCard from "components/SkeletonCard.vue"
import SkeletonGridCard from "components/SkeletonGridCard.vue"
import { Index } from "src/apis/runs/index"
import appIcon from "src/assets/app_icon.svg"
import dayjs from "src/logic/dayjs"
import { Autoplay, Navigation } from "swiper"
import { Swiper, SwiperSlide } from "swiper/vue"
import { computed } from "vue"
import { useI18n } from "vue-i18n"
import { useRouter } from "vue-router"

// Import Swiper styles
import "swiper/css"
import "swiper/css/pagination"
import "swiper/css/navigation"
import "swiper/css/autoplay"
import "swiper/css/grid"
// Import Swiper Vue.js components

const { t } = useI18n()
useHead(
  computed(() => {
    const title = "AnimeVsub"

    const description = t(
      "xem-anime-vietsub-online-xem-tren-dien-thoai-di-dong-va-may-tinh-nhanh-nhat"
    )

    return {
      title,
      description,
      meta: [
        { property: "og:title", content: title },
        { property: "og:description", content: description },
        { property: "og:image", content: process.env.APP_URL + appIcon },
        {
          property: "og:url",
          content: process.env.APP_URL,
        },
      ],
      link: [
        {
          rel: "canonical",
          href: process.env.APP_URL,
        },
      ],
    }
  })
)

const router = useRouter()

const aspectRatio = 622 / 350

const { data, loading } = useRequest(() => Index())

// eslint-disable-next-line functional/no-let, @typescript-eslint/no-explicit-any
let tmp: any
// eslint-disable-next-line functional/no-let, prefer-const
let isTodayF = false
</script>

<style lang="scss" scoped>
.nav-btn {
  width: 2em;
  height: 2em;
  --swiper-navigation-size: 25px;
  font-weight: bold;
  color: white;

  &.swiper-button {
    &-next {
      right: 30px;
    }
    &-prev {
      left: 30px;
    }
  }
}

.swiper-hot {
  position: relative;
  overflow: hidden;
  margin-bottom: -15.5%;
  cursor: pointer;
  z-index: 0;
  height: 56vw;
  max-height: 1012px;
  @media screen and (max-width: 767px) {
    margin-bottom: 16px;
    height: auto;

    .poster {
      height: max(calc(100vw / v-bind("aspectRatio")), 40vh, 56vw);
    }
  }
  @media (min-width: $breakpoint-sm-min) {
    margin-bottom: -10%;
  }
  @media (min-width: $breakpoint-md-min) {
    margin-bottom: -16%;
  }

  .drop {
    &-left {
      position: absolute;
      left: 0;
      top: 0;
      width: 30%;
      height: 100%;
      background: linear-gradient(
        269deg,
        rgba(20, 30, 51, 0) 1%,
        rgba(20, 30, 51, 0.02) 10%,
        rgba(20, 30, 51, 0.05) 18%,
        rgba(20, 30, 51, 0.12) 25%,
        rgba(20, 30, 51, 0.2) 32%,
        rgba(20, 30, 51, 0.29) 38%,
        rgba(20, 30, 51, 0.39) 44%,
        rgba(20, 30, 51, 0.5) 50%,
        rgba(20, 30, 51, 0.61) 57%,
        rgba(20, 30, 51, 0.71) 63%,
        rgba(20, 30, 51, 0.8) 69%,
        rgba(20, 30, 51, 0.88) 76%,
        rgba(20, 30, 51, 0.95) 83%,
        rgba(20, 30, 51, 0.98) 91%,
        rgb(20, 30, 51) 100%
      );
      z-index: 101;

      @media screen and (max-width: 767px) {
        display: none;
      }
    }
    &-center {
      position: absolute;
      left: 0px;
      top: 0px;
      width: 100%;
      height: 120px;
      opacity: 0.7;
      background-image: linear-gradient(
        179.5deg,
        rgba(17, 19, 25, 0.88) 0%,
        rgba(17, 19, 25, 0.89) 9%,
        rgba(17, 19, 25, 0.85) 17%,
        rgba(17, 19, 25, 0.79) 24%,
        rgba(17, 19, 25, 0.72) 31%,
        rgba(17, 19, 25, 0.64) 37%,
        rgba(17, 19, 25, 0.55) 44%,
        rgba(17, 19, 25, 0.45) 50%,
        rgba(17, 19, 25, 0.35) 56%,
        rgba(17, 19, 25, 0.26) 63%,
        rgba(17, 19, 25, 0.18) 69%,
        rgba(17, 19, 25, 0.11) 76%,
        rgba(17, 19, 25, 0.05) 83%,
        rgba(17, 19, 25, 0.01) 91%,
        rgba(17, 19, 25, 0) 100%
      );
    }
    &-right {
      position: absolute;
      right: 0;
      top: 0;
      width: 15%;
      height: 100%;
      background: linear-gradient(
        90deg,
        rgba(20, 30, 51, 0) 1%,
        rgba(20, 30, 51, 0.02) 10%,
        rgba(20, 30, 51, 0.05) 18%,
        rgba(20, 30, 51, 0.12) 25%,
        rgba(20, 30, 51, 0.2) 32%,
        rgba(20, 30, 51, 0.29) 38%,
        rgba(20, 30, 51, 0.39) 44%,
        rgba(20, 30, 51, 0.5) 50%,
        rgba(20, 30, 51, 0.61) 57%,
        rgba(20, 30, 51, 0.71) 63%,
        rgba(20, 30, 51, 0.8) 69%,
        rgba(20, 30, 51, 0.88) 76%,
        rgba(20, 30, 51, 0.95) 83%,
        rgba(20, 30, 51, 0.98) 91%,
        rgb(20, 30, 51) 100%
      );
      z-index: 101;

      @media screen and (max-width: 1808px) {
        display: none;
      }
    }
  }

  .info {
    position: absolute;
    bottom: 0;
    left: 0;
    z-index: 123;
    color: rgb(255, 255, 255);
    width: 100%;
    padding: 60px 30px calc(15% + 24px + 3.5vw) (30px + 64);
    // padding-top: 0;
    background-image: linear-gradient(
      -180deg,
      rgba(0, 0, 0, 0) 0,
      #000000 100%
    );

    @media (min-width: $breakpoint-sm-min) {
      font-size: 1.3rem;
    }

    @media screen and (max-width: 1023px) and (min-width: 768px) {
      padding: {
        left: 56px;
        bottom: calc(15% + 16px + 36px);
      }
    }
    @media screen and (max-width: 767px) {
      padding: {
        bottom: 20px;
        left: 15px;
      }
    }

    .focus-item-info {
      max-width: 80%;
      font-weight: 500;
      margin-top: 12px;
      display: flex;
      font-size: 12px;
      align-items: center;
      .focus-item-score {
        font-size: 14px;
        color: rgb(0, 194, 52); //  rgb(0, 214, 57);
        font-weight: 700;
        svg {
          width: 12px;
          height: 12px;
          margin-right: 4px;
        }
      }
      .focus-item-year,
      .focus-item-update {
        // text-shadow: rgb(0 0 0 / 50%) 0px 1px 2px;
      }

      .focus-item-score,
      .focus-item-year {
        display: inline-flex;
        line-height: 17px;
        align-items: center;

        &:after {
          content: "";
          margin: 0px 6px;
          height: 10px;
          width: 2px;
          background: rgba(255, 255, 255, 0.2);
        }
      }
    }
    .focus-item-tags {
      margin-top: 13px;
      font-size: 12px;
      @media (min-width: $breakpoint-sm-min) {
        font-size: 14px;
      }
      line-height: 16px;
      color: rgb(236, 236, 236);
      font-weight: 500;
      text-shadow: rgb(0 0 0 / 50%) 0px 1px 2px;
      span {
        display: inline-block;
        margin-right: 6px;
        padding: 0px 5px;
        background: rgba(255, 255, 255, 0.08);
        border-radius: 2px;
      }
    }
    .focus-item-desc {
      width: 31.25vw;
      min-width: 320px;
      overflow: hidden;
      height: 32px;
      line-height: 16px;
      margin-top: 12px;
      font-size: 14px;
      display: -webkit-box;
      text-overflow: ellipsis;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      text-shadow: rgb(0 0 0 / 50%) 0px 1px 2px;
      font-weight: 400; //500;
      @media screen and (max-width: 1023px) and (min-width: 768px) {
        font-size: 14px;
        font-weight: 500;
      }
    }
  }
}
</style>

<style lang="scss" scoped>
.coming_soon {
  &-timeline {
    @apply relative mt-[6px] mb-[14px];

    @media screen and (max-width: 767px) {
      @apply mb-[6px] flex items-center;
      @apply mr-[-8px];
    }
  }
  &-line {
    @apply w-[calc(100%+16px)] h-[2px] bg-[rgb(45,47,52)];
    @media screen and (max-width: 767px) {
      @apply w-full;
    }
    @media screen and (max-width: 767px) {
      order: 2;
    }
  }
  &-dot {
    @apply w-[10px] h-[10px] mx-auto mt-[-6px];
    background: rgb(130, 131, 135);
    border: 2px solid rgb(17, 19, 25);
    border-radius: 50%;

    @media screen and (max-width: 767px) {
      display: none;
    }
  }
  &-time-wrapper {
    @media screen and (max-width: 767px) {
      order: 1;
    }
    @apply text-center h-[42px] mt-4;
    // @apply absolute;

    @media screen and (max-width: 767px) {
      @apply flex items-center ml-[-14px];
      // position: absolute;
      height: 30px;
      margin: 0;
      padding-right: 8px;
      padding-left: 8px;
      text-align: left;
      font-size: 11px;
      color: rgb(188, 189, 190);
      white-space: nowrap;
    }
  }
}
.coming_soon-text {
  &-date {
    font-size: 14px;
    color: rgb(188, 189, 190);

    @media screen and (max-width: 767px) {
      display: inline-block;
      padding: 0px 2px;
      margin-left: -2px;
      background: rgb(17, 19, 25);
      font-size: 12px;
    }
  }
  &-day {
    font-size: 14px;
    color: rgb(130, 131, 135);

    @media screen and (max-width: 767px) {
      font-size: 12px;
    }
  }
  &-unknown {
    font-size: 14px;
    color: rgb(188, 189, 190);

    @media screen and (min-width: 768px) and (max-width: 1023px) {
      font-size: 12px;
      line-height: 0;
    }
  }
}
</style>

<style lang="scss" scoped>
.wpa-grid {
  //  display: flex;
  overflow-x: scroll;
  position: relative;
  .ctnr {
    max-width: initial;
    white-space: nowrap;
  }
}
.card-wrap {
  $offset: 0; //0.1;
  display: inline-block;
  white-space: initial;

  width: 280px;
  // class="col-4 col-lg-3 col-xl-2 px-[5px] py-2"
  // max-width: calc((100% - 16px) / #{3 + $offset});
  // padding-right: 8px;

  // @media (min-width: $breakpoint-sm-min) {
  //   max-width: calc((100% - 48px) / #{4 + $offset});
  //   padding-right: 24px;
  // }
  // @media (min-width: $breakpoint-md-min) {
  //   max-width: calc((100%-48px) / #{6 + $offset});
  //   padding-right: 24px;
  // }
  max-width: (100% / 3);
  padding: 0 4px;

  @media (min-width: $breakpoint-sm-min) {
    max-width: (100% / 4);
    padding: 0 14px;
  }

  @media (min-width: $breakpoint-md-min) {
    max-width: (100% / 6);
  }
}

.loader .wpa-grid {
  overflow-x: hidden;
}
</style>
