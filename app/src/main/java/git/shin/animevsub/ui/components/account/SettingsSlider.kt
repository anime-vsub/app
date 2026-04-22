package git.shin.animevsub.ui.components.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import kotlin.math.roundToInt

@Composable
fun SettingsSlider(
  label: String,
  value: Int,
  onValueChange: (Int) -> Unit,
  valueRange: ClosedFloatingPointRange<Float>,
  steps: Int = 0,
  valueText: String
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = label,
        color = TextPrimary,
        fontSize = 15.sp,
        modifier = Modifier.weight(1f)
      )
      Text(
        text = valueText,
        color = AccentMain,
        fontSize = 14.sp
      )
    }
    Slider(
      value = value.toFloat(),
      onValueChange = { onValueChange(it.roundToInt()) },
      valueRange = valueRange,
      steps = steps,
      colors = SliderDefaults.colors(
        thumbColor = AccentMain,
        activeTrackColor = AccentMain, inactiveTrackColor = TextGrey.copy(alpha = 0.3f), activeTickColor = androidx.compose.ui.graphics.Color.Transparent, inactiveTickColor = androidx.compose.ui.graphics.Color.Transparent
      ),
      modifier = Modifier.padding(top = 4.dp)
    )
  }
}
