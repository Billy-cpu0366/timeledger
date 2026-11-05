package com.example.util.simpletimetracker.data_local.pending

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pendingRecords")
data class PendingRecordDBO(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "raw_text")
    val rawText: String,

    @ColumnInfo(name = "status")
    val status: Int,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,
)
