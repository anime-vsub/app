package git.shin.animevsub.ui.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import git.shin.animevsub.R
import git.shin.animevsub.ui.theme.AccentMain
import git.shin.animevsub.ui.theme.DarkBackground
import git.shin.animevsub.ui.theme.DarkCard
import git.shin.animevsub.ui.theme.ErrorColor
import git.shin.animevsub.ui.theme.TextGrey
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
  onNavigateBack: () -> Unit,
  viewModel: LoginViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsState()
  val focusManager = LocalFocusManager.current
  var passwordVisible by remember { mutableStateOf(false) }

  // Navigate back on success
  LaunchedEffect(uiState.isSuccess) {
    if (uiState.isSuccess) {
      onNavigateBack()
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = stringResource(R.string.login),
            color = TextPrimary
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(R.string.back),
              tint = TextPrimary
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
      )
    },
    containerColor = DarkBackground
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(48.dp))

      // Logo / Title
      Text(
        text = stringResource(R.string.app_name),
        color = AccentMain,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = stringResource(R.string.login_required),
        color = TextSecondary,
        fontSize = 14.sp
      )

      Spacer(modifier = Modifier.height(48.dp))

      // Email field
      OutlinedTextField(
        value = uiState.email,
        onValueChange = { viewModel.onEmailChange(it) },
        label = { Text(stringResource(R.string.email)) },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Email,
            contentDescription = null,
            tint = TextGrey
          )
        },
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = TextPrimary,
          unfocusedTextColor = TextPrimary,
          focusedBorderColor = AccentMain,
          unfocusedBorderColor = DarkCard,
          focusedLabelColor = AccentMain,
          unfocusedLabelColor = TextGrey,
          cursorColor = AccentMain
        ),
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Email,
          imeAction = ImeAction.Next
        ),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Password field
      OutlinedTextField(
        value = uiState.password,
        onValueChange = { viewModel.onPasswordChange(it) },
        label = { Text(stringResource(R.string.password)) },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = TextGrey
          )
        },
        trailingIcon = {
          IconButton(onClick = { passwordVisible = !passwordVisible }) {
            Icon(
              imageVector = if (passwordVisible) Icons.Default.VisibilityOff
              else Icons.Default.Visibility,
              contentDescription = null,
              tint = TextGrey
            )
          }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None
        else PasswordVisualTransformation(),
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = TextPrimary,
          unfocusedTextColor = TextPrimary,
          focusedBorderColor = AccentMain,
          unfocusedBorderColor = DarkCard,
          focusedLabelColor = AccentMain,
          unfocusedLabelColor = TextGrey,
          cursorColor = AccentMain
        ),
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Password,
          imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
          onDone = {
            focusManager.clearFocus()
            viewModel.login()
          }
        ),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
      )

      // Error message
      if (uiState.error != null) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = uiState.error!!,
          color = ErrorColor,
          fontSize = 13.sp
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Login button
      Button(
        onClick = { viewModel.login() },
        enabled = !uiState.isLoading,
        colors = ButtonDefaults.buttonColors(containerColor = AccentMain),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
      ) {
        if (uiState.isLoading) {
          CircularProgressIndicator(
            color = Color.White,
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.dp
          )
        } else {
          Text(
            text = stringResource(R.string.login),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }
  }
}
