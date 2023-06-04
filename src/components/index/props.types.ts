import type { CardData } from "../Card.types"

export interface CarouselTopProps {
  hoz?: boolean
  aspectRatio: number
  items: {
    path: string
    image: string
    name: string

    quality?: string

    year?: string
    rate?: string
    process?: string

    isTrailer?: boolean

    genre?: {
      name: string
      path: string
    }[]

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

interface CardDataC extends CardData {
  time_release: string
}
export interface ListReleaseProps {
  items: CardDataC[]

  to: string
  name: string
}
