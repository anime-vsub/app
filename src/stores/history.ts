import type {
  CollectionReference,
  DocumentReference,
  QueryDocumentSnapshot,
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
  serverTimestamp,
  setDoc,
  startAfter,
  where,
} from "@firebase/firestore"
import { i18n } from "boot/i18n"
import dayjs from "dayjs"
import { defineStore } from "pinia"
import { app } from "src/boot/firebase"
import { useFirestore } from "src/composibles/useFirestore"
import { getRealSeasonId } from "src/logic/getRealSeasonId"
import { computed, ref } from "vue"

import { useAuthStore } from "./auth"

export const useHistoryStore = defineStore("history", () => {
  const db = getFirestore(app)
  const authStore = useAuthStore()

  interface HistoryItem {
    name: string
    poster: string
    season: string
    seasonName: string

    last?: {
      chap: string
      cur: number
      dur: number
      name: string
    }
    timestamp?: Timestamp // set along with last
  }
  interface HistoryItem_ChapItem {
    cur: number
    dur: number
    name: string
  }

  const last30ItemError = ref<Error | null>(null)
  const _countRefeshLast30Item = ref(0)
  const last30Item = useFirestore<
    Required<
      HistoryItem & {
        id: string
      }
    >[]
  >(
    computed(() => {
      // eslint-disable-next-line no-unused-expressions
      _countRefeshLast30Item.value

      last30ItemError.value = null
      if (!authStore.uid) return null

      return query(
        collection(db, "users", authStore.uid, "history"),
        where("timestamp", "!=", null),
        orderBy("timestamp", "desc"),
        limit(30)
      )
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
    }) as unknown as any,
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    null as any,
    {
      errorHandler(err) {
        last30ItemError.value = err
      },
    }
  )
  function refreshLast30ItemError() {
    _countRefeshLast30Item.value++
  }

  function loadMoreAfter(
    lastDoc?: QueryDocumentSnapshot<Required<HistoryItem>>
  ) {
    if (!authStore.uid)
      // eslint-disable-next-line functional/no-throw-statement
      throw new Error(
        i18n.global.t("errors.require_login_to", [
          i18n.global.t("xem-lich-su-gan-day"),
        ])
      )

    return getDocs(
      query(
        collection(
          db,
          "users",
          authStore.uid,
          "history"
        ) as CollectionReference<Required<HistoryItem>>,
        where("timestamp", "!=", null),
        orderBy("timestamp", "desc"),
        ...(lastDoc ? [startAfter(lastDoc)] : []),
        limit(30)
      )
    ).then(({ docs }) =>
      docs.map((doc) => {
        const data = doc.data()
        return {
          id: doc.id,
          ...data,
          timestamp: dayjs(data.timestamp?.toDate()),
          $doc: doc,
        }
      })
    )
  }

  async function createSeason(
    seasonId: string,
    info: Omit<HistoryItem, "timestamp" | "season">
  ) {
    if (!authStore.uid)
      // eslint-disable-next-line functional/no-throw-statement
      throw new Error(
        i18n.global.t("errors.require_login_to", [
          "lưu tiến trình xem season mới",
        ])
      )

    const seasonRef = doc(
      doc(db, "users", authStore.uid),
      "history",
      getRealSeasonId(seasonId)
    ) as DocumentReference<HistoryItem>

    if (!(await getDoc(seasonRef)).exists())
      await setDoc(seasonRef, { season: seasonId, ...info })
  }

  // children /chaps/:chap
  function getProgressChaps(season: string) {
    if (!authStore.uid)
      // eslint-disable-next-line functional/no-throw-statement
      throw new Error(
        i18n.global.t("errors.require_login_to", [
          i18n.global.t("xem-lich-su-gan-day"),
        ])
      )

    return getDocs(
      collection(
        db,
        "users",
        authStore.uid,
        "history",
        getRealSeasonId(season),
        "chaps"
      ) as CollectionReference<HistoryItem_ChapItem>
    ).then(({ docs }) => docs)
  }
  function getProgressChap(season: string, chap: string) {
    if (!authStore.uid)
      // eslint-disable-next-line functional/no-throw-statement
      throw new Error(
        i18n.global.t("errors.require_login_to", [
          i18n.global.t("xem-lich-su-gan-day"),
        ])
      )

    return getDoc(
      doc(
        db,
        "users",
        authStore.uid,
        "history",
        getRealSeasonId(season),
        "chaps",
        chap
      ) as DocumentReference<HistoryItem_ChapItem>
    ).then((res) => res.data())
  }
  function setProgressChap(
    season: string,
    chap: string,
    info: HistoryItem_ChapItem
  ) {
    if (!authStore.uid)
      // eslint-disable-next-line functional/no-throw-statement
      throw new Error(
        i18n.global.t("errors.require_login_to", ["lưu lịch sử xem"])
      )

    const realSeason = getRealSeasonId(season)

    const seasonRef = doc(
      db,
      "users",
      authStore.uid,
      "history",
      realSeason
    ) as DocumentReference<Required<HistoryItem>>
    const chapRef = doc(
      seasonRef,
      "chaps",
      chap
    ) as DocumentReference<HistoryItem_ChapItem>

    return Promise.all([
      setDoc(
        seasonRef,
        {
          timestamp: serverTimestamp(),
          season,
          last: {
            chap,
            ...info,
          },
        },
        { merge: true }
      ).catch((err) => console.error("update time error", err)),
      setDoc(chapRef, info, { merge: true }).catch((err) =>
        console.error("update progress error", err)
      ),
    ])
  }

  return {
    last30Item,
    last30ItemError,
    refreshLast30ItemError,
    loadMoreAfter,

    createSeason,

    getProgressChaps,
    getProgressChap,
    setProgressChap,
  }
})
