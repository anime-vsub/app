package git.shin.animevsub.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.currentState
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
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import coil.Coil
import coil.request.ImageRequest
import coil.request.SuccessResult
import dagger.hilt.android.EntryPointAccessors
import git.shin.animevsub.MainActivity
import git.shin.animevsub.R
import git.shin.animevsub.data.model.HistoryItem

class HistoryWidget : GlanceAppWidget() {

  companion object {
    val KEY_CAROUSEL_MODE = booleanPreferencesKey("carousel_mode")
    suspend fun refresh(context: Context) {
      HistoryWidget().updateAll(context)
    }
  }

  override val sizeMode: SizeMode = SizeMode.Exact
  override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    val historyRepository = EntryPointAccessors.fromApplication(
      context,
      WidgetEntryPoint::class.java
    ).historyRepository()

    val history = historyRepository.getHistory(1).getOrNull() ?: emptyList()

    val posterBitmaps = history.take(10).associate { item ->
      item.seasonId to try {
        val loader = Coil.imageLoader(context)
        val isCarouselMode = id.let {
          val prefs = androidx.glance.appwidget.state.getAppWidgetState(context, PreferencesGlanceStateDefinition, it)
          prefs[KEY_CAROUSEL_MODE] ?: false
        }
        val request = ImageRequest.Builder(context)
          .data(item.poster)
          .size(if (isCarouselMode) 400 else 200, if (isCarouselMode) 225 else 300)
          .build()
        (loader.execute(request) as? SuccessResult)?.drawable?.toBitmap()
      } catch (e: Exception) {
        null
      }
    }

