package git.shin.animevsub.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubRelease(
  @SerialName("tag_name") val tagName: String,
  @SerialName("body") val body: String,
  @SerialName("assets") val assets: List<GitHubAsset>
)

@Serializable
data class GitHubAsset(
  @SerialName("name") val name: String,
  @SerialName("browser_download_url") val downloadUrl: String,
  @SerialName("size") val size: Long
)

data class UpdateInfo(
  val version: String,
  val description: String,
  val downloadUrl: String,
  val isNewer: Boolean
)
