import type {
  CollectionReference,
  DocumentReference,
  DocumentSnapshot,
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
import { defineStore } from "pinia"
import { app } from "src/boot/firebase"
import { useFirestore } from "src/composibles/useFirestore"
import dayjs from "src/logic/dayjs"
import { getRealSeasonId } from "src/logic/getRealSeasonId"
import { addHostUrlImage, removeHostUrlImage } from "src/logic/urlImage"
import { computed, ref } from "vue"

import { useAuthStore } from "./auth"

const isCryptoReady = typeof crypto !== "undefined" // firefox not exists crypto
const generateUUID = isCryptoReady
  ? () => crypto.randomUUID()
  : () => {
      return parseInt(Math.random().toString().replace(".", "")).toString(34)
    }

function isToday(date?: Date) {
  if (!date) return false

  return dayjs(date).isToday()
}

export const useHistoryStore = defineStore("history", () => {
  const db = getFirestore(app)
  const authStore = useAuthStore()

  interface HistoryItem {
    name: string
    poster: string
    season: string
    seasonName: string

    last?: {
      /** @type : is a id chap. (e.g: 1132, 12345) */
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
  const [_last30Item, refreshLast30Item] = useFirestore<
    Required<
      HistoryItem & {
        id: string
      }
    >[]
  >(
    computed(() => {
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
  const last30Item = computed(() =>
    _last30Item.value?.map((item) => {
      item.poster = addHostUrlImage(item.poster)
      return item
    })
  )

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
          poster: addHostUrlImage(data.poster),
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
          i18n.global.t("luu-tien-trinh-xem-season-moi"),
        ])
      )

    const seasonRef = doc(
      doc(db, "users", authStore.uid),
      "history",
      getRealSeasonId(seasonId)
    ) as DocumentReference<HistoryItem>

    const snap = await getDoc(seasonRef)
    if (!snap.exists() || snap.data().season !== seasonId)
      await setDoc(seasonRef, {
        season: seasonId,
        ...info,
        poster: removeHostUrlImage(info.poster),
      })
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
        i18n.global.t("errors.require_login_to", [
          i18n.global.t("luu-lich-su-xem"),
        ])
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
      // TODO: can't where after orderBy
      // queue task this up function
      getDocs(
        query(
          seasonRef.parent,
          where("timestamp", "!=", null),
          orderBy("timestamp", "desc"),
          limit(1)
        )
      )
        // update progress and seasonRef put down
        .then(async ({ docs, size }) => {
          // this is old data. not conflict data with save in previous then
          // eslint-disable-next-line functional/no-let
          let oldData: DocumentSnapshot<Required<HistoryItem>> | null = null

          if (
            size !== 0 &&
            ((docs[0].id !== realSeason &&
              !docs[0].id.endsWith(`#${realSeason}`)) ||
              !isToday(docs[0].data().timestamp?.toDate()))
          ) {
            oldData = await getDoc(seasonRef)
          }
          // update to pre-read on history (indexed faster)

          // const batch = writeBatch(db)
          await Promise.all([
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
            ),
            (async () => {
          // create fake data replace fix #70
          if (oldData?.exists()) {
            // clone now
            const data = oldData.data()
            // save by buff diff

            const seasonRefOldData = doc(
              seasonRef.parent,
              `${generateUUID()}#${realSeason}`
            )

                return setDoc(
              seasonRefOldData,
              {
                ...data,
                poster: removeHostUrlImage(data.poster),
              },
              { merge: true }
            )
          }
            })(),
          ])
        })

        .catch((err) => {
          console.error("error with progress getDocs", err)
        }),
      // update to progress watch chaps, don't worry
      setDoc(chapRef, info, { merge: true }).catch((err) =>
        console.error("update progress error", err)
      ),
    ])
  }

  async function getLastEpOfSeason(season: string): Promise<null | string> {
    if (!authStore.uid)
      // eslint-disable-next-line functional/no-throw-statement
      throw new Error(
        i18n.global.t("errors.require_login_to", [
          i18n.global.t("xem-lich-su-gan-day"),
        ])
      )

    const data = await getDoc(
      doc(
        db,
        "users",
        authStore.uid,
        "history",
        getRealSeasonId(season)
      ) as DocumentReference<HistoryItem>
    ).then((res) => res.data())

    return data?.last?.chap ?? null
  }

  return {
    last30Item,
    last30ItemError,
    refreshLast30Item,
    loadMoreAfter,

    createSeason,

    getProgressChaps,
    getProgressChap,
    setProgressChap,

    getLastEpOfSeason,
  }
})
