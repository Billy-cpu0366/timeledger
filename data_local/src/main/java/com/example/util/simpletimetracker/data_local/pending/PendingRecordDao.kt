package com.example.util.simpletimetracker.data_local.pending

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PendingRecordDao {

    @Query("SELECT * FROM pendingRecords ORDER BY created_at ASC")
    suspend fun getAll(): List<PendingRecordDBO>

    @Query("SELECT * FROM pendingRecords WHERE id = :id LIMIT 1")
    suspend fun get(id: Long): PendingRecordDBO?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: PendingRecordDBO): Long

    @Query("UPDATE pendingRecords SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: Int)

    @Query("DELETE FROM pendingRecords WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM pendingRecords")
    suspend fun clear()
}
