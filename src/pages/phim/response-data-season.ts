import type PhimIdChap from "src/apis/parser/phim/[id]/[chap]"

import type { Season } from "./_season.interface"

export interface ResponseDataSeasonPending {
  status: "pending"
}
export interface ResponseDataSeasonSuccess {
  status: "success"
  response: Awaited<ReturnType<typeof PhimIdChap>> & {
    ssSibs?: Season[]
  }
}
export interface ResponseDataSeasonError {
  status: "error"
  response: {
    status: number
  }
}
