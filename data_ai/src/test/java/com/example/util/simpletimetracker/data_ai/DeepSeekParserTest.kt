package com.example.util.simpletimetracker.data_ai

import com.example.util.simpletimetracker.domain.parser.ApiKeyRepo
import com.example.util.simpletimetracker.domain.parser.model.ParseRequest
import com.example.util.simpletimetracker.domain.parser.model.ParseResult
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class DeepSeekParserTest {

    private lateinit var server: MockWebServer
    private lateinit var parser: DeepSeekParser

    private class FakeKeyRepo(private val key: String?) : ApiKeyRepo {
        override fun get(): String? = key
        override fun set(key: String) = Unit
        override fun clear() = Unit
    }

    private fun request() = ParseRequest(
        rawText = "刚才写周报，从两点到两点四十",
        spokenAt = 1_790_000_000_000,
        todayRecords = emptyList(),
        categories = listOf("工作", "学习"),
        currentTimeIso = "2026-11-05T14:20",
    )

    private fun contentResponse(content: String): MockResponse {
        val body = """
            {"choices":[{"message":{"content":${org.json.JSONObject.quote(content)}}}]}
        """.trimIndent()
        return MockResponse().setResponseCode(200).setBody(body)
    }

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        parser = DeepSeekParser(FakeKeyRepo("test-key"))
        parser.baseUrl = server.url("/").toString().trimEnd('/')
        parser.client = OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            .build()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `successful response maps to Parsed`() = runBlocking {
        server.enqueue(
            contentResponse(
                """{"status":"ok","start":"2026-11-05T14:00","end":"2026-11-05T14:40","activity":"写周报","category":"工作"}""",
            ),
        )

        val result = parser.parse(request())

        assertTrue(result is ParseResult.Parsed)
        assertEquals("写周报", (result as ParseResult.Parsed).activity)
    }

    @Test
    fun `sends authorization header`() = runBlocking {
        server.enqueue(contentResponse("""{"status":"failed","reason":"x"}"""))

        parser.parse(request())

        val recorded = server.takeRequest()
        assertEquals("Bearer test-key", recorded.getHeader("Authorization"))
        assertEquals("POST", recorded.method)
    }

    @Test
    fun `401 maps to invalid key`() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(401).setBody("{}"))

        val result = parser.parse(request())

        assertTrue(result is ParseResult.Failed)
        assertEquals("API Key 无效", (result as ParseResult.Failed).reason)
    }

    @Test
    fun `429 maps to rate limit`() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(429).setBody("{}"))

        val result = parser.parse(request())

        assertTrue(result is ParseResult.Failed)
        assertTrue((result as ParseResult.Failed).reason.contains("频繁"))
    }

    @Test
    fun `500 maps to service error`() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(500).setBody("{}"))

        val result = parser.parse(request())

        assertTrue(result is ParseResult.Failed)
        assertTrue((result as ParseResult.Failed).reason.contains("500"))
    }

    @Test
    fun `empty body maps to Failed`() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(200).setBody(""))

        val result = parser.parse(request())

        assertTrue(result is ParseResult.Failed)
    }

    @Test
    fun `unexpected body shape maps to Failed`() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(200).setBody("""{"foo":"bar"}"""))

        val result = parser.parse(request())

        assertTrue(result is ParseResult.Failed)
    }

    @Test
    fun `missing api key returns Failed without network call`() = runBlocking {
        val noKeyParser = DeepSeekParser(FakeKeyRepo(null))
        noKeyParser.baseUrl = server.url("/").toString().trimEnd('/')

        val result = noKeyParser.parse(request())

        assertTrue(result is ParseResult.Failed)
        assertEquals("未配置 API Key", (result as ParseResult.Failed).reason)
        assertEquals(0, server.requestCount)
    }

    @Test
    fun `timeout maps to Failed`() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"choices":[{"message":{"content":"{}"}}]}""")
                .setBodyDelay(6, TimeUnit.SECONDS),
        )

        val result = parser.parse(request())

        assertTrue(result is ParseResult.Failed)
        assertTrue((result as ParseResult.Failed).reason.contains("超时"))
    }
}
