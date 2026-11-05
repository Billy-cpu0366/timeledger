package com.example.util.simpletimetracker.data_ai

import com.example.util.simpletimetracker.domain.parser.model.ParseResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ParseResultMapperTest {

    @Test
    fun `maps ok response to Parsed`() {
        val json = """
            {"status":"ok","start":"2026-11-05T14:00","end":"2026-11-05T14:40","activity":"写周报","category":"工作"}
        """.trimIndent()

        val result = ParseResultMapper.map(json)

        assertTrue(result is ParseResult.Parsed)
        result as ParseResult.Parsed
        assertEquals("写周报", result.activity)
        assertEquals("工作", result.category)
        assertTrue(result.endAt != null)
        assertTrue(result.endAt!! > result.startAt)
    }

    @Test
    fun `maps null end to running activity`() {
        val json = """
            {"status":"ok","start":"2026-11-05T14:00","end":null,"activity":"改bug","category":"工作"}
        """.trimIndent()

        val result = ParseResultMapper.map(json)

        assertTrue(result is ParseResult.Parsed)
        assertNull((result as ParseResult.Parsed).endAt)
    }

    @Test
    fun `maps need_more to NeedMore`() {
        val json = """
            {"status":"need_more","activity":"改bug","category":"工作","question":"这个 bug 大概几点开始、几点结束的？"}
        """.trimIndent()

        val result = ParseResultMapper.map(json)

        assertTrue(result is ParseResult.NeedMore)
        assertEquals(
            "这个 bug 大概几点开始、几点结束的？",
            (result as ParseResult.NeedMore).question,
        )
    }

    @Test
    fun `maps failed to Failed`() {
        val result = ParseResultMapper.map("""{"status":"failed","reason":"无法理解"}""")

        assertTrue(result is ParseResult.Failed)
        assertEquals("无法理解", (result as ParseResult.Failed).reason)
    }

    @Test
    fun `strips markdown code fence`() {
        val json = """
            ```json
            {"status":"ok","start":"2026-11-05T09:00","end":"2026-11-05T10:00","activity":"开会","category":"工作"}
            ```
        """.trimIndent()

        val result = ParseResultMapper.map(json)

        assertTrue(result is ParseResult.Parsed)
        assertEquals("开会", (result as ParseResult.Parsed).activity)
    }

    @Test
    fun `invalid json returns Failed`() {
        val result = ParseResultMapper.map("这不是 JSON")

        assertTrue(result is ParseResult.Failed)
    }

    @Test
    fun `missing start returns Failed`() {
        val json = """{"status":"ok","start":"","end":null,"activity":"改bug","category":"工作"}"""

        val result = ParseResultMapper.map(json)

        assertTrue(result is ParseResult.Failed)
    }
}
