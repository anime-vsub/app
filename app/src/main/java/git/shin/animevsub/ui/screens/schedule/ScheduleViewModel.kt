package git.shin.animevsub.ui.screens.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import git.shin.animevsub.data.model.ScheduleDay
import git.shin.animevsub.data.repository.AnimeRepository
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ScheduleUiState(
  val isLoading: Boolean = true,
  val isRefreshing: Boolean = false,
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

  fun refresh() {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isRefreshing = true)
      repository.getSchedule()
        .onSuccess { days ->
          _uiState.value = _uiState.value.copy(
            isRefreshing = false,
            days = days
          )
        }
        .onFailure { e ->
          _uiState.value = _uiState.value.copy(
            isRefreshing = false,
            error = e.message
          )
        }
    }
  }

  fun loadSchedule() {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isLoading = true, error = null)
      repository.getSchedule()
        .onSuccess { days ->
          // Find today's index
          val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
          }.timeInMillis

          val todayIndex = days.indexOfFirst {
            val dayCal = Calendar.getInstance().apply { timeInMillis = it.date }
            dayCal.set(Calendar.HOUR_OF_DAY, 0)
            dayCal.set(Calendar.MINUTE, 0)
            dayCal.set(Calendar.SECOND, 0)
            dayCal.set(Calendar.MILLISECOND, 0)
            dayCal.timeInMillis == today
          }

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
}
