import type { Timestamp } from "@firebase/firestore"
import {
  collection,
  getDocs,
  getFirestore,
  limit,
  orderBy,
  query,
  where,
} from "@firebase/firestore"
import { app } from "boot/firebase"
import { i18n } from "src/boot/i18n"
import dayjs from "src/logic/dayjs"
import { useAuthStore } from "stores/auth"

interface ItemData {
  id: string
  first: string
  name: string
  poster: string
  seasonName: string
  timestamp: ReturnType<typeof dayjs>
  rawTimestamp: Timestamp
  last: {
    chap: string
    name: string
    cur: number
    dur: number
  }
}

export async function History(lastValue?: ItemData[]): Promise<ItemData[]> {
  const authStore = useAuthStore()

  if (!authStore.uid)
  // eslint-disable-next-line functional/no-throw-statement
    throw new Error(
      i18n.global.t(
        "errors.require_login_to",
        i18n.global.t("xem-lich-su-gan-day")
      )
    )

  const db = getFirestore(app)

  const historyRef = collection(db, "users", authStore.uid, "history")
  const q = query(
    historyRef,
    where("timestamp", "!=", null),
    ...(lastValue
      ? [where("timestamp", "<", lastValue[lastValue.length - 1].rawTimestamp)]
      : []),
    orderBy("timestamp", "desc"),
    limit(30)
  )

  const { docs } = await getDocs(q)

  return docs
    .map((item) => {
      const data = item.data()

      // eslint-disable-next-line array-callback-return
      if (!data.timestamp) return

      return {
        id: item.id,
        ...data,
        timestamp: dayjs(data.timestamp.toDate()),
        rawTimestamp: data.timestamp,
      }
    })
    .filter(Boolean) as ItemData[]
}
