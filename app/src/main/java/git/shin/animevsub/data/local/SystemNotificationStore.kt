package git.shin.animevsub.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import git.shin.animevsub.data.model.SystemNotification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.systemNotificationDataStore: DataStore<Preferences> by preferencesDataStore(name = "system_notifications")

@Singleton
class SystemNotificationStore @Inject constructor(
  @ApplicationContext private val context: Context,
  private val json: Json
) {
  private val notificationsKey = stringPreferencesKey("system_notifications_list")

  val notifications: Flow<List<SystemNotification>> = context.systemNotificationDataStore.data.map { prefs ->
    val jsonString = prefs[notificationsKey] ?: return@map emptyList()
    try {
      json.decodeFromString<List<SystemNotification>>(jsonString)
    } catch (e: Exception) {
      emptyList()
    }
  }

  private fun Preferences.getCurrentList(): List<SystemNotification> {
    val jsonString = this[notificationsKey] ?: return emptyList()
    return try {
      json.decodeFromString(jsonString)
    } catch (e: Exception) {
      emptyList()
    }
  }

  suspend fun save(notification: SystemNotification) {
    context.systemNotificationDataStore.edit { prefs ->
      val currentList = prefs.getCurrentList()
      val updatedList = listOf(notification) + currentList.filter { it.id != notification.id }
      prefs[notificationsKey] = json.encodeToString(updatedList)
    }
  }

  suspend fun saveAll(notifications: List<SystemNotification>) {
    context.systemNotificationDataStore.edit { prefs ->
      prefs[notificationsKey] = json.encodeToString(notifications)
    }
  }

  suspend fun markAsRead(id: String) {
    context.systemNotificationDataStore.edit { prefs ->
      val currentList = prefs.getCurrentList()
      val updatedList = currentList.map { if (it.id == id) it.copy(isRead = true) else it }
      prefs[notificationsKey] = json.encodeToString(updatedList)
    }
  }

  suspend fun delete(id: String) {
    context.systemNotificationDataStore.edit { prefs ->
      val currentList = prefs.getCurrentList()
      val updatedList = currentList.filter { it.id != id }
      prefs[notificationsKey] = json.encodeToString(updatedList)
    }
  }

  suspend fun clearAll() {
    context.systemNotificationDataStore.edit { prefs ->
      prefs.remove(notificationsKey)
    }
  }

  suspend fun getUnreadCount(): Int {
    val prefs = context.systemNotificationDataStore.data.first()
    return prefs.getCurrentList().count { !it.isRead }
  }
}
