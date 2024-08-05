import { isNative } from "../constants"

export function getRedirect(req: Request): Promise<string> {
  if (isNative)
    return fetch(new Request(`${req.url}#resolve`, req)).then((res) =>
      res.text()
    )

  const controller = new AbortController()

  return fetch(req, {
    ...req,
    signal: controller.signal,
  }).then((res) => {
    controller.abort()
    return res.url
  })
}
