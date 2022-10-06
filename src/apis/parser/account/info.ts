import { parserDom } from "../__helpers__/parserDom"

export default function AccountInfo(html: string) {
  const $ = parserDom(html)

  const avatar = $(".profile-userpic img").attr("src")
  const name = $(".profile-usertitle-name").text().trim()
  const email = $("#email").attr("value")
  const username = $("#hoten").attr("value")
  const sex = $("#male").attr("checked")
    ? "male"
    : $("#female").attr("checked")
    ? "female"
    : "unknown"

  return { avatar, name, email, username, sex }
}
