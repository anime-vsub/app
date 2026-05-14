package git.shin.animevsub.plugin

import git.shin.animevsub.data.remote.api.AnimeDataSource
import git.shin.animevsub.data.remote.api_hidden.AnimeApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataSourceProvider @Inject constructor(
    private val builtInDataSource: AnimeApi,
    private val pluginManager: PluginManager
) {
    val currentDataSource: Flow<AnimeDataSource> = combine(
        pluginManager.plugins,
        pluginManager.activePluginId
    ) { plugins, activeId ->
        if (activeId != null) {
            plugins[activeId]?.let { plugin ->
                if (plugin.info.enabled && plugin.status == PluginLoadStatus.LOADED && plugin.instance != null) {
                    return@combine plugin.instance!!
                }
            }
        }
        builtInDataSource
    }

    fun getDataSource(): AnimeDataSource {
        val activeId = pluginManager.activePluginId.value
        if (activeId != null) {
            val plugin = pluginManager.plugins.value[activeId]
            if (plugin?.info?.enabled == true && plugin.status == PluginLoadStatus.LOADED) {
                return plugin.instance!!
            }
        }
        return builtInDataSource
    }

    fun hasActivePlugin(): Boolean {
        val activeId = pluginManager.activePluginId.value ?: return false
        val plugin = pluginManager.plugins.value[activeId]
        return plugin?.info?.enabled == true && plugin.status == PluginLoadStatus.LOADED
    }
}