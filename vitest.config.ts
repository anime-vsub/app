import { defineConfig } from "vitest/config"
import AutoImport from "unplugin-auto-import/vite"
import IconsResolver from "unplugin-icons/resolver"
import Icons from "unplugin-icons/vite"
import Components from "unplugin-vue-components/vite"

export default defineConfig({
  test: {
    environment: "jsdom",
    globals: true,
  },
  plugins: [
    Icons(),
    Components({
      globs: [],
      resolvers: [
        IconsResolver({
          prefix: "i",
        }),
      ],
    }),
    AutoImport({
      imports: [
        "vue",
        "vue-router",
        { quasar: ["useQuasar"], "vue-i18n": ["useI18n"] },
      ],
      dts: "./auto-imports.d.ts",
      eslintrc: {
        enabled: true,
      },
    }),
  ],
})
