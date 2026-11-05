package com.example.util.simpletimetracker.domain.parser.model

/**
 * 解析结果，穷尽三种情况。
 *
 * 用 sealed interface 而不是可空字段，是为了让调用方必须处理全部分支，
 * 避免「解析失败」被当成「解析成功但字段为空」。
 */
sealed interface ParseResult {

    /** 解析出完整时间 */
    data class Parsed(
        val startAt: Long,
        val endAt: Long?,   // null 表示正在做的事，还没结束
        val activity: String,
        val category: String,
    ) : ParseResult

    /** 信息不足，需要向用户追问 */
    data class NeedMore(
        val question: String,
    ) : ParseResult

    /** 解析失败（网络、格式、额度等），可重试 */
    data class Failed(
        val reason: String,
    ) : ParseResult
}
