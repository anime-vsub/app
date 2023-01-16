import { Directory, Encoding, Filesystem } from "@capacitor/filesystem";

import { removeFirstSlash } from "./removeFirstSlash";

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
    );
  },
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  async writeFile(path: string, data: any): Promise<void> {
    await Filesystem.writeFile({
      path: removeFirstSlash(path),
      data: JSON.stringify(data),
      directory: Directory.Cache,
      encoding: Encoding.UTF8,
    });
  },
  async unlink(path: string) {
    path = removeFirstSlash(path);

    const { type } = await Filesystem.stat({
      path,
    });

    if (type === "directory")
      await Filesystem.rmdir({
        path,
        directory: Directory.Cache,
        recursive: true,
      });
    else
      await Filesystem.deleteFile({
        path,
        directory: Directory.Cache,
      });
  },
  async mkdir(path: string) {
    await Filesystem.mkdir({
      path: removeFirstSlash(path),
      directory: Directory.Cache,
    });
  },
};
