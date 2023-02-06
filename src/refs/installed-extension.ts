import { i18n } from "src/boot/i18n"
import { ref } from "vue"

const installed = ref<boolean>()

const installedAsync = new Promise<void>((resolve, reject) => {
  if (typeof window.Http !== "undefined") {
    installed.value = true
  } else {
    // eslint-disable-next-line functional/no-let
    let counter = 0
    const interval = setInterval(() => {
      const is = typeof window.Http !== "undefined"
      if (is) {
        installed.value = true
        clearInterval(interval)
        resolve()
        return
      }

      counter++

      if (counter >= 2 /* 3 seconds */) {
        installed.value = is
        clearInterval(interval)
        reject(
          i18n.global.t(
            "trang-web-can-extension-animevsub-helper-de-hoat-dong-binh-thuong"
          )
        )
      }
    }, 1000)
  }
})

export { installed, installedAsync }
