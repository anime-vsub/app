// Import the functions you need from the SDKs you need
import { initializeApp } from "@firebase/app"
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
}

// Initialize Firebase
const app = initializeApp(firebaseConfig)

export { app }
