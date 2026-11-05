package com.example.util.simpletimetracker.domain.parser.model

/**
 * 交给解析器的全部信息。
 *
 * 只包含「模型需要知道的东西」，不包含任何网络或 UI 细节——
 * 换模型实现时这个结构不用变。
 */
data class ParseRequest(
    val rawText: String,
    val spokenAt: Long,
    val todayRecords: List<RecordSummary>,
    val categories: List<String>,
    val currentTimeIso: String,
)

/**
 * 今天已有记录的摘要，供模型推断「刚才」「下午」这类相对时间。
 */
data class RecordSummary(
    val startAt: Long,
    val endAt: Long,
    val activity: String,
)
