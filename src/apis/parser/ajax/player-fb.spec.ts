import AjaxPlayerFBParser from "./player-fb"

describe("player", () => {
  test("AjaxPlayerFBParser", () => {
    expect(
      AjaxPlayerFBParser(
        '<h3>Chọn Server:</h3> <a class="btn3dsv active" href="#" data-id="0" data-play="api" data-href="yYIUcEbWNhNTfgVmsy0mGm12d-u_2ogvAIMZCMh3fl1vqtA87eKXLK7ANDBjJKmuweuDgeLYwKrhZmGzGUhnyE_7M9u1pa0YwTYGFTpZiMF2wU96TBvthRucajflBLQy">DU</a><a class="btn3dsv" href="#" data-id="2" data-play="api" data-href="SEAiZc7pSjNkB2hc9Z9HAgcH_OmZzuBHytI_Q51KqtyHg-T0WfhwQAmVV6i4TM4VtHrk09wA6YFztU0wUl4fCw">FB</a><a class="btn3dsv" href="#" data-id="3" data-play="embed" data-href="iJOmPd0bzrHcLIv-vNMUV14kWPVgfuUPYlynlv6lROsyoC3EbhZV21qkjVq8BcApGAiL-Xe4D95_ti5KfeRm2Q">HDX(ADS)</a>'
      )
    ).toEqual({
      id: "2",
      play: "api",
      hash: "SEAiZc7pSjNkB2hc9Z9HAgcH_OmZzuBHytI_Q51KqtyHg-T0WfhwQAmVV6i4TM4VtHrk09wA6YFztU0wUl4fCw"
    })
  })
})
