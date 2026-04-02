package git.shin.animevsub.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import git.shin.animevsub.R
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary

@Composable
fun SearchScreen(
  onNavigateToDetail: (String) -> Unit,
  viewModel: SearchViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()
  val focusManager = LocalFocusManager.current

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkBackground)
  ) {
    // Search bar
    TextField(
      value = uiState.query,
      onValueChange = { viewModel.onQueryChange(it) },
      placeholder = {
        Text(
          text = stringResource(R.string.search_hint),
          color = TextGrey
        )
      },
      leadingIcon = {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = null,
          tint = TextGrey
        )
      },
      trailingIcon = {
        if (uiState.query.isNotEmpty()) {
          IconButton(onClick = { viewModel.onQueryChange("") }) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = null,
              tint = TextGrey
            )
          }
        }
      },
      colors = TextFieldDefaults.colors(
        focusedContainerColor = DarkCard,
        unfocusedContainerColor = DarkCard,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        cursorColor = AccentMain,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
      ),
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
      keyboardActions = KeyboardActions(
        onSearch = {
          viewModel.onSearch(uiState.query)
          focusManager.clearFocus()
        }
      ),
      singleLine = true,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .clip(RoundedCornerShape(12.dp))
    )

    if (uiState.isLoading) {
      LinearProgressIndicator(
        modifier = Modifier.fillMaxWidth(),
        color = AccentMain
      )
    }

    // Content
    if (uiState.query.length < 2 && uiState.suggestions.isEmpty()) {
      // Show search history
      if (uiState.searchHistory.isNotEmpty()) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = stringResource(R.string.search_history),
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
          )
          TextButton(onClick = { viewModel.clearHistory() }) {
            Text(
              text = stringResource(R.string.clear_history),
              color = AccentMain,
              fontSize = 13.sp
            )
          }
        }

        LazyColumn {
          items(uiState.searchHistory) { item ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  viewModel.onQueryChange(item)
                  viewModel.onSearch(item)
                }
                .padding(horizontal = 16.dp, vertical = 12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = TextGrey,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(12.dp))
              Text(
                text = item,
                color = TextPrimary,
                fontSize = 14.sp
              )
            }
          }
        }
      }
    } else {
      // Show suggestions
      LazyColumn {
        items(uiState.suggestions) { suggestion ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                onNavigateToDetail(suggestion.animeId)
              }
              .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            AsyncImage(
              model = suggestion.image,
              contentDescription = suggestion.name,
              contentScale = ContentScale.Crop,
              modifier = Modifier
                .size(50.dp, 70.dp)
                .clip(RoundedCornerShape(6.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = suggestion.name,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = suggestion.status,
                color = TextSecondary,
                fontSize = 12.sp
              )
            }
          }
        }

        if (uiState.suggestions.isEmpty() && uiState.query.length >= 2 && !uiState.isLoading) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = stringResource(R.string.no_results),
                color = TextSecondary,
                fontSize = 14.sp
              )
            }
          }
        }
      }
    }
  }
}
