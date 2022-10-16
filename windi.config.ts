import { defineConfig } from "windicss/helpers"
import forms from "windicss/plugin/forms"
import lineClamp from "windicss/plugin/line-clamp"
import scrollSnap from "windicss/plugin/scroll-snap"

export default defineConfig({
  plugins: [lineClamp, scrollSnap, forms],
  safelist: "children:!px-0"
})

// !bg-[rgba(0,194,52,.15)]
