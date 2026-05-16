package git.shin.animevsub.ui.utils

import android.content.Context
import android.text.format.DateUtils
import git.shin.animevsub.R
import java.time.Instant

fun formatRelativeTime(context: Context, instant: Instant): String {
  val now = Instant.now()
  val diffMillis = java.time.Duration.between(instant, now).toMillis()

  return when {
    diffMillis < 0 -> context.getString(R.string.time_just_now)
    diffMillis < DateUtils.MINUTE_IN_MILLIS -> context.getString(R.string.time_just_now)
    diffMillis < DateUtils.HOUR_IN_MILLIS -> context.getString(R.string.time_minutes_ago, diffMillis / DateUtils.MINUTE_IN_MILLIS)
    diffMillis < DateUtils.DAY_IN_MILLIS -> context.getString(R.string.time_hours_ago, diffMillis / DateUtils.HOUR_IN_MILLIS)
    diffMillis < DateUtils.WEEK_IN_MILLIS -> context.getString(R.string.time_days_ago, diffMillis / DateUtils.DAY_IN_MILLIS)
    diffMillis < 4 * DateUtils.WEEK_IN_MILLIS -> context.getString(R.string.time_weeks_ago, diffMillis / DateUtils.WEEK_IN_MILLIS)
    else -> DateUtils.getRelativeTimeSpanString(instant.toEpochMilli(), now.toEpochMilli(), DateUtils.DAY_IN_MILLIS).toString()
  }
}
