import { i18n } from "boot/i18n"
import { Notify } from "quasar"
import { register } from "register-service-worker"
import { installedSW, updatingCache } from "src/logic/state-sw"
// The ready(), registered(), cached(), updatefound() and updated()
// events passes a ServiceWorkerRegistration instance in their arguments.
// ServiceWorkerRegistration: https://developer.mozilla.org/en-US/docs/Web/API/ServiceWorkerRegistration
register(process.env.SERVICE_WORKER_FILE, {
  // The registrationOptions object will be passed as the second argument
  // to ServiceWorkerContainer.register()
  // https://developer.mozilla.org/en-US/docs/Web/API/ServiceWorkerContainer/register#Parameter

  // registrationOptions: { scope: './' },

  ready(/* registration */) {
    console.log("Service worker is active.")
    installedSW.value = true
  },

  registered(/* registration */) {
    console.log("Service worker has been registered.")
    installedSW.value = true
  },

  cached(/* registration */) {
    console.log("Content has been cached for offline use.")
    updatingCache.value = false
  },

  updatefound(/* registration */) {
    console.log("New content is downloading.")
    updatingCache.value = true
  },

  updated(/* registration */) {
    console.log("New content is available; please refresh.")
    updatingCache.value = false
    Notify.create({
      message: i18n.global.t("ung-dung-da-duoc-cap-nhat"),
      position: "bottom-right",
      timeout: 5000,
      actions: [
        {
          label: i18n.global.t("lam-moi-ngay"),
          color: "main",
          noCaps: true,
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          handler: () => (location as unknown as any).reload()
        },
        {
          label: i18n.global.t("de-sau"),
          noCaps: true
        }
      ]
    })
  },

  offline() {
    // console.log('No internet connection found. App is running in offline mode.')
  },

  error(err) {
    console.error("Error during service worker registration:", err)
  }
})
