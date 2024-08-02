export function getRedirect(req: Request): Promise<string> {
  const controller = new AbortController()

  return fetch(req, {
    ...req,
    signal: controller.signal,
  }).then((res) => {
    controller.abort()
    return res.url
  })
}

