import { defineConfig } from "vitest/config"

export default defineConfig({
  test: {
    setupFiles: ["@vitest/web-worker"],
    environment: "jsdom",
    globals: true
  }
})
