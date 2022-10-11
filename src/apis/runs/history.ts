import { useAuthStore } from "stores/auth"

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
import { Icon } from "@iconify/vue"
import { app } from "boot/firebase"
import BottomBlur from "components/BottomBlur.vue"
import dayjs from "dayjs"
import isToday from "dayjs/plugin/isToday"
import isYesterday from "dayjs/plugin/isYesterday"
import { parseTime } from "src/logic/parseTime"
import { useAuthStore } from "stores/auth"
import { useRequest } from "vue-request"
import { useRouter } from "vue-router"

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

  // eslint-disable-next-line camelcase
  const historyRef = collection(db, "users", user_data.email, "history")
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
      timestamp: dayjs(data.timestamp.toDate()),
      rawTimestamp: data.timestamp,
    }
  }) as ItemData[]
}
