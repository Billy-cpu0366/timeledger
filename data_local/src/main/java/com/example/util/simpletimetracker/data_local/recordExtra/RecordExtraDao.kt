package com.example.util.simpletimetracker.data_local.recordExtra

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecordExtraDao {

    @Query("SELECT * FROM recordExtras WHERE record_id = :recordId LIMIT 1")
    suspend fun get(recordId: Long): RecordExtraDBO?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recordExtra: RecordExtraDBO)

    @Query("DELETE FROM recordExtras WHERE record_id = :recordId")
    suspend fun remove(recordId: Long)

    @Query("DELETE FROM recordExtras")
    suspend fun clear()
}
