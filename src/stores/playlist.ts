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
  setDoc,
  startAfter,
  updateDoc,
} from "@firebase/firestore"
import { i18n } from "boot/i18n"
import { defineStore } from "pinia"
import { app } from "src/boot/firebase"
import { useFirestore } from "src/composibles/useFirestore"
import { computed } from "vue"

import { useAuthStore } from "./auth"

export const usePlaylistStore = defineStore("playlist", () => {
  const db = getFirestore(app)
  const authStore = useAuthStore()

  // eslint-disable-next-line no-use-before-define
  const playlists = useFirestore<Playlist_Playlist[]>(
    computed(() => {
      if (!authStore.uid) return null

      const userRef = doc(db, "users", authStore.uid)

      return query(collection(userRef, "playlist"), orderBy("created", "desc"))
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
    }) as unknown as any,
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    null as any
  )

  interface Playlist_Movies_Movie {
    name: string
    poster: string
    nameSeason: string
    chap: string
    nameChap: string
    add_at: Timestamp
  }
  interface Playlist_Playlist {
    id: string
    name: string
    description?: string
    public: string
    created: Timestamp
    updated?: Timestamp
    size: number
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
      collection(userRef, "playlist") as CollectionReference<
        Omit<Playlist_Playlist, "id">
      >,
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

  async function setDescriptionPlaylist(uid: string, description: string) {
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
        description,
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
    season: string,
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

    await setDoc(
      doc(
        playlistRef,
        "movies",
        `${season}`
      ) as DocumentReference<Playlist_Movies_Movie>,
      {
        ...anime,
        add_at: serverTimestamp(),
      },
      { merge: true }
    )
    await updateDoc(playlistRef, {
      size: increment(1),
      updated: serverTimestamp(),
    })
  }

  async function deleteAnimeFromPlaylist(uidPlaylist: string, season: string) {
    if (!authStore.uid)
      // eslint-disable-next-line functional/no-throw-statement
      throw new Error(
        i18n.global.t("errors.require_login_to", [
          i18n.global.t("theo-vao-danh-sach-phat"),
        ])
      )

    const userRef = doc(db, "users", authStore.uid)
    const playlistRef = doc(userRef, "playlist", uidPlaylist)

    await deleteDoc(doc(playlistRef, "movies", season))
    await updateDoc(playlistRef, {
      size: increment(-1),
      updated: serverTimestamp(),
    })
  }

  async function hasAnimeOfPlaylist(uidPlaylist: string, season: string) {
    if (!authStore.uid)
      // eslint-disable-next-line functional/no-throw-statement
      throw new Error(
        i18n.global.t("errors.require_login_to", [
          i18n.global.t("theo-vao-danh-sach-phat"),
        ])
      )

    const userRef = doc(db, "users", authStore.uid)
    const playlistRef = doc(userRef, "playlist", uidPlaylist)

    return getDoc(doc(playlistRef, "movies", `${season}`)).then((res) =>
      res.exists()
    )
  }

  async function getAnimesFromPlaylist(
    uidPlaylist: string,
    lastAnimeDoc?: QueryDocumentSnapshot<Playlist_Movies_Movie>,
    sorter?: "asc" | "desc"
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
        orderBy("add_at", sorter ?? "asc"),
        ...(lastAnimeDoc ? [startAfter(lastAnimeDoc)] : []),
        limit(20)
      )
    ).then((res) => res.docs)
  }

  async function getPosterPlaylist(idPlaylist: string) {
    if (!authStore.uid)
      // eslint-disable-next-line functional/no-throw-statement
      throw new Error(
        i18n.global.t("errors.require_login_to", [
          i18n.global.t("theo-vao-danh-sach-phat"),
        ])
      )

    const userRef = doc(db, "users", authStore.uid)

    return getDocs(
      query(
        collection(
          userRef,
          "playlist",
          idPlaylist,
          "movies"
        ) as CollectionReference<Playlist_Movies_Movie>,
        orderBy("add_at", "asc"),
        limit(1)
      )
    ).then((res) => res.docs?.[0].data().poster ?? null)
  }

  return {
    playlists,
    // getMetaPlaylist,
    createPlaylist,
    deletePlaylist,
    renamePlaylist,
    setDescriptionPlaylist,
    publicPlaylist,
    getPosterPlaylist,

    addAnimeToPlaylist,
    deleteAnimeFromPlaylist,
    hasAnimeOfPlaylist,

    getAnimesFromPlaylist,
  }
})
