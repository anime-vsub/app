export interface Link {
  name: string
  path: string
}

export interface CardData {
  path: string
  image: string
  name: string
  chap?: string
  process?: string
  quality?: string
  rate?: number

  year?: Link
  description?: string
  studio?: string
  genre?: Link[]
  time_release?: number | null

  views?: number
  isTrailer?: boolean
}
