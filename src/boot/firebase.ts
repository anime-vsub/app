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
import { isNative } from "src/constants"
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

export { app }

const analytics = isNative ? null : getAnalytics()

export const logEvent = isNative
  ? (name: string, params: object = {}) =>
      FirebaseAnalytics.logEvent({ name, params })
  : (
      name: string,
      params?: {
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        [key: string]: any
        coupon?: string | undefined
        currency?: string | undefined
        items?: Item[] | undefined
        payment_type?: string | undefined
        value?: string | undefined
      }
    ) => {
      if (analytics) {
        logPWA(analytics, name, params)
      }
    }

export const setScreenName = isNative
  ? FirebaseAnalytics.setScreenName
  : // eslint-disable-next-line @typescript-eslint/no-empty-function
    () => {}
export const setUserProperty = isNative
  ? FirebaseAnalytics.setUserProperty
  : (options: CustomParams) => {
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      setUserPropertiesPWA(analytics!, options)
    }
export const setUserId = isNative
  ? // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    (id: string | null) => FirebaseAnalytics.setUserId({ userId: id! })
  : (id: string | null) => {
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      setUserIdPWA(analytics!, id)
    }
