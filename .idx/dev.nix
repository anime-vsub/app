{pkgs}: {
  channel = "stable-23.11";
  packages = [
    pkgs.nodejs_20
    pkgs.temurin-jre-bin-20
  ];
  idx.extensions = [
    "vue.volar"
    "codium.codium"
    "Codeium.codeium"
  ];
  # idx.previews = {
  #   previews = {
  #     web = {
  #       command = [
  #         "bun"
  #         "dev"
  #       ];
  #       manager = "web";
  #     };
  #   };
  # };
}
