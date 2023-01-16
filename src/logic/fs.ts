import { Directory, Encoding, Filesystem } from "@capacitor/filesystem"

import { removeFirstSlash } from "./removeFirstSlash"

export const fs = {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any, @typescript-eslint/no-unused-vars
  async readFile(path: string, _encode: "utf8"): Promise<any> {
    return JSON.parse(
      (
        await Filesystem.readFile({
          path: removeFirstSlash(path),
          directory: Directory.Cache,
          encoding: Encoding.UTF8,
        })
      ).data
    )
  },
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  async writeFile(path: string, data: any): Promise<void> {
    Filesystem.writeFile({
      path: removeFirstSlash(path),
      data: JSON.stringify(data),
      directory: Directory.Cache,
      encoding: Encoding.UTF8,
    })
  },
}
