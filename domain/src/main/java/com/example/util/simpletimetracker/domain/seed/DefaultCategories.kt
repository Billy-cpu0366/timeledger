package com.example.util.simpletimetracker.domain.seed

/**
 * 七个预置分类。
 *
 * 分类本身存在上游的 recordTypes 表里，这里只提供初始数据。
 * 图标名必须是 resources 模块 drawable 下真实存在的资源。
 */
object DefaultCategories {

    data class Spec(
        val name: String,
        val icon: String,
        val argb: Long,
    )

    val ALL: List<Spec> = listOf(
        Spec("工作", "ic_work_24px", 0xFF4A90D9),
        Spec("学习", "ic_school_24px", 0xFF50B37C),
        Spec("通勤", "ic_directions_bus_24px", 0xFF8E8E93),
        Spec("吃饭", "ic_restaurant_24px", 0xFFE8A33D),
        Spec("休息", "ic_hotel_24px", 0xFF9B7EDE),
        Spec("娱乐", "ic_sports_esports_24px", 0xFFE06666),
        Spec("其他", "ic_more_horiz_24px", 0xFFAAAAAA),
    )

    val names: List<String> = ALL.map { it.name }
}
