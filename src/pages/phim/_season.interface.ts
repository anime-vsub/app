import type PhimIdChap from "src/apis/parser/phim/[id]/[chap]"

export interface Season {
  name: string
  value: string
}

export type ProgressWatchStore = Map<
  string,
  | {
      status: "pending"
    }
  | {
      status: "success"
      response: Map<
        string,
        {
          cur: number
          dur: number
        }
      > | null
    }
  | {
      status: "error"
      error: Error
    }
  | {
      status: "queue"
    }
>

export interface ResponseDataSeasonPending {
  status: "pending"
}
export interface ResponseDataSeasonSuccess {
  status: "success"
  response: Awaited<ReturnType<typeof PhimIdChap>>
}
export interface ResponseDataSeasonError {
  status: "error"
  response: {
    status: number
  }
}
