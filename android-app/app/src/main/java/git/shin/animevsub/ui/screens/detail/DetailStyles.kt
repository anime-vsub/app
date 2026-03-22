package git.shin.animevsub.ui.screens.detail

import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

val NoPaddingTextStyle = TextStyle(
    platformStyle = PlatformTextStyle(includeFontPadding = false)
)

val DetailSmallTextStyle = NoPaddingTextStyle.copy(
    lineHeight = 14.sp
)
