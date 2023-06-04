export interface PhimIdChapReturns<PropsGetStreams> {
  chaps: {
    name: string
    path: string
    props: PropsGetStreams
  }[]
  poster?: string
  image?: string
  update?: [number, number, number]
}
