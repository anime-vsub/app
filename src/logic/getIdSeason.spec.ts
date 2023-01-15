import { getIdSeason } from "./getIdSeason"

describe("getIdSeason", () => {
  test("should return the season number", () => {
    expect(
      getIdSeason("hyouken-no-majutsushi-ga-sekai-wo-suberu-a4822")
    ).toEqual("4822")
    expect(getIdSeason("tonikaku-kawaii-a3860")).toEqual("3860")
  })
})
