package com.example.util.simpletimetracker.data_ai

import com.example.util.simpletimetracker.domain.parser.ActivityParser
import com.example.util.simpletimetracker.domain.parser.ApiKeyRepo
import com.example.util.simpletimetracker.domain.parser.model.ParseRequest
import com.example.util.simpletimetracker.domain.parser.model.ParseResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeepSeekParser @Inject constructor(
    private val apiKeyRepo: ApiKeyRepo,
) : ActivityParser {

    // internal 而非 private：单元测试需要注入 MockWebServer
    internal var client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(40, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    internal var baseUrl: String = DEFAULT_BASE_URL

    override suspend fun parse(request: ParseRequest): ParseResult = withContext(Dispatchers.IO) {
        val apiKey = apiKeyRepo.get()
            ?: return@withContext ParseResult.Failed("未配置 API Key")

        val body = buildRequestBody(request)
        val httpRequest = Request.Builder()
            .url("$baseUrl/chat/completions")
            .header("Authorization", "Bearer $apiKey")
            .post(body.toRequestBody(JSON_MEDIA_TYPE))
            .build()

        try {
            client.newCall(httpRequest).execute().use { response ->
                mapResponse(response.code, response.isSuccessful, response.body?.string())
            }
        } catch (e: SocketTimeoutException) {
            ParseResult.Failed("请求超时，稍后重试")
        } catch (e: IOException) {
            ParseResult.Failed("网络不可用：${e.message ?: "未知原因"}")
        }
    }

    private fun mapResponse(code: Int, isSuccessful: Boolean, rawBody: String?): ParseResult {
        return when {
            code == 401 || code == 403 -> ParseResult.Failed("API Key 无效")
            code == 402 -> ParseResult.Failed("账户余额不足")
            code == 429 -> ParseResult.Failed("请求太频繁，稍后重试")
            !isSuccessful -> ParseResult.Failed("服务返回 $code")
            rawBody.isNullOrBlank() -> ParseResult.Failed("服务返回空内容")
            else -> {
                val content = extractContent(rawBody)
                    ?: return ParseResult.Failed("返回格式不符合预期")
                ParseResultMapper.map(content)
            }
        }
    }

    private fun extractContent(rawBody: String): String? {
        return try {
            val json = JSONObject(rawBody)
            json.optJSONArray("choices")
                ?.optJSONObject(0)
                ?.optJSONObject("message")
                ?.optString("content")
                ?.takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            null
        }
    }

    private fun buildRequestBody(request: ParseRequest): String {
        val messages = JSONArray().apply {
            put(
                JSONObject().apply {
                    put("role", "system")
                    put("content", PromptBuilder.system(request.categories))
                },
            )
            put(
                JSONObject().apply {
                    put("role", "user")
                    put("content", PromptBuilder.user(request))
                },
            )
        }

        return JSONObject().apply {
            put("model", MODEL)
            put("messages", messages)
            put("temperature", 0.1)
            put("max_tokens", 300)
            put("response_format", JSONObject().put("type", "json_object"))
        }.toString()
    }

    private companion object {
        const val DEFAULT_BASE_URL = "https://api.deepseek.com"
        const val MODEL = "deepseek-chat"
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}
