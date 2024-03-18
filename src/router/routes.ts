import type { RouteRecordRaw } from "vue-router"

const routes: RouteRecordRaw[] = [
  {
    path: "/:mainPath(.*)*/trang-:page(\\d+)",
    redirect(to) {
      return `/${(to.params.mainPath as string[]).join("/")}?page=${
        to.params.page
      }`
    }
  },
  {
    path: "/",
    component: () => import("pages/index_outlet.vue"),
    meta: {
      footer: true,
      forceScrollBehavior: true
    },
    children: [
      {
        name: "index",
        path: "",
        component: () => import("pages/index.vue"),
        meta: {
          hideDrawer: true,
          transparentHeader: true,
          styleFn(offset, height) {
            return {
              height: height + "px",
              marginTop: -offset + "px"
            }
          }
        }
      },
      {
        path: ":type_normal(anime-bo|anime-le|hoat-hinh-trung-quoc|anime-sap-chieu|anime-moi)",
        redirect(to) {
          return `/danh-sach/${to.params.type_normal}`
        }
      },
      {
        path: ":type_normal(danh-sach|the-loai|quoc-gia|tag)/:value",
        alias: [":type_normal(season)/:value(.+/.+)"],
        component: () => import("pages/[_type-normal]/[value].vue"),
        meta: {
          footer: false
        }
      },
      {
        path: "muc-luc",
        redirect: "/danh-sach/all"
      },
      {
        path: "lich-chieu-phim",
        component: () => import("pages/lich-chieu-phim.vue"),
        meta: {
          footer: false
        }
      },
      {
        path: "bang-xep-hang/:type(day|voted|month|season|year)?",
        component: () => import("pages/bang-xep-hang/[type]-.vue"),
        meta: {
          footer: false
        }
      },
      {
        path: "playlist/:playlist",
        component: () => import("pages/playlist/[playlist].vue"),
        meta: {
          footer: false
        }
      },
      {
        path: "feed",
        component: () => import("pages/feed.vue"),
        meta: {
          footer: false
        }
      }
    ]
  },
  {
    name: "search",
    path: "/tim-kiem/:keyword?",
    component: () => import("pages/tim-kiem/[keyword]-.vue"),
    meta: {
      footer: true,
      forceScrollBehavior: true
    }
  },
  {
    name: "news",
    path: "/news",
    component: () => import("pages/news.vue"),
    meta: {
      footer: true,
      forceScrollBehavior: true
    }
  },
  {
    name: "notification",
    path: "/notification",
    component: () => import("pages/notification.vue"),
    meta: {
      footer: true,
      forceScrollBehavior: true
    }
  },
  {
    path: "/tai-khoan",
    component: () => import("pages/tai-khoan/index_outlet.vue"),
    meta: {
      forceScrollBehavior: true
    },
    children: [
      {
        name: "account",
        path: "",
        component: () => import("pages/tai-khoan/index.vue"),
        meta: {
          footer: true
        }
      },
      {
        path: "edit-profile",
        component: () => import("pages/tai-khoan/edit-profile.vue")
      },
      {
        path: "history",
        component: () => import("pages/tai-khoan/history.vue")
      },
      {
        path: "follow",
        component: () => import("pages/tai-khoan/follow.vue")
      },
      {
        path: "settings",
        component: () => import("pages/tai-khoan/settings/index_outlet.vue"),
        children: [
          {
            path: "",
            component: () => import("pages/tai-khoan/settings/index.vue")
          },
          {
            path: "player",
            component: () => import("pages/tai-khoan/settings/player.vue")
          }
        ]
      }
    ]
  },

  // helpers

  {
    name: "watch-anime",
    path: "/phim/:season/:chapName(.+)?-:chap(\\d+)", // [feature or defect]
    alias: ["/phim/:season/:chapName(\\0)?:chap(\\d+)", "/phim/:season"],
    component: () => import("pages/phim/_season.vue"),
    meta: {
      hideDrawer: true
    }
  },

  {
    name: "task-manager",
    path: "/task-manager",
    component: () => import("pages/task-manager.vue")
  },

  // try remove .html after url
  {
    path: "/:catchAll(.*)*.html",
    redirect(to) {
      console.log(to)
      return `/${(to.params.catchAll as string[]).join("/")}`
    }
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    name: "not_found",
    path: "/:catchAll(.*)*",
    component: () => import("pages/ErrorNotFound.vue")
  }
]

export default routes
