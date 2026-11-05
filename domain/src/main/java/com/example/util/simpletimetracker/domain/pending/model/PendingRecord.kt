package com.example.util.simpletimetracker.domain.pending.model

/**
 * 还没变成完整时间记录的一条原始输入。
 *
 * 输入的文字先存这里（保证任何情况下不丢），再由解析器加工成
 * records + recordExtras；或者追问后仍不完整，就留在 INCOMPLETE。
 */
data class PendingRecord(
    val id: Long = 0,
    val rawText: String,
    val status: PendingRecordStatus,
    val createdAt: Long,
)
