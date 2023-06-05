export interface PhimIdChapReturns<PropsGetStreams> {
  chaps: {
    name: string
    epId: string
    props: PropsGetStreams
  }[]
  poster?: string
  image?: string
  update?: [number, number, number]
}