    provideContent {
      val prefs = currentState<Preferences>()
      val isCarousel = prefs[KEY_CAROUSEL_MODE] ?: false
      GlanceTheme {
        WidgetContent(context, history, posterBitmaps, isCarousel)
      }
    }
  }

  @Composable
  private fun WidgetContent(
    context: Context,
    history: List<HistoryItem>,
    posters: Map<String, Bitmap?>,
    isCarousel: Boolean
  ) {
    val widgetSize = LocalSize.current

    Column(
      modifier = GlanceModifier
        .fillMaxSize()
        .background(ColorProvider(Color(0xFF141E33)))
        .padding(8.dp)
    ) {
      Row(
        modifier = GlanceModifier.fillMaxWidth().padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = context.getString(R.string.history),
          style = TextStyle(
            color = ColorProvider(Color.White),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          ),
          modifier = GlanceModifier.defaultWeight()
        )

        Image(
          provider = ImageProvider(if (isCarousel) R.drawable.ic_view_list else R.drawable.ic_view_carousel),
          contentDescription = "Toggle Mode",
          modifier = GlanceModifier
            .size(24.dp)
            .clickable(actionRunCallback<ToggleModeAction>()),
          colorFilter = androidx.glance.ColorFilter.tint(ColorProvider(Color.White))
        )
      }

      if (history.isNotEmpty()) {
        if (isCarousel) {
          val itemsToShow = if (widgetSize.width > 300.dp) history.take(3) else history.take(2)
          val availableHeight = widgetSize.height - 48.dp // Header + Padding
          val itemHeight = (availableHeight - 55.dp).coerceAtLeast(60.dp) // Leave space for text
          val itemWidth = itemHeight * 16 / 9

          Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            itemsToShow.forEachIndexed { index, item ->
              HistoryCarouselItem(context, item, posters[item.seasonId], itemWidth, itemHeight)
              if (index < itemsToShow.size - 1) {
                Spacer(modifier = GlanceModifier.width(8.dp))
              }
            }
          }
        } else {
          LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
            items(history) { item ->
              HistoryRow(context, item, posters[item.seasonId])
            }
          }
        }
      } else {
        Box(
          modifier = GlanceModifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = context.getString(R.string.no_history),
            style = TextStyle(color = ColorProvider(Color.Gray), fontSize = 12.sp)
          )
        }
      }
    }
  }

  @Composable
  private fun HistoryRow(
    context: Context,
    item: HistoryItem,
    poster: Bitmap?
  ) {
    val clickAction = actionStartActivity(
      Intent(context, MainActivity::class.java).apply {
        action = "PLAY_ANIME"
        putExtra("animeId", item.seasonId)
        putExtra("chapterId", item.chapId)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      }
    )

    Row(
      modifier = GlanceModifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)
        .clickable(clickAction),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (poster != null) {
        Image(
          provider = ImageProvider(poster),
          contentDescription = item.name,
          modifier = GlanceModifier
            .size(width = 45.dp, height = 65.dp)
            .cornerRadius(4.dp),
          contentScale = ContentScale.Crop
        )
      } else {
        Box(
          modifier = GlanceModifier
            .size(width = 45.dp, height = 65.dp)
            .cornerRadius(4.dp)
            .background(ColorProvider(Color(0xFF1E2D4A)))
        ) {}
      }

      Spacer(modifier = GlanceModifier.width(8.dp))

      Column(modifier = GlanceModifier.defaultWeight()) {
        Text(
          text = item.name,
          style = TextStyle(
            color = ColorProvider(Color.White),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
          ),
          maxLines = 1
        )
        Text(
          text = context.getString(
            R.string.history_subtitle,
            item.seasonName,
            item.chapName ?: ""
          ),
          style = TextStyle(
            color = ColorProvider(Color(0xFFB0B0B0)),
            fontSize = 12.sp
          ),
          maxLines = 1
        )
        Spacer(modifier = GlanceModifier.height(4.dp))
        val progress = if (item.dur > 0) (item.cur / item.dur).toFloat() else 0f
        LinearProgressIndicator(
          progress = progress,
          modifier = GlanceModifier.fillMaxWidth().height(3.dp),
          color = ColorProvider(Color(0xFF6C63FF)),
          backgroundColor = ColorProvider(Color(0xFF3A4A6B))
        )
      }
    }
  }

  @Composable
  private fun HistoryCarouselItem(
    context: Context,
    item: HistoryItem,
    poster: Bitmap?,
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp
  ) {
    val clickAction = actionStartActivity(
      Intent(context, MainActivity::class.java).apply {
        action = "PLAY_ANIME"
        putExtra("animeId", item.seasonId)
        putExtra("chapterId", item.chapId)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      }
    )

    Column(
      modifier = GlanceModifier
        .width(width)
        .clickable(clickAction)
    ) {
      Box(modifier = GlanceModifier.width(width).height(height).cornerRadius(8.dp)) {
        if (poster != null) {
          Image(
            provider = ImageProvider(poster),
            contentDescription = item.name,
            modifier = GlanceModifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )
        } else {
          Box(
            modifier = GlanceModifier
              .fillMaxSize()
              .background(ColorProvider(Color(0xFF1E2D4A)))
          ) {}
        }

        val progress = if (item.dur > 0) (item.cur / item.dur).toFloat() else 0f
        if (progress > 0) {
          Box(
            modifier = GlanceModifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
          ) {
            LinearProgressIndicator(
              progress = progress,
              modifier = GlanceModifier.fillMaxWidth().height(3.dp),
              color = ColorProvider(Color(0xFF6C63FF)),
              backgroundColor = ColorProvider(Color(0x80000000))
            )
          }
        }
      }
      Spacer(modifier = GlanceModifier.height(4.dp))
      Text(
        text = item.name,
        style = TextStyle(color = ColorProvider(Color.White), fontSize = 12.sp),
        maxLines = 1
      )
      Text(
        text = "Ep ${item.chapName ?: ""}",
        style = TextStyle(color = ColorProvider(Color.Gray), fontSize = 10.sp),
        maxLines = 1
      )
    }
  }
}

class ToggleModeAction : ActionCallback {
  override suspend fun onAction(
    context: Context,
    glanceId: GlanceId,
    parameters: ActionParameters
  ) {
    updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
      val current = prefs[HistoryWidget.KEY_CAROUSEL_MODE] ?: false
      prefs.toMutablePreferences().apply {
        set(HistoryWidget.KEY_CAROUSEL_MODE, !current)
      }
    }
    HistoryWidget().update(context, glanceId)
  }
}

class HistoryWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget = HistoryWidget()
}
