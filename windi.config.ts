import { defineConfig } from "windicss/helpers"
import forms from "windicss/plugin/forms"
import lineClamp from "windicss/plugin/line-clamp"
import scrollSnap from "windicss/plugin/scroll-snap"

export default defineConfig({
  plugins: [lineClamp, scrollSnap, forms],
})

// !bg-[rgba(0,194,52,.15)]
