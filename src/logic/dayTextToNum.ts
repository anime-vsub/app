const map: Record<string, number> = {
  // tiếng việt
  "chủ nhật": 0,
  "thứ 2": 1,
  "thứ hai": 1,
  "thứ 3": 2,
  "thứ ba": 2,
  "thứ 4": 3,
  "thứ tư": 3,
  "thứ bốn": 3,
  "thứ 5": 4,
  "thứ năm": 4,
  "thứ 6": 5,
  "thứ sáu": 5,
  "thứ 7": 6,
  "thứ bảy": 6,

  // 日本人
  セコンド: 1,
  三つ目: 2,
  第四に: 3,
  五番目: 4,
  ろく番目: 5,
  第七: 6,
  サンデー: 0
}

export function dayTextToNum(text: string) {
  return map[text.toLowerCase()] ?? text
}
