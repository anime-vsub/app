package git.shin.animevsub.ui.screens.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import git.shin.animevsub.data.model.UpdateInfo
import git.shin.animevsub.utils.UpdateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AboutUiState(
  val isCheckingUpdate: Boolean = false,
  val updateInfo: UpdateInfo? = null,
  val error: String? = null
)

@HiltViewModel
class AboutViewModel @Inject constructor(
  private val updateManager: UpdateManager
) : ViewModel() {

  private val _uiState = MutableStateFlow(AboutUiState())
  val uiState = _uiState.asStateFlow()

  fun checkUpdate() {
    viewModelScope.launch {
      _uiState.update { it.copy(isCheckingUpdate = true, error = null) }
      updateManager.checkForUpdate()
        .onSuccess { info ->
          _uiState.update { it.copy(isCheckingUpdate = false, updateInfo = info) }
        }
        .onFailure { e ->
          _uiState.update { it.copy(isCheckingUpdate = false, error = e.message) }
        }
    }
  }

  fun downloadUpdate(info: UpdateInfo) {
    updateManager.downloadAndInstall(info.downloadUrl, "AnimeVsub_v${info.version}.apk")
  }

  fun dismissUpdate() {
    _uiState.update { it.copy(updateInfo = null) }
  }
}
