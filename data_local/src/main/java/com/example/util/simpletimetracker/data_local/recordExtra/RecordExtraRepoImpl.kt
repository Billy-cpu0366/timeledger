package com.example.util.simpletimetracker.data_local.recordExtra

import com.example.util.simpletimetracker.domain.recordExtra.RecordExtraRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordExtraRepoImpl @Inject constructor(
    private val recordExtraDao: RecordExtraDao,
) : RecordExtraRepo {

    override suspend fun getRawText(recordId: Long): String? {
        return recordExtraDao.get(recordId)?.rawText
    }

    override suspend fun saveRawText(recordId: Long, rawText: String) {
        recordExtraDao.insert(RecordExtraDBO(recordId = recordId, rawText = rawText))
    }

    override suspend fun remove(recordId: Long) {
        recordExtraDao.remove(recordId)
    }
}
