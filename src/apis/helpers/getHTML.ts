export function getHTML(url: string) {
  return new Promise<string>((resolve) => {
    setTimeout(() => resolve(url), 500)
  })
}
