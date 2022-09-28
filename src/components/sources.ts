export interface Source {
  html: string
  url: string
  type:
    | "hls"
    | "aac"
    | "f4a"
    | "mp4"
    | "f4v"
    | "m3u"
    | "m4v"
    | "mov"
    | "mp3"
    | "mpeg"
    | "oga"
    | "ogg"
    | "ogv"
    | "vorbis"
    | "webm"
}
