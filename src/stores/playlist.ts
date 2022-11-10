import type {
  CollectionReference,
  DocumentReference,
  QueryDocumentSnapshot,
  Timestamp,
} from "@firebase/firestore"
import {
  addDoc,
  collection,
  deleteDoc,
  doc,
  getDoc,
  getDocs,
  getFirestore,
  increment,
  limit,
  orderBy,
  query,
  serverTimestamp,
  startAfter,
  updateDoc,
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
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        public: isPublic as any,
        created: serverTimestamp(),
        size: 0,
      }
    )
  }

  async function deletePlaylist(uid: string) {
    if (!authStore.uid)
      // eslint-disable-next-line functional/no-throw-statement
      throw new Error(
        i18n.global.t("errors.require_login_to", [
          i18n.global.t("xoa-danh-sach-phat"),
        ])
      )

    const userRef = doc(db, "users", authStore.uid)

    return deleteDoc(doc(userRef, "playlist", uid))
  }

  async function renamePlaylist(uid: string, newName: string) {
    if (!authStore.uid)
      // eslint-disable-next-line functional/no-throw-statement
      throw new Error(
        i18n.global.t("errors.require_login_to", [
          i18n.global.t("xoa-danh-sach-phat"),
        ])
      )

    const userRef = doc(db, "users", authStore.uid)

    return updateDoc(
      doc(userRef, "playlist", uid) as DocumentReference<Playlist_Playlist>,
      {
        name: newName,
      }
    )
  }

  async function publicPlaylist(uid: string, isPublic: boolean) {
    if (!authStore.uid)
      // eslint-disable-next-line functional/no-throw-statement
      throw new Error(
        i18n.global.t("errors.require_login_to", [
          i18n.global.t("xoa-danh-sach-phat"),
        ])
      )

    const userRef = doc(db, "users", authStore.uid)

    return updateDoc(
      doc(userRef, "playlist", uid) as DocumentReference<Playlist_Playlist>,
      {
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        public: isPublic as any,
      }
    )
  }

  // prv
  async function addAnimeToPlaylist(
    uidPlaylist: string,
    anime: Omit<Playlist_Movies_Movie, "add_at">
  ) {
    if (!authStore.uid)
      // eslint-disable-next-line functional/no-throw-statement
      throw new Error(
        i18n.global.t("errors.require_login_to", [
          i18n.global.t("theo-vao-danh-sach-phat"),
        ])
      )

    const userRef = doc(db, "users", authStore.uid)
    const playlistRef = doc(userRef, "playlist", uidPlaylist)

    // check playlist exists?
    if (!(await getDoc(playlistRef)).exists())
      // eslint-disable-next-line functional/no-throw-statement
      throw new Error(i18n.global.t("errors.danh-sach-phat-khong-ton-tai"))

    await addDoc(
      collection(
        playlistRef,
        "movies"
      ) as CollectionReference<Playlist_Movies_Movie>,
      {
        ...anime,
        add_at: serverTimestamp(),
      }
    )
    await updateDoc(playlistRef, {
      size: increment(1),
    })
  }

  async function deleteAnimeFromPlaylist(
    uidPlaylist: string,
    uidAnime: string
  ) {
    if (!authStore.uid)
      // eslint-disable-next-line functional/no-throw-statement
      throw new Error(
        i18n.global.t("errors.require_login_to", [
          i18n.global.t("theo-vao-danh-sach-phat"),
        ])
      )

    const userRef = doc(db, "users", authStore.uid)
    const playlistRef = doc(userRef, "playlist", uidPlaylist)

    await deleteDoc(doc(playlistRef, "movies", uidAnime))
    await updateDoc(playlistRef, {
      size: increment(-1),
    })
  }

  async function getAnimesFromPlaylist(
    uidPlaylist: string,
    lastAnimeDoc: QueryDocumentSnapshot<Playlist_Movies_Movie>
  ) {
    if (!authStore.uid)
      // eslint-disable-next-line functional/no-throw-statement
      throw new Error(
        i18n.global.t("errors.require_login_to", [
          i18n.global.t("theo-vao-danh-sach-phat"),
        ])
      )

    const userRef = doc(db, "users", authStore.uid)
    const playlistRef = doc(userRef, "playlist", uidPlaylist)

    return getDocs(
      query(
        collection(
          playlistRef,
          "movies"
        ) as CollectionReference<Playlist_Movies_Movie>,
        orderBy("add_at", "asc"),
        startAfter(lastAnimeDoc)
      )
    ).then((res) => res.docs)
  }

  return {
    playlists,
    getMetaPlaylist,
    createPlaylist,
    deletePlaylist,
    renamePlaylist,
    publicPlaylist,

    addAnimeToPlaylist,
    deleteAnimeFromPlaylist,

    getAnimesFromPlaylist,
  }
})
