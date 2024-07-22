import { QSpinner } from "quasar"
import { base64ToArrayBuffer } from "src/logic/base64ToArrayBuffer"
import { get } from "src/logic/http"
import type { Ref } from "vue"
import {
  computed,
  defineComponent,
  h,
  onBeforeUnmount,
  onMounted,
  ref,
  Transition,
  watch,
} from "vue"

const useRatioProps = {
  ratio: [String, Number],
}

function useRatio(
  props: { ratio?: number | string },
  naturalRatio: { value: number | string } | undefined
) {
  // return ratioStyle
  return computed(() => {
    const ratio = Number(
      props.ratio ||
        (naturalRatio !== undefined ? naturalRatio.value : undefined)
    )

    return isNaN(ratio) !== true && ratio > 0
      ? { paddingBottom: `${100 / ratio}%` }
      : null
  })
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
function hSlot(slot: (() => any) | undefined, otherwise?: any) {
  return slot !== undefined ? slot() || otherwise : otherwise
}

/*
 * (String)  key       - unique vnode key
 * (Boolean) condition - should change ONLY when adding/removing directive
 */

const storeHostnameXHR = new Set<string>()
function addSrcNeedXHR(src: string) {
  const { hostname } = new URL(src)

  storeHostnameXHR.add(hostname)
}
function checkSrcNeedXHR(src: string) {
  if (src.startsWith("blob:")) return false

  const { hostname } = new URL(src)

  return storeHostnameXHR.has(hostname)
}

const defaultRatio = 16 / 9

async function getImageWithXHR(url: string) {
  const res = await get({
    url,
    responseType: "arraybuffer",
  })

  if (res.status > 299) throw res

  const src = URL.createObjectURL(
    new Blob([
      typeof res.data === "object" ? res.data : base64ToArrayBuffer(res.data),
    ])
  )

  return { src }
}

export default defineComponent({
  name: "QImg",

  props: {
    ...useRatioProps,

    src: String,
    srcset: String,
    sizes: String,

    alt: String,
    crossorigin: String,
    decoding: String,
    referrerpolicy: String,

    draggable: Boolean,

    loading: {
      type: String,
      default: "lazy",
    },
    fetchpriority: {
      type: String,
      default: "auto",
    },
    width: String,
    height: String,
    initialRatio: {
      type: [Number, String],
      default: defaultRatio,
    },

    placeholderSrc: String,

    fit: {
      type: String,
      default: "cover",
    },
    position: {
      type: String,
      default: "50% 50%",
    },

    imgClass: String,
    imgStyle: Object,

    noSpinner: Boolean,
    noNativeMenu: Boolean,
    noTransition: Boolean,

    spinnerColor: String,
    spinnerSize: String,
  },

  emits: ["load", "error"],

  setup(props, { slots, emit }) {
    const naturalRatio = ref(props.initialRatio)
    const ratioStyle = useRatio(props, naturalRatio)

    let loadTimer: string | number | NodeJS.Timeout | null | undefined = null
    let isDestroyed = false

    const images: Ref<{ src: string } | null>[] = [
      ref(null),
      ref(getPlaceholderSrc()),
    ]

    const position = ref(0)

    const isLoading = ref(false)
    const hasError = ref(false)

    const classes = computed(
      () => `q-img q-img--${props.noNativeMenu === true ? "no-" : ""}menu`
    )

    const style = computed(() => ({
      width: props.width,
      height: props.height,
    }))

    const imgClass = computed(
      () =>
        `q-img__image ${
          props.imgClass !== undefined ? props.imgClass + " " : ""
        }` +
        `q-img__image--with${
          props.noTransition === true ? "out" : ""
        }-transition`
    )

    const imgStyle = computed(() => ({
      ...props.imgStyle,
      objectFit: props.fit,
      objectPosition: props.position,
    }))

    watch(() => getCurrentSrc(), addImage)

    function getCurrentSrc() {
      return props.src || props.srcset || props.sizes
        ? {
            // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
            src: props.src!,
            srcset: props.srcset,
            sizes: props.sizes,
          }
        : null
    }

    function getPlaceholderSrc() {
      return props.placeholderSrc !== undefined
        ? { src: props.placeholderSrc }
        : null
    }

    async function addImage(imgProps: { src: string } | null) {
      if (loadTimer !== null) {
        clearTimeout(loadTimer)
        loadTimer = null
      }

      hasError.value = false

      if (imgProps === null) {
        isLoading.value = false
        images[position.value ^ 1].value = getPlaceholderSrc()
      } else {
        isLoading.value = true
      }

      if (imgProps && checkSrcNeedXHR(imgProps.src))
        images[position.value].value = await getImageWithXHR(imgProps.src)
      else images[position.value].value = imgProps
    }

    function onLoad(event: Event) {
      const target = event.target as HTMLImageElement
      if (isDestroyed === true) {
        return
      }

      if (loadTimer !== null) {
        clearTimeout(loadTimer)
        loadTimer = null
      }

      naturalRatio.value =
        target.naturalHeight === 0
          ? 0.5
          : target.naturalWidth / target.naturalHeight

      waitForCompleteness(target, 1)
    }

    function waitForCompleteness(target: HTMLImageElement, count: number) {
      // protect against running forever
      if (isDestroyed === true || count === 1000) {
        return
      }

      if (target.complete === true) {
        onReady(target)
      } else {
        loadTimer = setTimeout(() => {
          loadTimer = null
          waitForCompleteness(target, count + 1)
        }, 50)
      }
    }

    function onReady(img: HTMLImageElement) {
      if (isDestroyed === true) {
        return
      }

      position.value = position.value ^ 1
      images[position.value].value = null
      isLoading.value = false
      hasError.value = false
      if (img.src.startsWith("blob:")) URL.revokeObjectURL(img.src)
      emit("load", img.currentSrc || img.src)
    }

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    async function onError(err: any) {
      try {
        const img = err.target as HTMLImageElement

        const source = img.currentSrc || img.src
        if (source.startsWith("blob:")) {
          URL.revokeObjectURL(source)

          throw new Error("blob: url not re-try fetch")
        }

        addSrcNeedXHR(source)
        addImage(await getImageWithXHR(source))
      } catch {
        if (loadTimer !== null) {
          clearTimeout(loadTimer)
          loadTimer = null
        }

        isLoading.value = false
        hasError.value = true
        images[position.value].value = null
        images[position.value ^ 1].value = getPlaceholderSrc()
        emit("error", err)
      }
    }

    function getImage(index: number) {
      const img = images[index].value

      const data = {
        key: "img_" + index,
        class: imgClass.value,
        style: imgStyle.value,
        crossorigin: props.crossorigin,
        decoding: props.decoding,
        referrerpolicy: props.referrerpolicy,
        height: props.height,
        width: props.width,
        loading: props.loading,
        fetchpriority: props.fetchpriority,
        "aria-hidden": "true",
        draggable: props.draggable,
        ...img,
      }

      if (position.value === index) {
        data.class += " q-img__image--waiting"
        Object.assign(data, { onLoad, onError })
      } else {
        data.class += " q-img__image--loaded"
      }

      return h(
        "div",
        { class: "q-img__container absolute-full", key: "img" + index },
        h("img", data)
      )
    }

    function getContent() {
      if (isLoading.value !== true) {
        return h(
          "div",
          {
            key: "content",
            class: "q-img__content absolute-full q-anchor--skip",
          },
          hSlot(slots[hasError.value === true ? "error" : "default"])
        )
      }

      return h(
        "div",
        {
          key: "loading",
          class: "q-img__loading absolute-full flex flex-center",
        },
        slots.loading !== undefined
          ? slots.loading()
          : props.noSpinner === true
          ? undefined
          : [
              h(QSpinner, {
                color: props.spinnerColor,
                size: props.spinnerSize,
              }),
            ]
      )
    }

    if (import.meta.env.SSR !== true) {
      if (typeof window !== "undefined") {
        onMounted(() => {
          addImage(getCurrentSrc())
        })
      } else {
        addImage(getCurrentSrc())
      }

      onBeforeUnmount(() => {
        isDestroyed = true

        if (loadTimer !== null) {
          clearTimeout(loadTimer)
          loadTimer = null
        }
      })
    }

    return () => {
      const content = []

      if (ratioStyle.value !== null) {
        content.push(h("div", { key: "filler", style: ratioStyle.value }))
      }

      if (hasError.value !== true) {
        if (images[0].value !== null) {
          content.push(getImage(0))
        }

        if (images[1].value !== null) {
          content.push(getImage(1))
        }
      }

      content.push(h(Transition, { name: "q-transition--fade" }, getContent))

      return h(
        "div",
        {
          class: classes.value,
          style: style.value,
          role: "img",
          "aria-label": props.alt,
        },
        content
      )
    }
  },
})
