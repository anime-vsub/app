import {
  addDoc,
  CollectionReference,
  DocumentReference,
  FieldValue,
  serverTimestamp,
  Timestamp,
} from "@firebase/firestore"
import {
  collection,
  doc,
  getDoc,
  getDocs,
  getFirestore,
  limit,
  orderBy,
  query,
} from "@firebase/firestore"
import { useFirestore } from "@vueuse/firebase"
import { i18n } from "boot/i18n"
import { defineStore } from "pinia"
import { app } from "src/boot/firebase"
import { computed } from "vue"

import { useAuthStore } from "./auth"

export const usePlaylistStore = defineStore("playlist", () => {
  const db = getFirestore(app)
  const authStore = useAuthStore()

  const playlists = computed(() => {
    // @Error
    if (!authStore.uid) return null

    const userRef = doc(db, "users", authStore.uid)

    return (
      useFirestore<{
        name: string
        public: boolean
        created: Timestamp
      }>(query(collection(userRef, "playlist"), orderBy("created", "desc"))) ??
      null
    )
  })

  interface Playlist_Movies_Movie {
    name: string
    poster: string
    season: string
    chap: string
    nameChap: string
    add_at: Timestamp
  }
  interface Playlist_Playlist {
    name: string
    public: string
    created: Timestamp
    size: number
  }

  async function getMetaPlaylist(uid: string) {
    if (!authStore.uid)
      // eslint-disable-next-line functional/no-throw-statement
      throw new Error(
        i18n.global.t("errors.require_login_to", [
          i18n.global.t("xem-danh-sach-phat"),
        ])
      )

    const userRef = doc(db, "users", authStore.uid)

    const [meta, firstMovie] = await Promise.all([
      getDoc(
        doc(userRef, "playlist", uid) as DocumentReference<Playlist_Playlist>
      ).then((res) => res.data()),
      getDocs(
        query(
          collection(
            userRef,
            "playlist"
          ) as CollectionReference<Playlist_Movies_Movie>,
          orderBy("created", "desc"),
          limit(1)
        )
      ).then((res) => res.docs?.[0].data()),
    ])

    if (!meta) return null

    return {
      ...meta,
      first: firstMovie,
    }
  }

  async function createPlaylist(name: string, isPublic: boolean) {
    if (!authStore.uid)
      // eslint-disable-next-line functional/no-throw-statement
      throw new Error(
        i18n.global.t("errors.require_login_to", [
          i18n.global.t("tao-danh-sach-phat"),
        ])
      )

    const userRef = doc(db, "users", authStore.uid)

    return addDoc(
      collection(userRef, "playlist") as CollectionReference<Playlist_Playlist>,
      {
        name,
        public: (isPublic) as any,
        created: serverTimestamp(),
        size: 0
      }
    )
  }

  function deletePlaylist()

  return { playlists, getMetaPlaylist, createPlaylist }
})
