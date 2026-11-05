package com.example.util.simpletimetracker.data_local.pending

import com.example.util.simpletimetracker.domain.pending.model.PendingRecord
import com.example.util.simpletimetracker.domain.pending.model.PendingRecordStatus
import javax.inject.Inject

class PendingRecordDataLocalMapper @Inject constructor() {

    fun map(dbo: PendingRecordDBO): PendingRecord {
        return PendingRecord(
            id = dbo.id,
            rawText = dbo.rawText,
            status = PendingRecordStatus.fromValue(dbo.status),
            createdAt = dbo.createdAt,
        )
    }

    fun map(model: PendingRecord): PendingRecordDBO {
        return PendingRecordDBO(
            id = model.id,
            rawText = model.rawText,
            status = model.status.value,
            createdAt = model.createdAt,
        )
    }
}
