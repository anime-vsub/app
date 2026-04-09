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
  val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
  return sdf.format(date).replaceFirstChar { it.uppercase() }
}

fun formatShortDayAndDate(timestampMillis: Long): Pair<String, String> {
  val date = Date(timestampMillis)
  val calendar = Calendar.getInstance().apply { time = date }
  val shortDay = when (val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)) {
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

fun parseTimeAgo(timeAgo: String?): Long? {
  if (timeAgo == null) return null
  val now = System.currentTimeMillis()
  val regex = "(\\d+)\\s+(giây|phút|giờ|ngày|tuần|tháng|năm)".toRegex()
  val match = regex.find(timeAgo) ?: return null

  val value = match.groupValues[1].toLong()
  val unit = match.groupValues[2]

  val millis = when (unit) {
    "giây" -> value * 1000
    "phút" -> value * 60 * 1000
    "giờ" -> value * 60 * 60 * 1000
    "ngày" -> value * 24 * 60 * 60 * 1000
    "tuần" -> value * 7 * 24 * 60 * 60 * 1000
    "tháng" -> value * 30 * 24 * 60 * 60 * 1000
    "năm" -> value * 365 * 24 * 60 * 60 * 1000
    else -> 0L
  }

  return now - millis
}

@Composable
fun formatTimeAgo(timestamp: Long?): String {
  if (timestamp == null) return ""
  val now = System.currentTimeMillis()
  val diff = now - timestamp

  return when {
    diff < TimeUnit.MINUTES.toMillis(1) -> stringResource(R.string.just_now)
    diff < TimeUnit.HOURS.toMillis(1) -> stringResource(
      R.string.minutes_ago,
      diff / TimeUnit.MINUTES.toMillis(1)
    )

    diff < TimeUnit.DAYS.toMillis(1) -> stringResource(
      R.string.hours_ago,
      diff / TimeUnit.HOURS.toMillis(1)
    )

    diff < TimeUnit.DAYS.toMillis(30) -> stringResource(
      R.string.days_ago,
      diff / TimeUnit.DAYS.toMillis(1)
    )

    diff < TimeUnit.DAYS.toMillis(365) -> stringResource(
      R.string.months_ago,
      diff / TimeUnit.DAYS.toMillis(30)
    )

    else -> stringResource(R.string.years_ago, diff / TimeUnit.DAYS.toMillis(365))
  }
}
