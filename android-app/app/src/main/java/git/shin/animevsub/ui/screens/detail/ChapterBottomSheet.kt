package git.shin.animevsub.ui.screens.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import git.shin.animevsub.data.model.ChapterInfo
import git.shin.animevsub.ui.components.player.EpisodeSelectorContent
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.TextGrey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterBottomSheet(
  uiState: DetailUiState,
  sheetState: SheetState,
  onDismissRequest: () -> Unit,
  onSeasonClick: (String) -> Unit,
  onChapterClick: (ChapterInfo, String) -> Unit
) {
  val configuration = LocalConfiguration.current
  val screenWidth = configuration.screenWidthDp.dp
  val videoHeight = screenWidth * 9 / 16
  val sheetHeight = configuration.screenHeightDp.dp - videoHeight

  ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    sheetState = sheetState,
    containerColor = DarkSurface,
    dragHandle = { BottomSheetDefaults.DragHandle(color = TextGrey) },
    modifier = Modifier
      .height(sheetHeight)
      .fillMaxWidth()
  ) {
    Column(modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight()) {
      EpisodeSelectorContent(
        displaySeasons = uiState.displaySeasons,
        activeDisplaySeasonId = uiState.activeDisplaySeasonId,
        episodes = uiState.chapterData?.chaps ?: emptyList(),
        currentEpisodeId = uiState.currentChapter?.id,
        onSeasonClick = onSeasonClick,
        onChapterClick = onChapterClick,
        chapterProgress = uiState.chapterProgress,
        isSideMenu = false
      )
    }
  }
}
