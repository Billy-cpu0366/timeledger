package com.example.util.simpletimetracker.domain.writer

import com.example.util.simpletimetracker.domain.recordType.repo.RecordTypeRepo
import javax.inject.Inject

/**
 * 把 AI 返回的分类名（"工作"）换成分类 id。
 * 找不到就用「其他」；「其他」也没有就建一个。
 */
class CategoryResolver @Inject constructor(
    private val recordTypeRepo: RecordTypeRepo,
) {

    suspend fun resolve(categoryName: String): Long {
        val name = categoryName.trim().ifEmpty { FALLBACK }

        recordTypeRepo.get(name).firstOrNull()?.let { return it.id }

        // 回退到「其他」
        recordTypeRepo.get(FALLBACK).firstOrNull()?.let { return it.id }

        // 连「其他」都没有，建一个
        return categorySeederFallback(name)
    }

    private suspend fun categorySeederFallback(name: String): Long {
        val spec = com.example.util.simpletimetracker.domain.seed.DefaultCategories.ALL
            .firstOrNull { it.name == FALLBACK }
            ?: com.example.util.simpletimetracker.domain.seed.DefaultCategories.ALL.first()

        return recordTypeRepo.add(
            com.example.util.simpletimetracker.domain.recordType.model.RecordType(
                name = spec.name,
                icon = spec.icon,
                color = com.example.util.simpletimetracker.domain.color.model.AppColor(
                    colorId = 0,
                    colorInt = spec.argb.toInt().toString(),
                    gradient = null,
                ),
                defaultDuration = 0,
                note = "",
            ),
        )
    }

    private companion object {
        const val FALLBACK = "其他"
    }
}
