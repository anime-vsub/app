// eslint-disable-next-line functional/no-let
let hash: string | null

export async function getHashHelper() {
  if (hash !== undefined) return hash

  return (hash = (await window?.Http?.getHash?.()) ?? null)
}
