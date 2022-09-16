export function parserDOM(html: string): Document {
  return new DOMParser().parseFromString(html, 'text/html');
}
