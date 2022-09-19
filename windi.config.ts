import { defineConfig } from "windicss/helpers"
import lineClamp from "windicss/plugin/line-clamp"
import scrollSnap from "windicss/plugin/scroll-snap"
import forms from "windicss/plugin/forms"

export default defineConfig({
  plugins: [lineClamp, scrollSnap, forms],
})
