declare module "virtual:i18n-langs" {
  export interface Language {
    code: string
    name: string
  }

  const languages: Language[]
  export default languages
}
