import AutoImport from "unplugin-auto-import/vite"
import IconsResolver from "unplugin-icons/resolver"
import Icons from "unplugin-icons/vite"
import Components from "unplugin-vue-components/vite"
import { defineConfig } from "vitest/config"

export default defineConfig({
  test: {
    setupFiles: ["@vitest/web-worker"],
    environment: "jsdom",
    globals: true
  },
  plugins: [
    Icons(),
    Components({
      resolvers: [
        IconsResolver({
          prefix: "i"
        })
      ]
    }),
    AutoImport({
      imports: [
        "vue",
        "vue-router",
        { quasar: ["useQuasar"], "vue-i18n": ["useI18n"] }
      ],
      dts: "./auto-imports.d.ts",
      eslintrc: {
        enabled: true
      }
    })
  ]
})
