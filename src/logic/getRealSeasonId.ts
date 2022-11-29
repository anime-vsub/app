/**
 * @params {string}: get into a virtual season id (with the `$` mark used to embed seasons too many chaps) or real
 * @returns {string}: Returns the actual season id used for the requests.
 */
export function getRealSeasonId(season: string) {
  return season.slice(0, season.lastIndexOf("$") >>> 0)
}
