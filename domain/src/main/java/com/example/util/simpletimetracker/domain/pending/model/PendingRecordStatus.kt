package com.example.util.simpletimetracker.domain.pending.model

enum class PendingRecordStatus(val value: Int) {
    /** 已落库，还没解析 */
    PENDING(0),

    /** 解析器认为信息不足，等待追问回答 */
    NEED_MORE(1),

    /** 问过一轮仍不完整，进待补清单 */
    INCOMPLETE(2),

    /** 解析失败（网络、格式等），可重试 */
    FAILED(3),
    ;

    companion object {
        fun fromValue(value: Int): PendingRecordStatus =
            entries.firstOrNull { it.value == value } ?: PENDING
    }
}
