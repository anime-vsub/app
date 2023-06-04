export interface GetListServersReturns<Props> {
  servers: {
    props: Props
    name: string
  }[]
}
