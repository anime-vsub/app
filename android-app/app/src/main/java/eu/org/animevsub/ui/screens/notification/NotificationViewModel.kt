package eu.org.animevsub.ui.screens.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.org.animevsub.data.model.NotificationData
import eu.org.animevsub.data.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationUiState(
    val isLoading: Boolean = true,
    val data: NotificationData? = null,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: AnimeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val loggedIn = repository.isLoggedIn.first()
            _uiState.value = _uiState.value.copy(isLoggedIn = loggedIn)
            if (loggedIn) {
                loadNotifications()
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.getNotifications()
                .onSuccess { data ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        data = data,
                        error = null
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
        }
    }

    fun retry() {
        loadNotifications()
    }
}
