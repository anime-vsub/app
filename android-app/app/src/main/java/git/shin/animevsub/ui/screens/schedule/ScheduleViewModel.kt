package git.shin.animevsub.ui.screens.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import git.shin.animevsub.data.model.ScheduleDay
import git.shin.animevsub.data.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScheduleUiState(
    val isLoading: Boolean = true,
    val days: List<ScheduleDay> = emptyList(),
    val error: String? = null,
    val selectedDay: Int = 0
)

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: AnimeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    init {
        loadSchedule()
    }

    fun loadSchedule() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.getSchedule()
                .onSuccess { days ->
                    // Find today's index
                    val todayIndex = days.indexOfFirst { it.isToday }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        days = days,
                        selectedDay = if (todayIndex >= 0) todayIndex else 0
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

    fun selectDay(index: Int) {
        _uiState.value = _uiState.value.copy(selectedDay = index)
    }

    fun retry() {
        loadSchedule()
    }
}
