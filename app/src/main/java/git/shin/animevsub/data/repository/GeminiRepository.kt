package git.shin.animevsub.data.repository

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import git.shin.animevsub.data.local.ApiStorage
import git.shin.animevsub.data.local.PreferencesManager
import git.shin.animevsub.data.model.DbNotificationItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiRepository @Inject constructor(
  private val prefs: PreferencesManager,
  private val storage: ApiStorage
) {
  // OkHttpClient remains for listing models as the SDK doesn't expose this management API yet
  private val client = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .build()

  private suspend fun getApiKey(): String? {
    val key = prefs.geminiApiKey.first()
    return if (key.isBlank()) null else key
  }

  private suspend fun getModelName(): String {
    val model = prefs.geminiModel.first()
    return if (model.isBlank()) "gemini-1.5-flash" else model
  }

  suspend fun listAvailableModels(): List<String> = withContext(Dispatchers.IO) {
    val apiKey = getApiKey() ?: return@withContext emptyList()
    try {
      val url = "https://generativelanguage.googleapis.com/v1beta/models"
      val request = Request.Builder()
        .url(url)
        .header("x-goog-api-key", apiKey)
        .get()
        .build()
      val response = client.newCall(request).execute()
      val body = response.body?.string() ?: return@withContext emptyList()
      response.close()

      val json = JSONObject(body)
      val models = json.getJSONArray("models")
      val result = mutableListOf<String>()
      for (i in 0 until models.length()) {
        val modelObj = models.getJSONObject(i)
        val name = modelObj.getString("name")
        val methods = modelObj.getJSONArray("supportedGenerationMethods")
        var supportsGenerate = false
        for (j in 0 until methods.length()) {
          if (methods.getString(j) == "generateContent") {
            supportsGenerate = true
            break
          }
        }
        if (supportsGenerate && name.contains("gemini") && !name.contains("vision")) {
          result.add(name.substringAfterLast("/"))
        }
      }
      return@withContext result
    } catch (e: Exception) {
      Log.e("GeminiRepository", "Error listing models", e)
      emptyList()
    }
  }

  suspend fun testApiKey(apiKey: String): Result<String> = runCatching {
    val modelName = getModelName()
    val generativeModel = GenerativeModel(
      modelName = modelName,
      apiKey = apiKey
    )
    val response = generativeModel.generateContent("Hello, are you working?")
    response.text ?: "Connect successfully but not respond."
  }

  suspend fun saveApiKey(apiKey: String) {
    prefs.setGeminiApiKey(apiKey)
  }

  suspend fun saveModel(modelName: String) {
    prefs.setGeminiModel(modelName)
  }

  private suspend fun callGeminiApi(prompt: String): String = withContext(Dispatchers.IO) {
    val apiKey = getApiKey() ?: throw Exception("Gemini API Key is not configured")
    val modelName = getModelName()
    try {
      val generativeModel = GenerativeModel(
        modelName = modelName,
        apiKey = apiKey,
        generationConfig = generationConfig {
          temperature = 0.7f
        }
      )
      val response = generativeModel.generateContent(prompt)
      response.text?.replace("```", "")?.trim() ?: throw Exception("Empty response from AI")
    } catch (e: Exception) {
      Log.e("GeminiRepository", "Error calling Gemini API", e)
      throw e
    }
  }

  suspend fun summarizeNotifications(notifications: List<DbNotificationItem>, language: String): String {
    val prompt = """
            You are an enthusiastic anime assistant. Summarize the following notification list in a friendly, concise, and natural way.
            Group related information if possible (e.g., new episodes of the same anime).
            Use Markdown formatting (like bolding anime titles) to make it readable. Avoid code blocks.
            Respond in $language.

            List:
            ${notifications.joinToString("\n") { "- ${it.name}: ${it.episodes.firstOrNull()?.name}" }}
    """.trimIndent()

    return callGeminiApi(prompt)
  }

  suspend fun getRecap(
    animeName: String,
    episode: String,
    language: String,
    animeId: String? = null,
    chapterId: String? = null
  ): String {
    val cacheKey = if (animeId != null && chapterId != null) "ai_recap_${animeId}_$chapterId" else null
    cacheKey?.let {
      val cached = storage.get(it)
      if (!cached.isNullOrBlank()) return cached
    }

    val prompt = """
            Act as a professional anime fan, summarize the main events that happened in previous episodes of '$animeName' (up to before episode $episode).
            Focus on key plot points so viewers can catch up.
            Respond in $language. Concise and natural. Use Markdown (bold, lists) to highlight main points. Avoid code blocks. Not need welcome.
    """.trimIndent()

    val response = callGeminiApi(prompt)
    if (cacheKey != null) {
      storage.set(cacheKey, response)
    }
    return response
  }

  suspend fun getEpisodeSummary(
    animeName: String,
    episodeName: String,
    timestampMs: Long,
    language: String,
    animeId: String? = null,
    chapterId: String? = null
  ): String {
    val minutes = timestampMs / (1000 * 60)
    val cacheKey = if (animeId != null && chapterId != null) "ai_summary_${animeId}_${chapterId}_$minutes" else null
    cacheKey?.let {
      val cached = storage.get(it)
      if (!cached.isNullOrBlank()) return cached
    }

    val timestampFormatted = String.format("%02d:%02d", minutes, (timestampMs / 1000) % 60)
    val prompt = """
            You are an anime expert. Summarize the current episode '$episodeName' of the anime '$animeName' up to the point $timestampFormatted.
            Provide a concise summary of the events occurring from the beginning of this episode until this timestamp.
            Use Markdown for formatting. Respond in $language. Be concise and helpful.
    """.trimIndent()

    val response = callGeminiApi(prompt)
    if (cacheKey != null) {
      storage.set(cacheKey, response)
    }
    return response
  }
}
