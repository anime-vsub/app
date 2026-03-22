package git.shin.animevsub.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import git.shin.animevsub.R
import git.shin.animevsub.data.model.AnimeDetail
import git.shin.animevsub.ui.components.*
import git.shin.animevsub.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailBottomSheet(
    detail: AnimeDetail,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onNavigateToCategory: (String, String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = DarkSurface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = TextGrey) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = detail.image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(120.dp)
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = detail.name,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Info section
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        InfoRow(
                            label = stringResource(R.string.language_label),
                            value = detail.language,
                            textStyle = DetailSmallTextStyle
                        )
                        InfoRow(
                            label = stringResource(R.string.country_label),
                            value = detail.countries.firstOrNull()?.name,
                            textStyle = DetailSmallTextStyle,
                            onClick = {
                                detail.countries.firstOrNull()?.let {
                                    onNavigateToCategory(
                                        "quoc-gia", it.id.removePrefix("/quoc-gia/").trim('/')
                                    )
                                }
                            }
                        )
                        InfoRow(
                            label = stringResource(R.string.year_label),
                            value = detail.yearOf?.toString(),
                            textStyle = DetailSmallTextStyle,
                            onClick = {
                                detail.yearOf?.let {
                                    onNavigateToCategory(
                                        "nam-phat-hanh", it.toString()
                                    )
                                }
                            }
                        )
                        InfoRow(
                            label = stringResource(R.string.duration_label),
                            value = detail.duration,
                            textStyle = DetailSmallTextStyle
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!detail.othername.isNullOrEmpty()) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = TextSecondary)) {
                            append("${stringResource(R.string.other_name)}: ")
                        }
                        withStyle(style = SpanStyle(color = TextPrimary)) {
                            append(detail.othername!!)
                        }
                    },
                    fontSize = 14.sp,
                    style = NoPaddingTextStyle,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Tags
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                detail.genre.forEach { genre ->
                    Text(
                        text = "#${genre.name}",
                        color = Color(0xFF00D639),
                        fontSize = 12.sp,
                        modifier = Modifier
                            .background(DarkCard, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .clickable {
                                onNavigateToCategory(
                                    "the-loai", genre.id.removePrefix("/the-loai/").trim('/')
                                )
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.description),
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = detail.description,
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            if (detail.trailer != null) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.trailer_title),
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black)
                ) {
                    AndroidView(
                        factory = { ctx ->
                            val webView = android.webkit.WebView(ctx).apply {
                                settings.javaScriptEnabled = true
                                settings.loadWithOverviewMode = true
                                settings.useWideViewPort = true
                                webViewClient = android.webkit.WebViewClient()
                            }
                            webView.loadUrl(detail.trailer!!)
                            webView
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
