package com.example.util.simpletimetracker.data_ai

import com.example.util.simpletimetracker.domain.parser.model.ParseRequest
import com.example.util.simpletimetracker.domain.parser.model.RecordSummary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 提示词。改这里就是改模型行为，不涉及其他模块。
 */
object PromptBuilder {

    private val timeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())

    fun system(categories: List<String>): String {
        return """
            你是一个时间记录助手。用户会用一句话描述他做了什么，你要把它转成结构化数据。

            规则：
            1. 只输出 JSON，不要解释、不要 markdown 代码块、不要多余文字。
            2. 输出必须是以下三种之一：

            成功：
            {"status":"ok","start":"YYYY-MM-DDTHH:mm","end":"YYYY-MM-DDTHH:mm 或 null","activity":"活动名","category":"分类"}

            信息不足需要追问：
            {"status":"need_more","activity":"活动名","category":"分类","question":"你要问用户的话"}

            无法理解：
            {"status":"failed","reason":"原因"}

            3. category 只能是这些值之一：${categories.joinToString("、")}
            4. 如果用户没说时间，也无法从上下文推断，必须返回 need_more，绝对不要猜时间。
            5. start 必须存在。end 可以是 null，表示这件事正在进行、还没结束。
            6. 用户说「刚才」「刚刚」时，指当前时间之前最近的一段时间。
            7. 用户说「上午」「下午」「晚上几点」时，按今天的日期换算。
            8. activity 要简短，不超过 10 个字。
        """.trimIndent()
    }

    fun user(request: ParseRequest): String {
        val records = if (request.todayRecords.isEmpty()) {
            "无"
        } else {
            request.todayRecords.joinToString("\n") { formatRecord(it) }
        }

        return """
            当前时间：${request.currentTimeIso}
            今天已有的记录：
            $records

            用户说：${request.rawText}
        """.trimIndent()
    }

    private fun formatRecord(record: RecordSummary): String {
        val start = timeFormat.format(Date(record.startAt))
        val end = timeFormat.format(Date(record.endAt))
        return "$start ~ $end  ${record.activity}"
    }
}
