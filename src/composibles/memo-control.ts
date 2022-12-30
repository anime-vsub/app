import type { ComputedRef, Ref } from "vue"
import { ref, watchEffect } from "vue"

export function useMemoControl<T>(
  fn: () => T,
  control: Ref<boolean> | ComputedRef<boolean>
): Ref<T | undefined> {
  const value = ref<T>()

  watchEffect(() => {
    if (control.value) value.value = fn()
  })

  return value
}
