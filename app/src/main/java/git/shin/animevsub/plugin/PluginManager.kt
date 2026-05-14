package git.shin.animevsub.plugin

import android.content.Context
import git.shin.animevsub.data.remote.api.AnimeDataSource
import git.shin.animevsub.data.local.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PluginManager @Inject constructor(
    private val context: Context,
    private val httpClient: OkHttpClient,
    private val json: Json,
    private val preferencesManager: PreferencesManager
) {
    private val pluginLoader = PluginLoader(context)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _plugins = MutableStateFlow<Map<String, Plugin>>(emptyMap())
    val plugins: StateFlow<Map<String, Plugin>> = _plugins.asStateFlow()

    private val _repos = MutableStateFlow<List<RepoIndex>>(emptyList())
    val repos: StateFlow<List<RepoIndex>> = _repos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _activePluginId = MutableStateFlow<String?>(null)
    val activePluginId: StateFlow<String?> = _activePluginId.asStateFlow()

    init {
        scope.launch { loadSavedStates() }
    }

    private suspend fun loadSavedStates() {
        try {
            val statesJson = preferencesManager.pluginStates.first()
            if (statesJson.isNotEmpty()) {
                try {
                    val states = json.decodeFromString<Map<String, PluginState>>(statesJson)
                    val pluginsMap = states.mapValues { (id, state) ->
                        Plugin(
                            info = PluginInfo(
                                id = id,
                                name = id,
                                version = state.version,
                                enabled = state.enabled
                            ),
                            status = if (state.enabled) PluginLoadStatus.NOT_LOADED else PluginLoadStatus.DISABLED
                        )
                    }
                    _plugins.value = pluginsMap
                } catch (e: Exception) {
                    // Ignore parse errors
                }
            }

            val reposJson = preferencesManager.pluginRepos.first()
            if (reposJson.isNotEmpty()) {
                try {
                    _repos.value = json.decodeFromString(reposJson)
                } catch (e: Exception) {
                    // Ignore parse errors
                }
            }

            val activeId = preferencesManager.activePlugin.first()
            _activePluginId.value = activeId.ifEmpty { null }
        } catch (e: Exception) {
            // Ignore errors during initialization
        }
    }

    private fun saveStates() {
        scope.launch {
            try {
                val states = _plugins.value.mapValues { (_, plugin) ->
                    PluginState(
                        id = plugin.info.id,
                        enabled = plugin.info.enabled,
                        version = plugin.info.version
                    )
                }
                preferencesManager.setPluginStates(json.encodeToString(states))
                preferencesManager.setPluginRepos(json.encodeToString(_repos.value))
                _activePluginId.value?.let { preferencesManager.setActivePlugin(it) }
            } catch (e: Exception) {
                // Ignore save errors
            }
        }
    }

    suspend fun addRepo(url: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).get().build()
            val response = httpClient.newCall(request).execute()
            val body = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response"))

            val manifest = json.decodeFromString<RepoManifest>(body)
            val repoIndex = manifest.repo.copy(url = url)

            _repos.value = _repos.value + repoIndex
            saveStates()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun removeRepo(repoUrl: String) {
        _repos.value = _repos.value.filter { it.url != repoUrl }
        saveStates()
    }

    suspend fun fetchRepoPlugins(repoUrl: String): Result<List<RepoPlugin>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(repoUrl).get().build()
            val response = httpClient.newCall(request).execute()
            val body = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response"))

            val manifest = json.decodeFromString<RepoManifest>(body)
            Result.success(manifest.plugins)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun installPlugin(repoPlugin: RepoPlugin, repoUrl: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            _isLoading.value = true

            val request = Request.Builder().url(repoPlugin.url).get().build()
            val response = httpClient.newCall(request).execute()
            val jarBytes = response.body?.bytes() ?: return@withContext Result.failure(Exception("Empty JAR"))

            val pluginInfo = PluginInfo(
                id = repoPlugin.packageName,
                name = repoPlugin.name,
                version = repoPlugin.version,
                description = repoPlugin.description,
                author = repoPlugin.author,
                iconUrl = repoPlugin.icon,
                repoUrl = repoUrl,
                jarUrl = repoPlugin.url,
                checksum = repoPlugin.sha256,
                className = "${repoPlugin.packageName}.DataSource",
                enabled = true,
                installedAt = System.currentTimeMillis()
            )

            val installResult = pluginLoader.installPlugin(jarBytes, pluginInfo)
            if (installResult.isFailure) {
                return@withContext Result.failure(installResult.exceptionOrNull()!!)
            }

            val loadResult = pluginLoader.loadPlugin(pluginInfo)
            if (loadResult.isFailure) {
                pluginLoader.uninstallPlugin(pluginInfo.id)
                return@withContext Result.failure(loadResult.exceptionOrNull()!!)
            }

            val plugin = Plugin(
                info = pluginInfo,
                status = PluginLoadStatus.LOADED,
                instance = loadResult.getOrNull()
            )

            _plugins.value = _plugins.value + (plugin.id to plugin)
            saveStates()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun installFromFile(file: File): Result<Plugin> = withContext(Dispatchers.IO) {
        try {
            _isLoading.value = true

            val jarBytes = file.readBytes()
            val jarName = file.nameWithoutExtension

            val pluginInfo = PluginInfo(
                id = jarName,
                name = jarName,
                version = "1.0.0",
                className = "$jarName.DataSource",
                enabled = true,
                installedAt = System.currentTimeMillis()
            )

            val installResult = pluginLoader.installPlugin(jarBytes, pluginInfo)
            if (installResult.isFailure) {
                return@withContext Result.failure(installResult.exceptionOrNull()!!)
            }

            val loadResult = pluginLoader.loadPlugin(pluginInfo)
            if (loadResult.isFailure) {
                pluginLoader.uninstallPlugin(pluginInfo.id)
                return@withContext Result.failure(loadResult.exceptionOrNull()!!)
            }

            val plugin = Plugin(
                info = pluginInfo,
                status = PluginLoadStatus.LOADED,
                instance = loadResult.getOrNull()
            )

            _plugins.value = _plugins.value + (plugin.id to plugin)
            saveStates()

            Result.success(plugin)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }

    fun setActivePlugin(pluginId: String?) {
        _activePluginId.value = pluginId
        saveStates()
    }

    suspend fun enablePlugin(pluginId: String): Result<AnimeDataSource> {
        val plugin = _plugins.value[pluginId] ?: return Result.failure(Exception("Plugin not found"))

        if (plugin.info.enabled && plugin.status == PluginLoadStatus.LOADED) {
            return Result.success(plugin.instance!!)
        }

        val result = pluginLoader.loadPlugin(plugin.info.copy(enabled = true))
        if (result.isSuccess) {
            _plugins.value = _plugins.value + (pluginId to plugin.copy(
                info = plugin.info.copy(enabled = true),
                status = PluginLoadStatus.LOADED,
                instance = result.getOrNull()
            ))
            saveStates()
        }

        return result
    }

    fun disablePlugin(pluginId: String) {
        val plugin = _plugins.value[pluginId] ?: return
        pluginLoader.unloadPlugin(pluginId)

        _plugins.value = _plugins.value + (pluginId to plugin.copy(
            info = plugin.info.copy(enabled = false),
            status = PluginLoadStatus.DISABLED,
            instance = null
        ))
        saveStates()
    }

    fun uninstallPlugin(pluginId: String): Result<Unit> {
        val result = pluginLoader.uninstallPlugin(pluginId)
        if (result.isSuccess) {
            _plugins.value = _plugins.value - pluginId
            saveStates()
        }
        return result
    }

    fun getActivePlugin(): AnimeDataSource? {
        val activeId = _activePluginId.value ?: return null
        return _plugins.value[activeId]?.let { plugin ->
            if (plugin.info.enabled && plugin.status == PluginLoadStatus.LOADED) {
                plugin.instance
            } else null
        }
    }

    fun getAllEnabledPlugins(): List<Plugin> {
        return _plugins.value.values.filter { it.info.enabled && it.status == PluginLoadStatus.LOADED }
    }

    fun close() {
        pluginLoader.close()
    }
}