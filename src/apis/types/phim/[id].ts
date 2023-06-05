import type { CardData } from "src/components/Card.types"

interface Link {
  path: string
  name: string
}
export interface PhimReturns {
  name: string
  othername: string
  image: string
  poster?: string
  pathToView: string|null
  description: string
  rate?: number
  count_rate?: number
  duration: string
  yearOf?: Link
  views: number
  season: Link[]
  genre: Link[]
  quality?: string
  status?: string
  authors?: Link[]
  contries?: Link[]
  follows: number
  language?: string
  studio?: string | Link
  seasonOf?: Link
  trailer?: string
  toPut: CardData[]
}
