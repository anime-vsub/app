import AutoImport from "unplugin-auto-import/vite"
import IconsResolver from "unplugin-icons/resolver"
import Icons from "unplugin-icons/vite"
import Components from "unplugin-vue-components/vite"
import { defineConfig } from "vitest/config"

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
        {
          quasar: ["useQuasar"],
          "vue-i18n": ["useI18n"],
          // "@vueuse/core": ["computedAsync"],
        },
      ],
      dirs: ["./src/*.ts", "./src/composibles"],
      eslintrc: {
        enabled: true,
      },
    }),
  ],
})
