export function float(val: null | string | undefined) {
  return val === null || val === undefined ? undefined : parseFloat(val)
}
export function int(val: null | string | undefined) {
  return val === null || val === undefined ? undefined : parseInt(val)
}
