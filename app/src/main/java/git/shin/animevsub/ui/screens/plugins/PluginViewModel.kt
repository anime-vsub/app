package git.shin.animevsub.ui.screens.plugins

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import git.shin.animevsub.plugin.DataSourceProvider
import git.shin.animevsub.plugin.Plugin
import git.shin.animevsub.plugin.PluginManager
import git.shin.animevsub.plugin.RepoIndex
import git.shin.animevsub.plugin.RepoPlugin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class PluginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddRepoDialog: Boolean = false,
    val showBrowseDialog: Boolean = false,
    val repoPlugins: List<RepoPlugin> = emptyList(),
    val selectedRepo: RepoIndex? = null,
    val browseResult: String? = null
)

@HiltViewModel
class PluginViewModel @Inject constructor(
    private val pluginManager: PluginManager,
    private val dataSourceProvider: DataSourceProvider
) : ViewModel() {

    val plugins = pluginManager.plugins.stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())
    val repos = pluginManager.repos.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val isLoading = pluginManager.isLoading.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val activePluginId = pluginManager.activePluginId.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _uiState = MutableStateFlow(PluginUiState())
    val uiState: StateFlow<PluginUiState> = _uiState.asStateFlow()

    fun showAddRepoDialog() {
        _uiState.value = _uiState.value.copy(showAddRepoDialog = true)
    }

    fun hideAddRepoDialog() {
        _uiState.value = _uiState.value.copy(showAddRepoDialog = false)
    }

    fun addRepo(url: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, showAddRepoDialog = false)
            val result = pluginManager.addRepo(url)
            if (result.isFailure) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun removeRepo(repoUrl: String) {
        pluginManager.removeRepo(repoUrl)
    }

    fun browseRepo(repo: RepoIndex) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, selectedRepo = repo)
            val result = pluginManager.fetchRepoPlugins(repo.url)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    repoPlugins = result.getOrNull() ?: emptyList(),
                    showBrowseDialog = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun hideBrowseDialog() {
        _uiState.value = _uiState.value.copy(showBrowseDialog = false, selectedRepo = null, repoPlugins = emptyList())
    }

    fun installPlugin(repoPlugin: RepoPlugin) {
        val repoUrl = _uiState.value.selectedRepo?.url ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, showBrowseDialog = false)
            val result = pluginManager.installPlugin(repoPlugin, repoUrl)
            if (result.isFailure) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun installFromFile(file: File) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = pluginManager.installFromFile(file)
            if (result.isFailure) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun setActivePlugin(pluginId: String?) {
        pluginManager.setActivePlugin(pluginId)
    }

    fun enablePlugin(pluginId: String) {
        viewModelScope.launch {
            pluginManager.enablePlugin(pluginId)
        }
    }

    fun disablePlugin(pluginId: String) {
        pluginManager.disablePlugin(pluginId)
    }

    fun uninstallPlugin(pluginId: String) {
        pluginManager.uninstallPlugin(pluginId)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}