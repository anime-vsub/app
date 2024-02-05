import { parserDom } from "../__helpers__/parserDom"

export default function AccountInfo(html: string) {
  const $ = parserDom(html)

  const avatar = $(".profile-userpic img").attr("src")
  const name = $(".profile-usertitle-name").text().trim()
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const email = $("#email").attr("value")!
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  const username = $("#hoten").attr("value")!
  const sex = $("#male").attr("checked")
    ? "male"
    : $("#female").attr("checked")
      ? "female"
      : "unknown"

  return { avatar, name, email, username, sex }
}
