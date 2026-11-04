package com.example.util.simpletimetracker.data_local.recordExtra

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.util.simpletimetracker.data_local.record.RecordDBO

/**
 * 原始输入文本，与 [RecordDBO] 一对一关联。
 *
 * 独立成表而不是给 records 加字段，是为了让上游的 RecordDBO 保持不变，
 * 合并上游更新时不产生冲突（见项目 D12「只加不改」红线）。
 */
@Entity(
    tableName = "recordExtras",
    foreignKeys = [
        ForeignKey(
            entity = RecordDBO::class,
            parentColumns = ["id"],
            childColumns = ["record_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class RecordExtraDBO(
    @PrimaryKey
    @ColumnInfo(name = "record_id")
    val recordId: Long,

    @ColumnInfo(name = "raw_text")
    val rawText: String,
)
