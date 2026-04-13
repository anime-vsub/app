package git.shin.animevsub.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.apiDataStore: DataStore<Preferences> by preferencesDataStore(name = "api_internal_storage")

class ApiStorage(private val context: Context) {
  fun getString(key: String): Flow<String?> = context.apiDataStore.data.map { it[stringPreferencesKey(key)] }

  suspend fun get(key: String, defaultValue: String? = null): String? = getString(key).first() ?: defaultValue

  suspend fun set(key: String, value: String?) {
    context.apiDataStore.edit { preferences ->
      val prefKey = stringPreferencesKey(key)
      if (value == null) {
        preferences.remove(prefKey)
      } else {
        preferences[prefKey] = value
      }
    }
  }

  suspend fun clear() {
    context.apiDataStore.edit { it.clear() }
  }
}
