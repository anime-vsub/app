package git.shin.animevsub.ui.screens.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import git.shin.animevsub.data.local.download.DownloadEntity
import git.shin.animevsub.data.repository.DownloadRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadsViewModel @Inject constructor(
  private val downloadRepository: DownloadRepository
) : ViewModel() {

  val downloads: StateFlow<List<DownloadEntity>> = downloadRepository.allDownloads
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  fun deleteDownload(id: String) {
    viewModelScope.launch {
      downloadRepository.deleteDownload(id)
    }
  }
}
