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
import sha256 from "sha256"
import dayjs from "src/logic/dayjs"
import { addHostUrlImage } from "src/logic/urlImage"
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

  // eslint-disable-next-line camelcase
  const user_data = authStore.user_data

  // eslint-disable-next-line camelcase, functional/no-throw-statement
  if (!user_data) throw new Error("LOGIN_REQUIRED")

  const db = getFirestore(app)

  const historyRef = collection(
    db,
    "users",
    // eslint-disable-next-line camelcase
    sha256(user_data.email + user_data.name),
    "history"
  )
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

  return docs.map((item) => {
    const data = item.data()
    return {
      id: item.id,
      ...data,
      poster: addHostUrlImage(data.poster),
      timestamp: dayjs(data.timestamp.toDate()),
      rawTimestamp: data.timestamp,
    }
  }) as ItemData[]
}
