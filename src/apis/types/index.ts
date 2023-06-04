import type { CarouselTopProps, GridCardProps, ListProps, ListReleaseProps } from "src/components/index/props.types"

export type IndexReturns = (
  | {
      name: "CarouselTop"
      props: CarouselTopProps
    }
  | {
      name: "GridCard"
      props: GridCardProps
    }
  | {
      name: "List"
      props: ListProps
    }
  | {
      name: "ListRelease"
      props: ListReleaseProps
    }
)[]
