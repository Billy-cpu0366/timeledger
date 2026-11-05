package com.example.util.simpletimetracker.data_local.pending

import com.example.util.simpletimetracker.domain.pending.model.PendingRecord
import com.example.util.simpletimetracker.domain.pending.model.PendingRecordStatus
import com.example.util.simpletimetracker.domain.pending.repo.PendingRecordRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PendingRecordRepoImpl @Inject constructor(
    private val pendingRecordDao: PendingRecordDao,
    private val pendingRecordDataLocalMapper: PendingRecordDataLocalMapper,
) : PendingRecordRepo {

    override suspend fun getAll(): List<PendingRecord> {
        return pendingRecordDao.getAll().map(pendingRecordDataLocalMapper::map)
    }

    override suspend fun get(id: Long): PendingRecord? {
        return pendingRecordDao.get(id)?.let(pendingRecordDataLocalMapper::map)
    }

    override suspend fun add(record: PendingRecord): Long {
        return pendingRecordDao.insert(pendingRecordDataLocalMapper.map(record))
    }

    override suspend fun updateStatus(id: Long, status: PendingRecordStatus) {
        pendingRecordDao.updateStatus(id, status.value)
    }

    override suspend fun remove(id: Long) {
        pendingRecordDao.delete(id)
    }

    override suspend fun clear() {
        pendingRecordDao.clear()
    }
}
