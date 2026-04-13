package git.shin.animevsub.ui.components.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import git.shin.animevsub.R

@Composable
fun CommentInput(
  onPost: (String) -> Unit,
  isPosting: Boolean,
  placeholder: String = stringResource(R.string.comment_hint),
  initialText: String = "",
  userAvatar: String? = null,
  onCancel: (() -> Unit)? = null
) {
  var text by remember { mutableStateOf(initialText) }

  Row(verticalAlignment = Alignment.Top) {
    AsyncImage(
      model = ImageRequest.Builder(LocalContext.current)
        .data(userAvatar ?: "https://via.placeholder.com/150")
        .crossfade(true)
        .build(),
      contentDescription = null,
      modifier = Modifier
        .size(36.dp)
        .clip(CircleShape),
      contentScale = ContentScale.Crop
    )

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      TextField(
        value = text,
        onValueChange = { text = it },
        placeholder = { Text(placeholder) },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
          focusedContainerColor = Color.Transparent,
          unfocusedContainerColor = Color.Transparent,
          disabledContainerColor = Color.Transparent
        ),
        maxLines = 5
      )

      if (text.isNotBlank() || onCancel != null) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
          TextButton(onClick = {
            if (onCancel != null) {
              onCancel()
            } else {
              text = ""
            }
          }) {
            Text(stringResource(R.string.cancel_comment))
          }
          if (text.isNotBlank()) {
            Button(
              onClick = {
                onPost(text)
                if (isPosting) text = "" // Assuming successful post if we are still in this state
              },
              enabled = true // Always clickable to trigger auth gate if not logged in
            ) {
              if (isPosting) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
              } else {
                Text(stringResource(R.string.post_comment))
              }
            }
          }
        }
      }
    }
  }
}
