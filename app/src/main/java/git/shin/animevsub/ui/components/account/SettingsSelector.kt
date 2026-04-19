package git.shin.animevsub.ui.components.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary

@Composable
fun SettingsSelector(
  label: String,
  selectedOption: String,
  options: List<Pair<String, String>>, // Pair(Internal Value, Display Name)
  onOptionSelected: (String) -> Unit
) {
  var expanded by remember { mutableStateOf(false) }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { expanded = true }
      .padding(horizontal = 16.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = label,
        color = TextPrimary,
        fontSize = 15.sp
      )
      Text(
        text = options.find { it.first == selectedOption }?.second ?: selectedOption,
        color = TextGrey,
        fontSize = 13.sp
      )
    }

    Box {
      DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
      ) {
        options.forEach { (value, displayName) ->
          DropdownMenuItem(
            text = { Text(displayName) },
            onClick = {
              onOptionSelected(value)
              expanded = false
            }
          )
        }
      }
    }
  }
}
