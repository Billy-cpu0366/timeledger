package com.example.util.simpletimetracker.domain.pending.repo

import com.example.util.simpletimetracker.domain.pending.model.PendingRecord
import com.example.util.simpletimetracker.domain.pending.model.PendingRecordStatus

interface PendingRecordRepo {

    suspend fun getAll(): List<PendingRecord>

    suspend fun get(id: Long): PendingRecord?

    suspend fun add(record: PendingRecord): Long

    suspend fun updateStatus(id: Long, status: PendingRecordStatus)

    suspend fun remove(id: Long)

    suspend fun clear()
}
