import { defineConfig } from "windicss/helpers"
import forms from "windicss/plugin/forms"
import lineClamp from "windicss/plugin/line-clamp"
import scrollSnap from "windicss/plugin/scroll-snap"

export default defineConfig({
  plugins: [lineClamp, scrollSnap, forms],
  safelist: ["children:!px-0", "!py-[6px]"],
  theme: {
    screens: {
      xs: { min: "0px", max: "599.99px" },
      sm: { min: "600px", max: "1023.99px" },
      md: { min: "1024px", max: "1439.99px" },
      lg: { min: "1440px", max: "1919.99px" },
      xl: { min: "1920px" },
    },
  },
})

// !bg-[rgba(0,194,52,.15)]
