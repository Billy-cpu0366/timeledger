package com.example.util.simpletimetracker.domain.seed

import com.example.util.simpletimetracker.domain.color.model.AppColor
import com.example.util.simpletimetracker.domain.recordType.model.RecordType
import com.example.util.simpletimetracker.domain.recordType.repo.RecordTypeRepo
import javax.inject.Inject

/**
 * 确保七个预置分类存在。已存在的不动，缺的补上。
 *
 * 用「按名字比对」而不是「只在首次启动跑一次」，是为了让用户手动删掉某个分类后
 * 下次打开能补回来。
 */
class CategorySeeder @Inject constructor(
    private val recordTypeRepo: RecordTypeRepo,
) {

    suspend fun ensure() {
        val existing = recordTypeRepo.getAll().map { it.name }.toSet()

        DefaultCategories.ALL
            .filterNot { it.name in existing }
            .forEach { spec ->
                recordTypeRepo.add(
                    RecordType(
                        name = spec.name,
                        icon = spec.icon,
                        color = AppColor(
                            colorId = 0,
                            colorInt = spec.argb.toInt().toString(),
                            gradient = null,
                        ),
                        defaultDuration = 0,
                        note = "",
                    ),
                )
            }
    }
}
