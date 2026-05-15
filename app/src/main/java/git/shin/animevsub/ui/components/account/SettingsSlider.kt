package git.shin.animevsub.ui.components.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.utils.tvFocusScale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSlider(
  label: String,
  value: Int,
  onValueChange: (Int) -> Unit,
  valueRange: ClosedFloatingPointRange<Float>,
  steps: Int = 0,
  valueText: String,
  icon: ImageVector? = null
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .tvFocusScale()
      .padding(horizontal = 16.dp, vertical = 8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = AccentMain,
          modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
      }
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
      modifier = Modifier.padding(top = 4.dp),
      colors = SliderDefaults.colors(
        activeTrackColor = AccentMain,
        inactiveTrackColor = TextGrey.copy(alpha = 0.3f),
        activeTickColor = Color.Transparent,
        inactiveTickColor = Color.Transparent
      ),
      thumb = {
        androidx.compose.foundation.layout.Box(
          modifier = Modifier
            .size(12.dp)
            .padding(0.dp)
        ) {
          androidx.compose.foundation.Canvas(modifier = Modifier.size(12.dp)) {}
        }
      }
    )
  }
}
