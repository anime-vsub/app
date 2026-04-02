package git.shin.animevsub.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import git.shin.animevsub.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun formatNumber(num: Int): String {
  val million = stringResource(R.string.million_suffix)
  val thousand = stringResource(R.string.thousand_suffix)
  return when {
    num >= 1_000_000 -> String.format("%.1f%s", num / 1_000_000.0, million)
    num >= 1_000 -> String.format("%.1f%s", num / 1_000.0, thousand)
    else -> num.toString()
  }
}

@Composable
fun formatScheduleUpdate(update: Triple<Int, Int, Int>): String {
  val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 7 = Saturday
  val updateDayOfWeek =
    if (update.first == 0) 1 else update.first + 1 // Convert 0-6 to Calendar's 1-7

  val time = String.format("%02d:%02d", update.second, update.third)

  val dayText = if (updateDayOfWeek == currentDay) {
    stringResource(R.string.today_text)
  } else {
    if (updateDayOfWeek == Calendar.SUNDAY) {
      stringResource(R.string.sunday_text)
    } else {
      stringResource(R.string.day_of_week_format, updateDayOfWeek)
    }
  }

  val weekText = if (updateDayOfWeek == currentDay) {
    ""
  } else if (updateDayOfWeek > currentDay) {
    stringResource(R.string.this_week_text)
  } else {
    stringResource(R.string.next_week_text)
  }

  return stringResource(R.string.schedule_update_format, time, dayText, weekText)
}

fun formatDuration(durationMs: Long): String {
  val hours = TimeUnit.MILLISECONDS.toHours(durationMs)
  val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs) % 60
  val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60

  return if (hours > 0) {
    String.format("%02d:%02d:%02d", hours, minutes, seconds)
  } else {
    String.format("%02d:%02d", minutes, seconds)
  }
}

fun formatTime(timestampMillis: Long): String {
  val date = Date(timestampMillis)
  val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
  return sdf.format(date)
}

fun formatDayName(timestampMillis: Long): String {
  val date = Date(timestampMillis)
  val sdf = SimpleDateFormat("EEEE", Locale("vi", "VN"))
  return sdf.format(date).replaceFirstChar { it.uppercase() }
}

fun formatShortDayAndDate(timestampMillis: Long): Pair<String, String> {
  val date = Date(timestampMillis)
  val calendar = Calendar.getInstance().apply { time = date }
  val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

  val shortDay = when (dayOfWeek) {
    Calendar.SUNDAY -> "CN"
    else -> "T$dayOfWeek"
  }

  val sdfDate = SimpleDateFormat("dd/MM", Locale.getDefault())
  return Pair(shortDay, sdfDate.format(date))
}

fun isToday(timestampMillis: Long): Boolean {
  val today = Calendar.getInstance()
  val date = Calendar.getInstance().apply { timeInMillis = timestampMillis }
  return today.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
    today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
}
