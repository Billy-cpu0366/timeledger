package com.example.util.simpletimetracker.data_ai

import com.example.util.simpletimetracker.domain.parser.model.ParseResult
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 模型输出 -> ParseResult。纯函数，不涉及网络。
 *
 * 模型偶尔会加 markdown 代码块或前后缀文字，这里先做一次清理。
 */
object ParseResultMapper {

    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())

    fun map(rawResponse: String): ParseResult {
        val json = try {
            JSONObject(extractJson(rawResponse))
        } catch (e: Exception) {
            return ParseResult.Failed("返回内容不是合法 JSON")
        }

        return when (json.optString("status")) {
            "ok" -> mapOk(json)
            "need_more" -> {
                val question = json.optString("question").trim()
                if (question.isEmpty()) {
                    ParseResult.Failed("模型要求追问但没给出问题")
                } else {
                    ParseResult.NeedMore(question)
                }
            }
            "failed" -> ParseResult.Failed(json.optString("reason", "模型无法理解"))
            else -> ParseResult.Failed("未知的 status 字段")
        }
    }

    private fun mapOk(json: JSONObject): ParseResult {
        val start = parseIso(json.optString("start"))
            ?: return ParseResult.Failed("缺少开始时间")

        val activity = json.optString("activity").trim()
        if (activity.isEmpty()) return ParseResult.Failed("缺少活动名")

        val category = json.optString("category").trim()
        if (category.isEmpty()) return ParseResult.Failed("缺少分类")

        return ParseResult.Parsed(
            startAt = start,
            endAt = parseIso(json.optString("end")),
            activity = activity,
            category = category,
        )
    }

    /** 去掉 ```json ... ``` 包裹和前后杂字 */
    private fun extractJson(raw: String): String {
        val trimmed = raw.trim()
        val start = trimmed.indexOf('{')
        val end = trimmed.lastIndexOf('}')
        if (start < 0 || end <= start) return trimmed
        return trimmed.substring(start, end + 1)
    }

    private fun parseIso(value: String?): Long? {
        if (value.isNullOrBlank() || value == "null") return null
        return try {
            isoFormat.parse(value)?.time
        } catch (e: Exception) {
            null
        }
    }
}
