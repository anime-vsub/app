package git.shin.animevsub.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import coil.Coil
import coil.request.ImageRequest
import coil.request.SuccessResult
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import git.shin.animevsub.MainActivity
import git.shin.animevsub.R
import git.shin.animevsub.data.model.HistoryItem
import git.shin.animevsub.data.repository.HistoryRepository

class ContinueWatchingWidget : GlanceAppWidget() {

  companion object {
    suspend fun refresh(context: Context) {
      ContinueWatchingWidget().updateAll(context)
    }
  }

  override val sizeMode: SizeMode = SizeMode.Exact

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    val historyRepository = EntryPointAccessors.fromApplication(
      context,
      WidgetEntryPoint::class.java
    ).historyRepository()

    val history = historyRepository.getHistory(1, 1).getOrNull()?.firstOrNull()

    val posterBitmap = history?.poster?.let { url ->
      try {
        val loader = Coil.imageLoader(context)
        val request = ImageRequest.Builder(context)
          .data(url)
          .size(300, 450)
          .build()
        (loader.execute(request) as? SuccessResult)?.drawable?.toBitmap()
      } catch (e: Exception) {
        null
      }
    }

    provideContent {
      GlanceTheme {
        WidgetContent(context, history, posterBitmap)
      }
    }
  }

  private fun formatDuration(seconds: Double): String {
    val s = seconds.toInt()
    val h = s / 3600
    val m = (s % 3600) / 60
    val sec = s % 60
    return if (h > 0) {
      "%d:%02d:%02d".format(h, m, sec)
    } else {
      "%02d:%02d".format(m, sec)
    }
  }

  @Composable
  private fun WidgetContent(context: Context, history: HistoryItem?, poster: Bitmap?) {
    val widgetSize = LocalSize.current
    val isSmall = widgetSize.width < 150.dp
    val isLarge = widgetSize.width > 250.dp
    val isShort = widgetSize.height < 110.dp

    val clickAction = if (history != null) {
      val intent = Intent(context, MainActivity::class.java).apply {
        action = "PLAY_ANIME"
        putExtra("animeId", history.seasonId)
        putExtra("chapterId", history.chapId)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      }
      actionStartActivity(intent)
    } else {
      actionStartActivity(Intent(context, MainActivity::class.java))
    }

    Column(
      modifier = GlanceModifier
        .fillMaxSize()
        .background(ColorProvider(Color(0xFF141E33)))
        .padding(if (isSmall || isShort) 8.dp else 16.dp)
        .clickable(clickAction),
      verticalAlignment = Alignment.CenterVertically,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      if (history != null) {
        Row(
          modifier = GlanceModifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Poster with dynamic size
          val posterWidth = when {
            isLarge -> 110.dp
            isSmall || isShort -> 50.dp
            else -> 85.dp
          }
          val posterHeight = when {
            isLarge -> 160.dp
            isSmall || isShort -> 75.dp
            else -> 125.dp
          }

          if (poster != null) {
            Image(
              provider = ImageProvider(poster),
              contentDescription = history.name,
              modifier = GlanceModifier
                .width(posterWidth)
                .height(posterHeight)
                .cornerRadius(8.dp)
                .background(ColorProvider(Color(0xFF1E2D4A))),
              contentScale = ContentScale.Crop
            )
          } else {
            Box(
              modifier = GlanceModifier
                .width(posterWidth)
                .height(posterHeight)
                .cornerRadius(8.dp)
                .background(ColorProvider(Color(0xFF1E2D4A))),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "▶",
                style = TextStyle(
                  color = ColorProvider(Color.White),
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold
                )
              )
            }
          }

          Spacer(modifier = GlanceModifier.width(12.dp))

          Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
              text = history.name,
              style = TextStyle(
                color = ColorProvider(Color.White),
                fontSize = if (isLarge) {
                  20.sp
                } else if (isSmall) {
                  14.sp
                } else {
                  16.sp
                },
                fontWeight = FontWeight.Bold
              ),
              maxLines = if (isLarge) 2 else 1
            )
            Text(
              text = context.getString(
                R.string.history_subtitle,
                history.seasonName,
                history.chapName ?: ""
              ),
              style = TextStyle(
                color = ColorProvider(Color(0xFFB0B0B0)),
                fontSize = if (isLarge) {
                  16.sp
                } else if (isSmall) {
                  12.sp
                } else {
                  14.sp
                }
              ),
              maxLines = 1
            )

            Spacer(modifier = GlanceModifier.height(if (isLarge) 12.dp else 4.dp))

            Text(
              text = "${formatDuration(history.cur)} / ${formatDuration(history.dur)}",
              style = TextStyle(
                color = ColorProvider(Color(0xFF9E9E9E)),
                fontSize = if (isLarge) {
                  14.sp
                } else if (isSmall) {
                  11.sp
                } else {
                  12.sp
                }
              )
            )

            Spacer(modifier = GlanceModifier.height(if (isLarge) 12.dp else 8.dp))

            // Progress Bar
            val progress = if (history.dur > 0) (history.cur / history.dur).toFloat() else 0f
            LinearProgressIndicator(
              progress = progress,
              modifier = GlanceModifier.fillMaxWidth().height(if (isLarge) 6.dp else 4.dp),
              color = ColorProvider(Color(0xFF6C63FF)),
              backgroundColor = ColorProvider(Color(0xFF3A4A6B))
            )
          }
        }
      } else {
        Column(
          modifier = GlanceModifier.fillMaxSize(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = GlanceModifier
              .size(if (isSmall || isShort) 40.dp else 56.dp)
              .cornerRadius(28.dp)
              .background(ColorProvider(Color(0xFF1E2D4A))),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "🎬",
              style = TextStyle(fontSize = if (isSmall || isShort) 20.sp else 28.sp)
            )
          }
          Spacer(modifier = GlanceModifier.height(if (isSmall || isShort) 8.dp else 12.dp))
          Text(
            text = context.getString(R.string.no_anime_watched),
            style = TextStyle(
              color = ColorProvider(Color.White),
              fontSize = if (isSmall || isShort) 12.sp else 14.sp,
              fontWeight = FontWeight.Medium,
              textAlign = TextAlign.Center
            )
          )
          if (!isShort) {
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
              text = context.getString(R.string.tap_to_open_app),
              style = TextStyle(
                color = ColorProvider(Color(0xFF9E9E9E)),
                fontSize = if (isSmall) 10.sp else 12.sp,
                textAlign = TextAlign.Center
              )
            )
          }
        }
      }
    }
  }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
  fun historyRepository(): HistoryRepository
}

class ContinueWatchingWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget = ContinueWatchingWidget()
}
