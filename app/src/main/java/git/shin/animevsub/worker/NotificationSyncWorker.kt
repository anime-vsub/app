package git.shin.animevsub.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import git.shin.animevsub.R
import git.shin.animevsub.data.model.NotificationItem
import git.shin.animevsub.data.repository.AnimeRepository
import git.shin.animevsub.utils.NotificationHelper
import kotlinx.coroutines.flow.first

@HiltWorker
class NotificationSyncWorker @AssistedInject constructor(
  @Assisted private val context: Context,
  @Assisted params: WorkerParameters,
  private val repository: AnimeRepository
) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    return try {
      if (!repository.enableNotifications.first()) return Result.success()
      if (!repository.isLoggedIn.first()) return Result.success()

      val oldNotifications = repository.notifications.first() ?: repository.getNotifications().getOrNull()
      val oldItems = oldNotifications?.items ?: emptyList()

      val newItems = repository.getNotifications().getOrNull()?.items ?: emptyList()
      handleNewNotifications(oldItems, newItems)

      if (repository.autoSyncNotify.first()) {
        repository.startSyncNotifications().getOrThrow()
      }

      Result.success()
    } catch (e: Exception) {
      print(e)
      if (runAttemptCount < 3) Result.retry() else Result.failure()
    }
  }

  private fun handleNewNotifications(oldItems: List<NotificationItem>, newItems: List<NotificationItem>) {
    val latestOldId = oldItems.firstOrNull()?.id
    val newlyAdded = if (latestOldId == null) {
      newItems.take(1)
    } else {
      newItems.takeWhile { it.id != latestOldId }
    }

    if (newlyAdded.isNotEmpty()) {
      val helper = NotificationHelper(context)
      newlyAdded.forEach { item ->
        helper.showNotification(
          title = context.getString(R.string.new_notification_title),
          message = context.getString(R.string.new_notification_message, item.title, item.content)
        )
      }
    }
  }
}
