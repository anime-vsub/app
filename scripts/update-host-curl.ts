/// <reference types="@types/bun" />
import { resolve } from "path"

import { $ } from "bun"

const { url } = await fetch(
  "\x68\x74\x74\x70\x73\x3a\x2f\x2f\x62\x69\x74\x2e\x6c\x79\x2f\x61\x6e\x69\x6d\x65\x76\x69\x65\x74\x73\x75\x62\x74\x76",
  {
    referrer: "manual"
  }
)

// url to array byte
const charCodes =
  "[\n  " +
  url
    .replace(/https?:\/{2}/, "")
    .replace(/\/$/, "")
    .split("")
    .map((char) => char.charCodeAt(0))
    .join(", ") +
  "\n]"

const content = await Bun.file(
  resolve(import.meta.dirname ?? "", "../src/constants.ts")
).text()

const indexStart = content.indexOf("// @host")
const indexStop = content.indexOf("// @end")

const newContent =
  content.slice(0, indexStart) +
  `// @host
export const HOST_CURL = ${charCodes}
  .map((val) => String.fromCharCode(val))
  .join("")
// @end` +
  content.slice(indexStop)

await Bun.write(
  Bun.file(resolve(import.meta.dirname ?? "", "../src/constants.ts")),
  newContent
)

if (newContent !== content) {
  // git commit
  await $`git add src/constants.ts`
  await $`git commit -m "[script]: Update \`HOST_CURL\``
  await $`git pull --rebase`
  await $`git push origin main`
}

export {}
