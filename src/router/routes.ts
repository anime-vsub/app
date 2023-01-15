import type { RouteRecordRaw } from "vue-router"

const routes: RouteRecordRaw[] = [
  {
    path: "/",
    component: () => import("pages/index_outlet.vue"),
    meta: {
      footer: true,
      forceScrollBehavior: true,
      screen: {
        name: "home",
        override: "Index",
      },
    },
    children: [
      {
        name: "index",
        path: "",
        component: () => import("pages/index.vue"),
      },
      {
        path: ":type_normal(anime-bo|anime-le|hoat-hinh-trung-quoc|anime-sap-chieu|anime-moi)",
        redirect(to) {
          return `/danh-sach/${to.params.type_normal}`
        },
      },
      {
        path: ":type_normal(danh-sach|the-loai|quoc-gia|tag)/:value",
        alias: [":type_normal(season)/:value(.+/.+)"],
        component: () => import("pages/[_type-normal]/[value].vue"),
        meta: {
          footer: false,
          screen: {
            name: "home",
            override: (to) =>
              to.params.type_normal +
              "/" +
              (Array.isArray(to.params.value)
                ? to.params.value.join("/")
                : to.params.value),
          },
        },
      },
      {
        path: "muc-luc",
        redirect: "/danh-sach/all",
      },
      {
        path: "lich-chieu-phim",
        component: () => import("pages/lich-chieu-phim.vue"),
        meta: {
          footer: false,
          screen: {
            name: "home",
            override: "Lich chieu phim",
          },
        },
      },
      {
        path: "bang-xep-hang/:type(day|voted|month|season|year)?",
        alias: ["bang-xep-hang/:type(day|voted|month|season|year)?.html"],
        component: () => import("pages/bang-xep-hang/[type]-.vue"),
        meta: {
          footer: false,
          screen: {
            name: "home",
            override: (to) => `bang-xep-hang/${to.params.type}`,
          },
        },
      },
    ],
  },
  {
    name: "search",
    path: "/tim-kiem/:keyword?",
    component: () => import("pages/tim-kiem/[keyword]-.vue"),
    meta: {
      footer: true,
      forceScrollBehavior: true,
      screen: {
        name: "search",
      },
    },
  },
  {
    name: "news",
    path: "/news",
    component: () => import("pages/news.vue"),
    meta: {
      footer: true,
      forceScrollBehavior: true,
      screen: {
        name: "news",
      },
    },
  },
  {
    name: "notification",
    path: "/notification",
    component: () => import("pages/notification.vue"),
    meta: {
      footer: true,
      forceScrollBehavior: true,
      screen: {
        name: "notification",
      },
    },
  },
  {
    path: "/tai-khoan",
    component: () => import("pages/tai-khoan/index_outlet.vue"),
    meta: {
      forceScrollBehavior: true,
      screen: {
        name: "account",
      },
    },
    children: [
      {
        name: "account",
        path: "",
        component: () => import("pages/tai-khoan/index.vue"),
        meta: {
          footer: true,
          screen: {
            name: "account",
          },
        },
      },
      {
        path: "edit-profile",
        component: () => import("pages/tai-khoan/edit-profile.vue"),
        meta: {
          screen: {
            name: "account",
            override: "editProfile",
          },
        },
      },
      {
        path: "history",
        component: () => import("pages/tai-khoan/history.vue"),
        meta: {
          screen: {
            name: "account",
            override: "history",
          },
        },
      },
      {
        path: "follow",
        component: () => import("pages/tai-khoan/follow.vue"),
        meta: {
          screen: {
            name: "account",
            override: "follow",
          },
        },
      },
      {
        path: "settings",
        component: () => import("pages/tai-khoan/settings/index_outlet.vue"),
        children: [
          {
            path: "",
            component: () => import("pages/tai-khoan/settings/index.vue"),
          },
          {
            path: "player",
            component: () => import("pages/tai-khoan/settings/player.vue"),
          },
        ],
        meta: {
          screen: {
            name: "account",
            override: "settings",
          },
        },
      },
      {
        path: "about",
        alias: ["/tai-khoan/settings/about"],
        component: () => import("pages/tai-khoan/about.vue"),
        meta: {
          screen: {
            name: "account",
            override: "about",
          },
        },
      },
    ],
  },

  // helpers

  {
    name: "watch-anime",
    path: "/phim/:season/:chapName(?:(.*\\)-)?:chap(\\d+)?", // [feature or defect]
    alias: ["/phim/:season/:chapName(\\0)?:chap(\\d+)?", "/phim/:season"],
    component: () => import("pages/phim/_season.vue"),
    meta: {
      screen: {
        name: "watching",
      },
    },
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    name: "not_found",
    path: "/:catchAll(.*)*",
    component: () => import("pages/ErrorNotFound.vue"),
    meta: {
      screen: {
        name: "not_found",
      },
    },
  },
]

export default routes
