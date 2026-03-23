package git.shin.animevsub.ui.styles

import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

val NoPaddingTextStyle = TextStyle(
    platformStyle = PlatformTextStyle(includeFontPadding = false)
)

val SmallTextStyle = NoPaddingTextStyle.copy(
    lineHeight = 14.sp
)
