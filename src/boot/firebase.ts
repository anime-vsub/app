// Import the functions you need from the SDKs you need
import { FirebaseAnalytics } from "@capacitor-community/firebase-analytics"
import type { CustomParams, Item } from "@firebase/analytics"
import {
  getAnalytics,
  logEvent as logPWA,
  setUserId as setUserIdPWA,
  setUserProperties as setUserPropertiesPWA,
} from "@firebase/analytics"
import { initializeApp } from "@firebase/app"
import type { Index } from "@firebase/firestore"
import {
  CACHE_SIZE_UNLIMITED,
  initializeFirestore,
  persistentLocalCache,
  persistentMultipleTabManager,
  setIndexConfiguration,
} from "@firebase/firestore"
import { isNative } from "src/constants"
import configure from "app/firebase/firestore.indexes.json"
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
const firebaseConfig = {
  apiKey: "AIzaSyB4ouSBjkMUnV6cN2XVlc5PxJtf6gVsRkQ",
  authDomain: "animevsub-history-app.firebaseapp.com",
  projectId: "animevsub-history-app",
  storageBucket: "animevsub-history-app.appspot.com",
  messagingSenderId: "12658633187",
  appId: "1:12658633187:web:56cf1588279277f3a698d6",
  measurementId: "G-F2KJ27SHYK",
}

// Initialize Firebase
const app = initializeApp(firebaseConfig)
const db = initializeFirestore(app, {
  localCache: persistentLocalCache({
    cacheSizeBytes: CACHE_SIZE_UNLIMITED,
    tabManager: persistentMultipleTabManager(),
  }),
})
setIndexConfiguration(
  db,
  configure as {
    indexes: Index[]
  }
)
  // eslint-disable-next-line promise/always-return
  .then(() => {
    console.log("[Install indexes]: Installed indexes")
  })
  .catch((err) => {
    console.error("[Install indexes]: failure ", err)
  })

export { app, db }

const analytics = isNative ? null : getAnalytics()

export const logEvent = isNative
  ? (name: string, params: object = {}) =>
      FirebaseAnalytics.logEvent({ name, params })
  : (
      name: string,
      params?: {
        [key: string]: any
        coupon?: string | undefined
        currency?: string | undefined
        items?: Item[] | undefined
        payment_type?: string | undefined
        value?: number | undefined
      }
    ) => {
      if (analytics) {
        logPWA(analytics, name, params)
      }
    }

export const setScreenName = isNative
  ? FirebaseAnalytics.setScreenName
  : () => {}
export const setUserProperty = isNative
  ? FirebaseAnalytics.setUserProperty
  : (options: CustomParams) => {
      setUserPropertiesPWA(analytics!, options)
    }
export const setUserId = isNative
  ? FirebaseAnalytics.setUserId
  : (id: string | null) => {
      setUserIdPWA(analytics!, id)
    }
