import type { Directory } from "@capacitor/filesystem"
import { Filesystem } from "@capacitor/filesystem"
import { createFilesystem } from "capacitor-fs"

const fsStore = new Map<Directory, ReturnType<typeof createFilesystem>>()
export function useFs(directory: Directory) {
  const inStore = fsStore.get(directory)

  if (inStore) return inStore

  const fs = createFilesystem(Filesystem, {
    directory,
    base64Alway: true,
    watcher: false,
  })
  fsStore.set(directory, fs)

  return fs
}
