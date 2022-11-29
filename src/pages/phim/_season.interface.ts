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
