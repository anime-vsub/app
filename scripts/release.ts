import { spawnSync } from "child_process"
import { readFileSync, writeFileSync } from "fs"
import { resolve } from "path"
import { exit } from "process"

import { bold, green, red } from "kleur"
import prompts from "prompts"
import semver, { valid as isValidVersion, SemVer } from "semver"

async function bumppAndroid() {
  const androidDir = resolve(__dirname, "../src-capacitor/android")

  const buildGradle = readFileSync(
    resolve(androidDir, "app/build.gradle"),

    "utf8"
  )

  const indexVersionCode = buildGradle.indexOf("versionCode")
  const indexVersionName = buildGradle.indexOf("versionName")

  if (indexVersionCode === -1) {
    console.error(red("versionCode not found in build.gradle"))
    exit(1)
  }
  if (indexVersionName === -1) {
    console.error(red("versionName not found in build.gradle"))
    exit(1)
  }

  const _t1 = buildGradle.slice(indexVersionCode + 11)
  const currentVersionCode = parseInt(_t1.slice(0, _t1.indexOf("\n")).trim())
  const _t2 = buildGradle.slice(indexVersionName + 11)
  // eslint-disable-next-line no-new-func
  const currentVersionName = new Function(
    "return " + _t2.slice(0, _t2.indexOf("\n")).trim()
  )()

  const PADDING = 13

  const options = await prompts([
    {
      type: "autocomplete",
      name: "versionCode",
      message: `Current versionCode ${green(currentVersionCode)}`,
      initial: 0,
      choices: [
        {
          value: currentVersionCode + 1,
          title: `${"next".padStart(PADDING, " ")} ${bold(
            currentVersionCode + 1
          )}`,
          selected: true,
        },
        {
          value: currentVersionCode,
          title: `${"as-it".padStart(PADDING, " ")} ${bold(
            currentVersionCode
          )}`,
        },
        { value: "custom", title: "custom ...".padStart(PADDING + 4, " ") },
      ],
    },
    {
      type: (prev) => (prev === "custom" ? "number" : null),
      name: "customVersionCode",
      message: "Enter the new version number:",
      initial: currentVersionCode,
      validate: (value: number) =>
        value >= currentVersionCode
          ? true
          : "Invalid version code. The new version must be higher than the old version",
    },
    {
      type: "select",
      name: "versionName",
      initial: 3,
      message: `Current versionName ${green(currentVersionName)}:`,
      choices: (
        [
          "major",
          "minor",
          "patch",

          "next",

          "prepatch",

          "preminor",
          "prerelease",
        ] as (semver.ReleaseType | "next")[]
      )
        .map((name) => {
          const value =
            name === "next"
              ? semver.parse(currentVersionName)?.prerelease?.length
                ? // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
                  semver.inc(currentVersionName, "prerelease")!
                : // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
                  semver.inc(currentVersionName, "patch")!
              : new SemVer(currentVersionName).inc(name)

          return { title: `${name.padStart(PADDING, " ")} ${value}`, value }
        })
        .concat([
          { value: "custom", title: "custom ...".padStart(PADDING + 4, " ") },
        ]),
    },
    {
      type: (prev) => (prev === "custom" ? "text" : null),
      name: "customVersionName",
      message: "Enter the new version number:",
      initial: currentVersionName,
      validate: (custom: string) => {
        return isValidVersion(custom)
          ? true
          : "That's not a valid version number"
      },
    },
  ])

  const newVersionCode =
    options.versionCode === "custom"
      ? options.customVersionCode
      : options.versionCode
  const newVersionName =
    options.versionName === "custom"
      ? options.customVersionName
      : options.versionName

  const newBuildGradle = buildGradle
    .replace(
      `versionCode ${currentVersionCode}`,
      `versionCode ${newVersionCode}`
    )
    .replace(
      `versionName '${currentVersionName}`,
      `versionName '${newVersionName}`
    )

  writeFileSync(resolve(androidDir, "app/build.gradle"), newBuildGradle)
  spawnSync("git", ["add", `${resolve(androidDir, "app/build.gradle")}`], {
    stdio: "inherit",
  })
  spawnSync(
    "git",
    [
      "commit",
      "-m",
      `chore: release ${newVersionName} build ${newVersionCode}`,
    ],
    { stdio: "inherit" }
  )
  spawnSync("git", ["tag", `v${currentVersionName}`])
  spawnSync("git", ["push"], { stdio: "inherit" })
  spawnSync("git", ["push", "--tags"])
}
bumppAndroid()
