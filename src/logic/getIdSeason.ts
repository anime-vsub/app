export function getIdSeason(season: string): `${number}` | null {
  return (/\d+$/.exec(season)?.[0] as `${number}`) ?? null
}
