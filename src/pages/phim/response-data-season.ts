import type PhimIdChap from "src/apis/parser/phim/[id]/[chap]"

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
