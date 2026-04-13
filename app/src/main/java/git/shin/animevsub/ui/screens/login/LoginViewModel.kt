package git.shin.animevsub.ui.screens.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import git.shin.animevsub.R
import git.shin.animevsub.data.repository.AnimeRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
  val loginUrl: String = "",
  val isLoading: Boolean = false,
  val error: String? = null,
  val isSuccess: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
  private val repository: AnimeRepository,
  @ApplicationContext private val context: Context
) : ViewModel() {

  private val _uiState = MutableStateFlow(LoginUiState(loginUrl = repository.loginUrl))
  val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

  fun checkLoginStatus() {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isLoading = true, error = null)
      repository.refreshUser()
        .onSuccess {
          _uiState.value = _uiState.value.copy(
            isLoading = false,
            isSuccess = true
          )
        }
        .onFailure { e ->
          _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = context.getString(R.string.login_not_success)
          )
        }
    }
  }
}
