package git.shin.animevsub.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import git.shin.animevsub.ui.theme.TextPrimary
import git.shin.animevsub.ui.theme.TextSecondary

@Composable
fun InfoRow(
    label: String,
    value: String?,
    fontSize: TextUnit = 12.sp,
    textStyle: TextStyle = TextStyle.Default,
    onClick: (() -> Unit)? = null
) {
    if (!value.isNullOrEmpty()) {
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = TextSecondary)) {
                    append("$label: ")
                }
                withStyle(style = SpanStyle(color = if (onClick != null) Color(0xFF00D639) else TextPrimary)) {
                    append(value)
                }
            },
            fontSize = fontSize,
            style = textStyle,
            modifier = Modifier
                .padding(vertical = 2.dp)
                .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
        )
    }
}
