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
      if (installed.value) {
        if (!Http.version || Http.version < "0.0.21")
          reject(
            Object.assign(
              new Error(
                i18n.global.t(
                  "phien-ban-extension-cua-ban-da-qua-cu-hay-cap-nhat-no-de-tiep-tuc"
                )
              ),
              { requireUpdate: true }
            )
          )
        else resolve()

        return
      }
      if (installed.value === false)
        return reject(
          Object.assign(
            new Error(
              i18n.global.t(
                "trang-web-can-extension-animevsub-helper-de-hoat-dong-binh-thuong"
              )
            ),
            { extesionNotExists: true }
          )
        )

      const watcher = watch(installed, (installed) => {
        watcher()
        if (installed) {
          if (!Http.version || Http.version < "0.0.21")
            reject(
              Object.assign(
                new Error(
                  i18n.global.t(
                    "phien-ban-extension-cua-ban-da-qua-cu-hay-cap-nhat-no-de-tiep-tuc"
                  )
                ),
                { requireUpdate: true }
              )
            )
          else resolve()
        } else
          reject(
            Object.assign(
              new Error(
                i18n.global.t(
                  "trang-web-can-extension-animevsub-helper-de-hoat-dong-binh-thuong"
                )
              ),
              { extesionNotExists: true }
            )
          )
      })
    })
  },
}

export { installed, installedAsync }
