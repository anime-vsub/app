import type { CardData, Link } from "../Card.types"

export interface CarouselTopProps {
  hoz?: boolean
  aspectRatio: number
  items: {
    path: string
    image: string
    name: string

    quality?: string

    year?: Link
    rate?: number
    process?: string

    isTrailer?: boolean

    genre?:Link[]

    description?: string
    originName?: string
  }[]
}

export interface GridCardProps {
  items: CardData[]

  to?: string
  name: string
}

export interface ListProps {
  items: CardData[]

  qualityFloatRight?: boolean
  rank?: boolean

  to?: string
  name?: string
}

export interface ListReleaseProps {
  items: CardData[]

  to: string
  name: string
}
