package git.shin.animevsub.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import git.shin.animevsub.R
import java.util.Calendar

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
