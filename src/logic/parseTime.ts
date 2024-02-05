function toFixed(num: number): string {
  if (num < 10) return `0${num}`

  return `${num}`
}

export function parseTime(seconds: number): string {
  let minutes = ~~(seconds / 60)
  seconds -= minutes * 60

  if (minutes < 60) {
    return `${toFixed(minutes)}:${toFixed(~~seconds)}`
  }

  const hours = ~~(minutes / 60)
  minutes -= hours * 60

  return `${toFixed(hours)}:${toFixed(minutes)}:${toFixed(~~seconds)}`
}
