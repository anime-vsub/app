import { useEventListener } from "@vueuse/core"

export function useOnline() {
	const online = ref(navigator.onLine)


	useEventListener(window, 'offline', () => {
		online.value = false 
	})
	useEventListener(window, 'online', () => {
		online.value = true
	})

	return ref(false) // online
}