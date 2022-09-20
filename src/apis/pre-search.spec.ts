
import { describe, expect, test } from "vitest"
import { preSearch } from "./pre-search"

describe("pre-search", () => {
   test("normal", async () => {
     expect(await preSearch("")).toEqual([
      {
        poster: 'http://cdn.animevietsub.cc/data/poster/2022/02/24/animevsub-vziyRxKpU1.jpg',
        path: '/phim/tonikaku-kawaii-2nd-season-a4476/',
        name: 'Tonikaku Kawaii 2nd Season',
        status: 'Preview VietSub'
      },
      {
        poster: 'http://cdn.animevietsub.cc/data/poster/2021/08/18/animevsub-HXKAyrZo5i.jpg',
        path: '/phim/tonikaku-kawaii-sns-a4229/',
        name: 'Tonikaku Kawaii: SNS',
        status: 'HD-VietSub'
      },
      {
        poster: 'http://cdn.animevietsub.cc/data/poster/2020/09/30/animevsub-1NmQPMC9x0.jpg',
        path: '/phim/tonikaku-kawaii-a3860/',
        name: 'Tonikaku Kawaii [BD]',
        status: 'Full VietSub'
      }
    ])
   })
})