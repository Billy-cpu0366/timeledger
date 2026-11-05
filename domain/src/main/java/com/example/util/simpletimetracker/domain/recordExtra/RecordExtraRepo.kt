package com.example.util.simpletimetracker.domain.recordExtra

interface RecordExtraRepo {

    suspend fun getRawText(recordId: Long): String?

    suspend fun saveRawText(recordId: Long, rawText: String)

    suspend fun remove(recordId: Long)
}
