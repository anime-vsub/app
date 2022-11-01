import { createHead } from "@vueuse/head"
import { boot } from "quasar/wrappers"

export default boot(({ app }) => {
  const head = createHead()

  app.use(head)
})
