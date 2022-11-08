import type PhimIdChap from "src/apis/parser/phim/[id]/[chap]"

export interface ResponseDataSeasonPending {
  status: "pending"
  progressChaps?: Map<
    string,
    {
      cur: number
      dur: number
    }
  > | null /* null is status fetching */
}
export interface ResponseDataSeasonSuccess {
  status: "success"
  progressChaps?: Map<
    string,
    {
      cur: number
      dur: number
    }
  > | null /* null is status fetching */
  response: Awaited<ReturnType<typeof PhimIdChap>>
}
export interface ResponseDataSeasonError {
  status: "error"
  progressChaps?: Map<
    string,
    {
      cur: number
      dur: number
    }
  > | null /* null is status fetching */
  response: {
    status: number
  }
}
