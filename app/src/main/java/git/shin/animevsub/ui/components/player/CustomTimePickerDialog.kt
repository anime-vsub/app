package git.shin.animevsub.ui.components.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import git.shin.animevsub.R

@Composable
fun CustomTimePickerDialog(
  onDismissRequest: () -> Unit,
  onConfirm: () -> Unit,
  content: @Composable () -> Unit
) {
  Dialog(
    onDismissRequest = onDismissRequest,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      tonalElevation = 6.dp,
      modifier = Modifier
        .width(IntrinsicSize.Min)
        .height(IntrinsicSize.Min)
        .background(MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
          text = stringResource(R.string.select_time),
          style = MaterialTheme.typography.labelMedium
        )
        content()
        Row(
          modifier = Modifier
            .height(40.dp)
            .fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(onClick = onDismissRequest) {
            Text(stringResource(R.string.cancel))
          }
          TextButton(onClick = onConfirm) {
            Text(stringResource(R.string.ok))
          }
        }
      }
    }
  }
}
