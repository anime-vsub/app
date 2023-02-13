import { i18n } from "src/boot/i18n"
import { ref, watch } from "vue"

const installed = ref<boolean>()
setTimeout(() => {
  if (!installed.value) installed.value = false
}, 5_0000)

// eslint-disable-next-line functional/no-let
let Http: Http
Object.defineProperty(window, "Http", {
  get() {
    // console.log("================= set Http ==================")
    return Http
  },
  set(value) {
    if (value !== undefined) installed.value = true

    Http = value
  },
})

const installedAsync = {
  get value() {
    return new Promise<void>((resolve, reject) => {
      if (installed.value) return resolve()
      if (installed.value === false)
        return reject(
          new Error(
            i18n.global.t(
              "trang-web-can-extension-animevsub-helper-de-hoat-dong-binh-thuong"
            )
          )
        )

      const watcher = watch(installed, (installed) => {
        watcher()
        if (installed) resolve()
        else
          reject(
            new Error(
              i18n.global.t(
                "trang-web-can-extension-animevsub-helper-de-hoat-dong-binh-thuong"
              )
            )
          )
      })
    })
  },
}

export { installed, installedAsync }
