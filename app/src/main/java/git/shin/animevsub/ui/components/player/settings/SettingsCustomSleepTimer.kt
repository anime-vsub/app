package git.shin.animevsub.ui.components.player.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.R
import git.shin.animevsub.ui.theme.MainColor

@Composable
fun SettingsCustomSleepTimer(onSleepTimerChange: (Int) -> Unit, isSideMenu: Boolean) {
  var showInput by remember { mutableStateOf(false) }
  var value by remember { mutableStateOf("") }

  if (!showInput) {
    if (isSideMenu) {
      SideMenuOptionChip(
        text = stringResource(R.string.sleep_timer_custom),
        isSelected = false,
        onClick = { showInput = true }
      )
    } else {
      SettingsOptionItem(
        title = stringResource(R.string.sleep_timer_custom),
        isSelected = false,
        onClick = { showInput = true }
      )
    }
  } else {
    Row(
      modifier = Modifier
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      OutlinedTextField(
        value = value,
        onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 4) value = it },
        placeholder = { Text(stringResource(R.string.sleep_timer_custom_hint), fontSize = 12.sp) },
        modifier = Modifier.width(if (isSideMenu) 100.dp else 140.dp),
        singleLine = true,
        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 14.sp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = MainColor,
          unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
          focusedContainerColor = Color.Transparent,
          unfocusedContainerColor = Color.Transparent,
          cursorColor = MainColor
        )
      )
      IconButton(
        onClick = {
          val mins = value.toIntOrNull() ?: 0
          if (mins in 1..1440) {
            onSleepTimerChange(mins)
          }
          showInput = false
        },
        enabled = value.isNotEmpty()
      ) {
        Icon(Icons.Default.Check, null, tint = if (value.isNotEmpty()) MainColor else Color.Gray)
      }
      IconButton(onClick = { showInput = false }) {
        Icon(Icons.Default.Close, null, tint = Color.White.copy(alpha = 0.6f))
      }
    }
  }
}
