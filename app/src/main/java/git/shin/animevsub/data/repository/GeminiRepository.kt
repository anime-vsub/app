package git.shin.animevsub.data.repository

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.ai.client.generativeai.type.content
import git.shin.animevsub.data.local.ApiStorage
import git.shin.animevsub.data.local.PreferencesManager
import git.shin.animevsub.data.model.DbNotificationItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

data class ChatMessage(
  val role: String,
  val content: String
)

data class ChatContext(
  val animeName: String,
  val otherName: String? = null,
  val episodeName: String?,
  val currentTimestamp: Long? = null,
  val language: String
)

data class AiChatResponse(
  val content: String,
  val suggestions: List<String> = emptyList()
)

enum class AiProvider {
  GEMINI,
  OPENAI,
  CLAUDE
}

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
    return if (model.isBlank()) "gemini-flash-lite-latest" else model
  }

  private suspend fun getAiProvider(): AiProvider {
    val provider = prefs.aiProvider.first()
    return when (provider.lowercase()) {
      "openai" -> AiProvider.OPENAI
      "claude" -> AiProvider.CLAUDE
      else -> AiProvider.GEMINI
    }
  }

  private suspend fun getOpenAIKey(): String? {
    val key = prefs.openaiApiKey.first()
    return if (key.isBlank()) null else key
  }

  private suspend fun getOpenAIModel(): String {
    val model = prefs.openaiModel.first()
    return if (model.isBlank()) "gpt-4o-mini" else model
  }

  private suspend fun getOpenAIEndpoint(): String {
    val endpoint = prefs.openaiEndpoint.first()
    return if (endpoint.isBlank()) "https://api.openai.com/v1" else endpoint.trimEnd('/')
  }

  private suspend fun getClaudeKey(): String? {
    val key = prefs.claudeApiKey.first()
    return if (key.isBlank()) null else key
  }

  private suspend fun getClaudeModel(): String {
    val model = prefs.claudeModel.first()
    return if (model.isBlank()) "claude-sonnet-4-20250514" else model
  }

  private suspend fun getClaudeEndpoint(): String {
    val endpoint = prefs.claudeEndpoint.first()
    return if (endpoint.isBlank()) "https://api.anthropic.com/v1" else endpoint.trimEnd('/')
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

  suspend fun testOpenAI(apiKey: String, model: String, endpoint: String): Result<String> = withContext(Dispatchers.IO) {
    runCatching {
      val baseUrl = if (endpoint.isBlank()) "https://api.openai.com/v1" else endpoint.trimEnd('/')
      val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

      val requestBody = JSONObject().apply {
        put("model", model)
        put(
          "messages",
          JSONArray().put(
            JSONObject().apply {
              put("role", "user")
              put("content", "Hello, are you working?")
            }
          )
        )
        put("max_tokens", 50)
      }

      val request = Request.Builder()
        .url("$baseUrl/chat/completions")
        .header("Authorization", "Bearer $apiKey")
        .header("Content-Type", "application/json")
        .post(requestBody.toString().toRequestBody("application/json".toMediaTypeOrNull()))
        .build()

      val response = client.newCall(request).execute()
      val body = response.body?.string() ?: throw Exception("Empty response")
      response.close()

      val json = JSONObject(body)
      if (json.has("choices")) {
        val choices = json.getJSONArray("choices")
        if (choices.length() > 0) {
          choices.getJSONObject(0).getJSONObject("message").getString("content")
        } else {
          "Connected but no response"
        }
      } else if (json.has("error")) {
        throw Exception(json.getJSONObject("error").getString("message"))
      } else {
        throw Exception("Unknown response: $body")
      }
    }
  }

  suspend fun testClaude(apiKey: String, model: String, endpoint: String): Result<String> = withContext(Dispatchers.IO) {
    runCatching {
      val baseUrl = if (endpoint.isBlank()) "https://api.anthropic.com/v1" else endpoint.trimEnd('/')
      val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

      val requestBody = JSONObject().apply {
        put("model", model)
        put(
          "messages",
          JSONArray().put(
            JSONObject().apply {
              put("role", "user")
              put("content", "Hello, are you working?")
            }
          )
        )
        put("max_tokens", 50)
      }

      val request = Request.Builder()
        .url("$baseUrl/messages")
        .header("x-api-key", apiKey)
        .header("anthropic-version", "2023-06-01")
        .header("Content-Type", "application/json")
        .post(requestBody.toString().toRequestBody("application/json".toMediaTypeOrNull()))
        .build()

      val response = client.newCall(request).execute()
      val body = response.body?.string() ?: throw Exception("Empty response")
      response.close()

      val json = JSONObject(body)
      if (json.has("content")) {
        val content = json.getJSONArray("content")
        if (content.length() > 0) {
          content.getJSONObject(0).getString("text")
        } else {
          "Connected but no response"
        }
      } else if (json.has("error")) {
        throw Exception(json.getJSONObject("error").getString("message"))
      } else {
        throw Exception("Unknown response: $body")
      }
    }
  }

  suspend fun saveApiKey(apiKey: String) {
    prefs.setGeminiApiKey(apiKey)
  }

  suspend fun saveModel(modelName: String) {
    prefs.setGeminiModel(modelName)
  }

  private suspend fun callAiApi(prompt: String): String = withContext(Dispatchers.IO) {
    when (getAiProvider()) {
      AiProvider.GEMINI -> callGeminiApi(prompt)
      AiProvider.OPENAI -> callOpenAIApi(prompt)
      AiProvider.CLAUDE -> callClaudeApi(prompt)
      else -> throw Exception("Unknown AI provider")
    }
  }

  private suspend fun callGeminiApi(prompt: String): String {
    val apiKey = getApiKey() ?: throw Exception("Gemini API Key is not configured")
    val modelName = getModelName()
    return try {
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

  private suspend fun callOpenAIApi(prompt: String): String {
    val apiKey = getOpenAIKey() ?: throw Exception("OpenAI API Key is not configured")
    val model = getOpenAIModel()
    val baseUrl = getOpenAIEndpoint()

    val requestBody = JSONObject().apply {
      put("model", model)
      put(
        "messages",
        JSONArray().put(
          JSONObject().apply {
            put("role", "user")
            put("content", prompt)
          }
        )
      )
      put("max_tokens", 2048)
    }

    val request = Request.Builder()
      .url("$baseUrl/chat/completions")
      .header("Authorization", "Bearer $apiKey")
      .header("Content-Type", "application/json")
      .post(requestBody.toString().toRequestBody("application/json".toMediaTypeOrNull()))
      .build()

    val response = client.newCall(request).execute()
    val body = response.body?.string() ?: throw Exception("Empty response")
    response.close()

    val json = JSONObject(body)
    if (json.has("choices")) {
      val choices = json.getJSONArray("choices")
      if (choices.length() > 0) {
        return choices.getJSONObject(0).getJSONObject("message").getString("content").replace("```", "").trim()
      } else {
        throw Exception("No response from OpenAI")
      }
    } else if (json.has("error")) {
      throw Exception(json.getJSONObject("error").getString("message"))
    } else {
      throw Exception("Unknown response: $body")
    }
  }

  private suspend fun callClaudeApi(prompt: String): String {
    val apiKey = getClaudeKey() ?: throw Exception("Claude API Key is not configured")
    val model = getClaudeModel()
    val baseUrl = getClaudeEndpoint()

    val requestBody = JSONObject().apply {
      put("model", model)
      put(
        "messages",
        JSONArray().put(
          JSONObject().apply {
            put("role", "user")
            put("content", prompt)
          }
        )
      )
      put("max_tokens", 2048)
    }

    val request = Request.Builder()
      .url("$baseUrl/messages")
      .header("x-api-key", apiKey)
      .header("anthropic-version", "2023-06-01")
      .header("Content-Type", "application/json")
      .post(requestBody.toString().toRequestBody("application/json".toMediaTypeOrNull()))
      .build()

    val response = client.newCall(request).execute()
    val body = response.body?.string() ?: throw Exception("Empty response")
    response.close()

    val json = JSONObject(body)
    if (json.has("content")) {
      val content = json.getJSONArray("content")
      if (content.length() > 0) {
        return content.getJSONObject(0).getString("text").replace("```", "").trim()
      } else {
        throw Exception("No response from Claude")
      }
    } else if (json.has("error")) {
      throw Exception(json.getJSONObject("error").getString("message"))
    } else {
      throw Exception("Unknown response: $body")
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

    return callAiApi(prompt)
  }

  private fun extractSuggestions(response: String): AiChatResponse {
    val tagStart = "<suggestions>"
    val tagEnd = "</suggestions>"
    val startIndex = response.indexOf(tagStart)
    val endIndex = response.indexOf(tagEnd)

    if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
      val content = response.substring(0, startIndex).trim()
      val suggestionsJson = response.substring(startIndex + tagStart.length, endIndex).trim()
      val suggestions = mutableListOf<String>()
      try {
        val jsonArray = JSONArray(suggestionsJson)
        for (i in 0 until jsonArray.length()) {
          suggestions.add(jsonArray.getString(i))
        }
      } catch (e: Exception) {
        Log.e("GeminiRepository", "Error parsing suggestions", e)
      }
      return AiChatResponse(content, suggestions)
    }
    return AiChatResponse(response.trim())
  }

  suspend fun getRecap(
    animeName: String,
    otherName: String? = null,
    episode: String,
    language: String,
    animeId: String? = null,
    chapterId: String? = null,
    includeSuggestions: Boolean = false
  ): AiChatResponse {
    val cacheKey = if (animeId != null && chapterId != null) "ai_recap_${animeId}_$chapterId" else null
    if (!includeSuggestions) {
      cacheKey?.let {
        val cached = storage.get(it)
        if (!cached.isNullOrBlank()) return AiChatResponse(cached)
      }
    }

    val animeIdentity = if (otherName.isNullOrBlank()) "'$animeName'" else "'$animeName' (also known as '$otherName')"
    val prompt = """
            Act as a professional anime fan, summarize the main events that happened in previous episodes of $animeIdentity (up to before episode $episode).
            Focus on key plot points so viewers can catch up.
            Respond in $language. Concise and natural. Use Markdown (bold, lists) to highlight main points. Avoid code blocks. Not need welcome.
            Finally, include a brief note about the next season's release date or the possibility of its production if information is available.
            ${if (includeSuggestions) "\nAt the very end, provide 3 suggested follow-up questions in a JSON array wrapped in <suggestions> tags. Example: <suggestions>[\"Question 1\", \"Question 2\"]</suggestions>" else ""}
    """.trimIndent()

    val response = callAiApi(prompt)
    val result = extractSuggestions(response)
    if (cacheKey != null && !includeSuggestions) {
      storage.set(cacheKey, result.content)
    }
    return result
  }

  suspend fun getEpisodeSummary(
    animeName: String,
    otherName: String? = null,
    episodeName: String,
    timestampMs: Long,
    language: String,
    animeId: String? = null,
    chapterId: String? = null,
    includeSuggestions: Boolean = false
  ): AiChatResponse {
    val minutes = timestampMs / (1000 * 60)
    val cacheKey = if (animeId != null && chapterId != null) "ai_summary_${animeId}_${chapterId}_$minutes" else null
    if (!includeSuggestions) {
      cacheKey?.let {
        val cached = storage.get(it)
        if (!cached.isNullOrBlank()) return AiChatResponse(cached)
      }
    }

    val timestampFormatted = String.format("%02d:%02d", minutes, (timestampMs / 1000) % 60)
    val animeIdentity = if (otherName.isNullOrBlank()) "'$animeName'" else "'$animeName' (also known as '$otherName')"
    val prompt = """
            You are an anime expert. Summarize the current episode '$episodeName' of the anime $animeIdentity up to the point $timestampFormatted.
            Provide a concise summary of the events occurring from the beginning of this episode until this timestamp.
            Use Markdown for formatting. Respond in $language. Be concise and helpful.
            Finally, include a brief note about the next season's release date or the possibility of its production if information is available.
            ${if (includeSuggestions) "\nAt the very end, provide 3 suggested follow-up questions in a JSON array wrapped in <suggestions> tags. Example: <suggestions>[\"Question 1\", \"Question 2\"]</suggestions>" else ""}
    """.trimIndent()

    val response = callAiApi(prompt)
    val result = extractSuggestions(response)
    if (cacheKey != null && !includeSuggestions) {
      storage.set(cacheKey, result.content)
    }
    return result
  }

  suspend fun chatWithAI(
    messages: List<ChatMessage>,
    context: ChatContext
  ): AiChatResponse = withContext(Dispatchers.IO) {
    val systemPrompt = """
      You are an enthusiastic anime assistant helping users understand and enjoy anime.
      Current context:
      - Anime: ${context.animeName} ${context.otherName?.let { "(Other name: $it)" } ?: ""}
      - Episode: ${context.episodeName ?: "N/A"}
      - Current timestamp: ${
      context.currentTimestamp?.let {
        String.format("%02d:%02d", it / (1000 * 60), (it / 1000) % 60)
      } ?: "N/A"
    }
      - Language: ${context.language}

      Guidelines:
      1. Respond in the same language as the user
      2. Be friendly, helpful, and concise
      3. Use Markdown for formatting
      4. Avoid code blocks in responses
      5. If asked about future plot points, politely say you don't know
      6. Keep responses focused on the anime context
      7. At the very end of your response, always provide 3 suggested follow-up questions that the user might want to ask next. Format these suggestions as a JSON array of strings wrapped in <suggestions> tags. Example: <suggestions>["Question 1", "Question 2", "Question 3"]</suggestions>
    """.trimIndent()

    when (getAiProvider()) {
      AiProvider.GEMINI -> chatWithGemini(messages, systemPrompt)
      AiProvider.OPENAI -> chatWithOpenAI(messages, systemPrompt)
      AiProvider.CLAUDE -> chatWithClaude(messages, systemPrompt)
    }
  }

  private suspend fun chatWithGemini(messages: List<ChatMessage>, systemPrompt: String): AiChatResponse {
    val apiKey = getApiKey() ?: throw Exception("Gemini API Key is not configured")
    val modelName = getModelName()

    return try {
      val generativeModel = GenerativeModel(
        modelName = modelName,
        apiKey = apiKey,
        generationConfig = generationConfig {
          temperature = 0.7f
        }
      )

      val contents = buildList {
        add(
          content(role = "user") { text(systemPrompt) }
        )
        messages.forEach { msg ->
          add(
            content(role = if (msg.role == "user") "user" else "model") {
              text(msg.content)
            }
          )
        }
      }

      val response = generativeModel.generateContent(contents)
      val rawText = response.text?.trim() ?: throw Exception("Empty response from AI")
      extractSuggestions(rawText)
    } catch (e: Exception) {
      Log.e("GeminiRepository", "Error in Gemini chat", e)
      throw e
    }
  }

  private suspend fun chatWithOpenAI(messages: List<ChatMessage>, systemPrompt: String): AiChatResponse {
    val apiKey = getOpenAIKey() ?: throw Exception("OpenAI API Key is not configured")
    val model = getOpenAIModel()
    val baseUrl = getOpenAIEndpoint()

    try {
      val openAIMessages = JSONArray().apply {
        put(
          JSONObject().apply {
            put("role", "system")
            put("content", systemPrompt)
          }
        )
        messages.forEach { msg ->
          put(
            JSONObject().apply {
              put("role", if (msg.role == "user") "user" else "assistant")
              put("content", msg.content)
            }
          )
        }
      }

      val requestBody = JSONObject().apply {
        put("model", model)
        put("messages", openAIMessages)
        put("max_tokens", 2048)
      }

      val request = Request.Builder()
        .url("$baseUrl/chat/completions")
        .header("Authorization", "Bearer $apiKey")
        .header("Content-Type", "application/json")
        .post(requestBody.toString().toRequestBody("application/json".toMediaTypeOrNull()))
        .build()

      val response = client.newCall(request).execute()
      val body = response.body?.string() ?: throw Exception("Empty response")
      response.close()

      val json = JSONObject(body)
      if (json.has("choices")) {
        val choices = json.getJSONArray("choices")
        if (choices.length() > 0) {
          val rawText = choices.getJSONObject(0).getJSONObject("message").getString("content").trim()
          return extractSuggestions(rawText)
        } else {
          throw Exception("No response from OpenAI")
        }
      } else if (json.has("error")) {
        throw Exception(json.getJSONObject("error").getString("message"))
      } else {
        throw Exception("Unknown response: $body")
      }
    } catch (e: Exception) {
      Log.e("GeminiRepository", "Error in OpenAI chat", e)
      throw e
    }
  }

  private suspend fun chatWithClaude(messages: List<ChatMessage>, systemPrompt: String): AiChatResponse {
    val apiKey = getClaudeKey() ?: throw Exception("Claude API Key is not configured")
    val model = getClaudeModel()
    val baseUrl = getClaudeEndpoint()

    try {
      val claudeMessages = JSONArray().apply {
        put(
          JSONObject().apply {
            put("role", "user")
            put("content", "System: $systemPrompt")
          }
        )
        messages.forEach { msg ->
          put(
            JSONObject().apply {
              put("role", if (msg.role == "user") "user" else "assistant")
              put("content", msg.content)
            }
          )
        }
      }

      val requestBody = JSONObject().apply {
        put("model", model)
        put("messages", claudeMessages)
        put("max_tokens", 2048)
      }

      val request = Request.Builder()
        .url("$baseUrl/messages")
        .header("x-api-key", apiKey)
        .header("anthropic-version", "2023-06-01")
        .header("Content-Type", "application/json")
        .post(requestBody.toString().toRequestBody("application/json".toMediaTypeOrNull()))
        .build()

      val response = client.newCall(request).execute()
      val body = response.body?.string() ?: throw Exception("Empty response")
      response.close()

      val json = JSONObject(body)
      if (json.has("content")) {
        val content = json.getJSONArray("content")
        if (content.length() > 0) {
          val rawText = content.getJSONObject(0).getString("text").trim()
          return extractSuggestions(rawText)
        } else {
          throw Exception("No response from Claude")
        }
      } else if (json.has("error")) {
        throw Exception(json.getJSONObject("error").getString("message"))
      } else {
        throw Exception("Unknown response: $body")
      }
    } catch (e: Exception) {
      Log.e("GeminiRepository", "Error in Claude chat", e)
      throw e
    }
  }
}
