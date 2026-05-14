package git.shin.animevsub.plugin

import git.shin.animevsub.data.remote.api.AnimeDataSource
import kotlinx.serialization.Serializable

@Serializable
data class PluginInfo(
    val id: String,
    val name: String,
    val version: String,
    val description: String = "",
    val author: String = "",
    val iconUrl: String = "",
    val repoUrl: String = "",
    val jarUrl: String = "",
    val checksum: String = "",
    val className: String = "",
    val enabled: Boolean = false,
    val installedAt: Long = 0L,
    val lastUpdated: Long = 0L
)

@Serializable
data class PluginState(
    val id: String,
    val enabled: Boolean,
    val version: String,
    val lastCheck: Long = 0L
)

@Serializable
data class RepoIndex(
    val name: String,
    val url: String,
    val icon: String = ""
)

@Serializable
data class RepoPlugin(
    val name: String,
    val packageName: String,
    val version: String,
    val versionCode: Int,
    val description: String = "",
    val author: String = "",
    val icon: String = "",
    val url: String,
    val sha256: String = ""
)

@Serializable
data class RepoManifest(
    val repo: RepoIndex,
    val plugins: List<RepoPlugin>
)

enum class PluginLoadStatus {
    NOT_LOADED,
    LOADING,
    LOADED,
    FAILED,
    DISABLED
}

data class Plugin(
    val info: PluginInfo,
    val status: PluginLoadStatus = PluginLoadStatus.NOT_LOADED,
    val instance: AnimeDataSource? = null,
    val error: String? = null
) {
    val id: String get() = info.id
}