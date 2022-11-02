import fs from "fs"
import { resolve } from "path"

const dir = resolve(__dirname, "..", "meta/screenshoots")
const pathToReadme = resolve(__dirname, "../README.md")

const screenshots = fs
  .readdirSync(dir)
  .map((name) => `./meta/screenshoots/${name}`)

const md = screenshots.map((src) => {
  return `<a href="${src}"><img src="${src}" style="margin-top: 8px"></a>`
})

const readme = fs.readFileSync(pathToReadme, "utf8")

const indexStartScrns = readme.indexOf("<!--screenshot-->")
const indexEndScrns = readme.indexOf("<!--/screenshot-->")

if (indexStartScrns === -1 || indexEndScrns === -1) {
  console.warn("mark screenshot not exists")

  // eslint-disable-next-line n/no-process-exit
  process.exit(0)
}

fs.writeFileSync(
  pathToReadme,
  readme.slice(0, indexStartScrns) +
    `<!--screenshot-->${md.join("\n")}` +
    readme.slice(indexEndScrns - 1)
)
