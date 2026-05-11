package git.shin.animevsub.ui.components.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jeziellago.compose.markdowntext.MarkdownText
import git.shin.animevsub.R
import git.shin.animevsub.ui.theme.DarkSurface
import git.shin.animevsub.ui.theme.GithubBlue
import git.shin.animevsub.ui.theme.MainColor
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary
import git.shin.animevsub.ui.utils.shimmerEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatBottomSheet(
  title: String,
  isLoading: Boolean,
  error: String?,
  messages: List<AiChatMessage>,
  suggestedQuestions: List<String>,
  isSending: Boolean,
  sheetState: SheetState,
  onDismissRequest: () -> Unit,
  onSendMessage: (String) -> Unit,
  onSuggestionClick: (String) -> Unit,
  onRetry: () -> Unit,
  modifier: Modifier = Modifier,
  onClearHistory: (() -> Unit)? = null
) {
  var inputText by remember { mutableStateOf("") }
  val listState = rememberLazyListState()
  val keyboardController = LocalSoftwareKeyboardController.current
  val focusRequester = remember { FocusRequester() }
  val scope = rememberCoroutineScope()

  LaunchedEffect(Unit) {
    scope.launch {
      sheetState.expand()
    }
  }

  LaunchedEffect(messages.size, isSending, suggestedQuestions.size) {
    if (messages.isNotEmpty() || isSending) {
      scope.launch {
        delay(200)
        val totalItems = listState.layoutInfo.totalItemsCount
        if (totalItems > 0) {
          listState.animateScrollToItem(totalItems - 1)
        }
      }
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    sheetState = sheetState,
    containerColor = DarkSurface,
    dragHandle = { BottomSheetDefaults.DragHandle(color = TextGrey) },
    contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
    modifier = modifier
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.7f)
        .statusBarsPadding()
        .navigationBarsPadding()
        .imePadding()
        .padding(horizontal = 16.dp)
        .padding(bottom = 8.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 12.dp)
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = MainColor,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
          text = title,
          color = TextPrimary,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.weight(1f)
        )
        if (onClearHistory != null && messages.isNotEmpty()) {
          IconButton(onClick = onClearHistory) {
            Icon(
              imageVector = Icons.Default.DeleteOutline,
              contentDescription = stringResource(R.string.clear_history),
              tint = TextSecondary
            )
          }
        }
        IconButton(onClick = onDismissRequest) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(R.string.close),
            tint = TextSecondary
          )
        }
      }

      if (error != null && messages.isEmpty()) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Text(text = error, color = TextSecondary, textAlign = TextAlign.Center)
          Spacer(modifier = Modifier.height(16.dp))
          androidx.compose.material3.TextButton(onClick = onRetry) {
            Text(stringResource(R.string.retry), color = MainColor)
          }
        }
      } else {
        LazyColumn(
          state = listState,
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          if (isLoading && messages.isEmpty()) {
            item {
              AiLoadingSkeleton()
            }
          }

          items(messages) { message ->
            if (message.isLoading) {
              AiLoadingIndicator()
            } else {
              AiMessageBubble(
                content = message.content,
                isFromUser = message.isFromUser
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (suggestedQuestions.isNotEmpty()) {
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
          ) {
            items(suggestedQuestions) { question ->
              SuggestionChip(
                text = question,
                onClick = {
                  onSuggestionClick(question)
                  keyboardController?.hide()
                }
              )
            }
          }
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            modifier = Modifier
              .weight(1f)
              .focusRequester(focusRequester)
              .onFocusChanged {
                if (it.isFocused) {
                  scope.launch {
                    sheetState.expand()
                  }
                }
              },
            placeholder = {
              Text(
                stringResource(R.string.ai_chat_placeholder),
                color = TextGrey,
                fontSize = 14.sp
              )
            },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = MainColor,
              unfocusedBorderColor = TextGrey.copy(alpha = 0.3f),
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
              cursorColor = MainColor
            ),
            shape = RoundedCornerShape(24.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(
              onSend = {
                if (inputText.isNotBlank() && !isSending) {
                  onSendMessage(inputText)
                  inputText = ""
                  keyboardController?.hide()
                }
              }
            ),
            singleLine = true
          )
          Spacer(modifier = Modifier.width(8.dp))
          IconButton(
            onClick = {
              if (inputText.isNotBlank() && !isSending) {
                onSendMessage(inputText)
                inputText = ""
                keyboardController?.hide()
              }
            },
            enabled = inputText.isNotBlank() && !isSending
          ) {
            if (isSending) {
              CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MainColor,
                strokeWidth = 2.dp
              )
            } else {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = stringResource(R.string.send),
                tint = if (inputText.isNotBlank()) MainColor else TextGrey
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun AiMessageBubble(
  content: String,
  isFromUser: Boolean,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = if (isFromUser) Arrangement.End else Arrangement.Start
  ) {
    if (!isFromUser) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(MainColor.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = MainColor,
          modifier = Modifier.size(18.dp)
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
    }

    Column(
      modifier = Modifier
        .then(if (isFromUser) Modifier.widthIn(max = 280.dp) else Modifier.weight(1f))
        .clip(
          RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = if (isFromUser) 16.dp else 4.dp,
            bottomEnd = if (isFromUser) 4.dp else 16.dp
          )
        )
        .background(
          if (isFromUser) {
            MainColor.copy(alpha = 0.15f)
          } else {
            DarkSurface.copy(alpha = 0.8f)
          }
        )
        .border(
          1.dp,
          if (isFromUser) {
            MainColor.copy(alpha = 0.3f)
          } else {
            TextGrey.copy(alpha = 0.2f)
          },
          RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = if (isFromUser) 16.dp else 4.dp,
            bottomEnd = if (isFromUser) 4.dp else 16.dp
          )
        )
        .padding(12.dp)
    ) {
      if (isFromUser) {
        Text(
          text = content,
          color = TextPrimary,
          fontSize = 14.sp
        )
      } else {
        MarkdownText(
          markdown = content,
          style = MaterialTheme.typography.bodyMedium.copy(
            color = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp
          ),
          linkColor = GithubBlue,
          syntaxHighlightColor = Color.Transparent,
          syntaxHighlightTextColor = TextPrimary
        )
      }
    }
  }
}

@Composable
private fun SuggestionChip(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(16.dp),
    color = MainColor.copy(alpha = 0.1f),
    border = androidx.compose.foundation.BorderStroke(1.dp, MainColor.copy(alpha = 0.3f))
  ) {
    Text(
      text = text,
      color = MainColor,
      fontSize = 13.sp,
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
    )
  }
}

@Composable
private fun AiLoadingIndicator(modifier: Modifier = Modifier) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.Start
  ) {
    Box(
      modifier = Modifier
        .size(32.dp)
        .clip(CircleShape)
        .background(MainColor.copy(alpha = 0.2f)),
      contentAlignment = Alignment.Center
    ) {
      CircularProgressIndicator(
        modifier = Modifier.size(18.dp),
        color = MainColor,
        strokeWidth = 2.dp
      )
    }
  }
}

@Composable
private fun AiLoadingSkeleton(modifier: Modifier = Modifier) {
  Column(modifier = modifier.fillMaxWidth()) {
    Box(
      modifier = Modifier
        .fillMaxWidth(0.7f)
        .height(16.dp)
        .clip(RoundedCornerShape(4.dp))
        .shimmerEffect()
    )
    Spacer(modifier = Modifier.height(8.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth(0.5f)
        .height(16.dp)
        .clip(RoundedCornerShape(4.dp))
        .shimmerEffect()
    )
  }
}
