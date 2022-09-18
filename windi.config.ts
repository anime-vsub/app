import { defineConfig } from "windicss/helpers"
import lineClamp from "windicss/plugin/line-clamp"
import scrollSnap from "windicss/plugin/scroll-snap"

export default defineConfig({
  plugins: [lineClamp, scrollSnap],
})
