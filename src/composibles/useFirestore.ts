import type {
  DocumentData,
  DocumentReference,
  DocumentSnapshot,
  Query,
  QueryDocumentSnapshot
} from "@firebase/firestore"
import { onSnapshot } from "@firebase/firestore"
import type { MaybeRef } from "@vueuse/shared"
import { isDef, tryOnScopeDispose } from "@vueuse/shared"
import { WARN } from "src/constants"
import type { Ref } from "vue"
import { computed, isRef, ref, watch } from "vue"

// eslint-disable-next-line functional/no-mixed-type
export interface UseFirestoreOptions {
  errorHandler?: (err: Error) => void
  autoDispose?: boolean
}

export type FirebaseDocRef<T> = Query<T> | DocumentReference<T>

function getData<T>(docRef: DocumentSnapshot<T> | QueryDocumentSnapshot<T>) {
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const data = docRef.data()!

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  ;(data as any).id = docRef.id

  return data
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
function isDocumentReference<T>(docRef: any): docRef is DocumentReference<T> {
  return (docRef.path?.match(/\//g) || []).length % 2 !== 0
}

export function useFirestore<T extends DocumentData>(
  maybeDocRef: MaybeRef<DocumentReference<T>>,
  initialValue: T,
  options?: UseFirestoreOptions
): [Ref<T | null>, () => void]
export function useFirestore<T extends DocumentData>(
  maybeDocRef: MaybeRef<Query<T>>,
  initialValue: T[],
  options?: UseFirestoreOptions
): [Ref<T[]>, () => void]

// nullable initial values
export function useFirestore<T extends DocumentData>(
  maybeDocRef: MaybeRef<DocumentReference<T>>,
  initialValue?: T | undefined,
  options?: UseFirestoreOptions
): [Ref<T | undefined | null>, () => void]
export function useFirestore<T extends DocumentData>(
  maybeDocRef: MaybeRef<Query<T>>,
  initialValue?: T[],
  options?: UseFirestoreOptions
): [Ref<T[] | undefined>, () => void]

/**
 * Reactive Firestore binding. Making it straightforward to always keep your
 * local data in sync with remotes databases.
 *
 * @see https://vueuse.org/useFirestore
 */
export function useFirestore<T extends DocumentData>(
  maybeDocRef: MaybeRef<FirebaseDocRef<T>>,
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  initialValue: any = undefined,
  options: UseFirestoreOptions = {}
): [Ref<unknown>, () => void] {
  const { errorHandler = WARN, autoDispose = true } = options

  const refOfDocRef = (
    isRef(maybeDocRef) ? maybeDocRef : computed(() => maybeDocRef)
  ) as Ref<FirebaseDocRef<T>>

  let initd = false

  // eslint-disable-next-line @typescript-eslint/no-empty-function
  let close = () => {}
  const data = ref() as Ref<T | T[] | null>

  function run(docRef: FirebaseDocRef<T>) {
    if (!initd) return

    close()
    if (!refOfDocRef.value) {
      data.value = initialValue
    } else if (isDocumentReference<T>(refOfDocRef.value)) {
      close = onSnapshot(
        docRef as DocumentReference<T>,
        (snapshot) => {
          data.value = getData(snapshot) || null
        },
        errorHandler
      )
    } else {
      close = onSnapshot(
        docRef as Query<T>,
        (snapshot) => {
          data.value = snapshot.docs.map(getData).filter(isDef)
        },
        errorHandler
      )
    }
  }

  watch(refOfDocRef, run)

  // watch(refOfDocRef, run)

  if (autoDispose) {
    tryOnScopeDispose(() => {
      close()
    })
  }

  function refresh() {
    run(refOfDocRef.value)
  }

  return [
    computed(() => {
      if (!initd) {
        initd = true
        refresh()
      }
      return data.value
    }),
    refresh
  ]
}
