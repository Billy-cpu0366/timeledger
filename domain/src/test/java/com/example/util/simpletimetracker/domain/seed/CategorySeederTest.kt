package com.example.util.simpletimetracker.domain.seed

import com.example.util.simpletimetracker.domain.recordType.model.RecordType
import com.example.util.simpletimetracker.domain.recordType.repo.RecordTypeRepo
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CategorySeederTest {

    private class FakeRecordTypeRepo : RecordTypeRepo {
        val added = mutableListOf<RecordType>()
        private var nextId = 1L
        private val store = mutableListOf<RecordType>()

        override suspend fun getAll(): List<RecordType> = store.toList()

        override suspend fun get(id: Long): RecordType? = store.firstOrNull { it.id == id }

        override suspend fun get(name: String): List<RecordType> =
            store.filter { it.name == name }

        override suspend fun add(recordType: RecordType): Long {
            val id = nextId++
            added += recordType
            store += recordType.copy(id = id)
            return id
        }

        override suspend fun archive(id: Long) = Unit

        override suspend fun restore(id: Long) = Unit

        override suspend fun remove(id: Long) {
            store.removeAll { it.id == id }
        }

        override suspend fun clear() {
            store.clear()
        }
    }

    @Test
    fun `seeds all seven categories when empty`() = runBlocking {
        val repo = FakeRecordTypeRepo()

        CategorySeeder(repo).ensure()

        val names = repo.getAll().map { it.name }
        assertEquals(7, names.size)
        assertEquals(DefaultCategories.names, names)
        assertEquals(7, repo.added.size)
    }

    @Test
    fun `does not touch existing categories`() = runBlocking {
        val repo = FakeRecordTypeRepo()
        repo.add(
            RecordType(
                name = "工作",
                icon = "ic_work_24px",
                color = com.example.util.simpletimetracker.domain.color.model.AppColor(0, "", null),
                defaultDuration = 0,
                note = "",
            ),
        )

        // 清掉手动插入的记录，只看 seeder 的产出
        repo.added.clear()

        CategorySeeder(repo).ensure()

        assertEquals(6, repo.added.size)
        assertTrue(repo.added.none { it.name == "工作" })
        assertEquals(7, repo.getAll().size)
    }

    @Test
    fun `keeps user-created categories untouched`() = runBlocking {
        val repo = FakeRecordTypeRepo()
        repo.add(
            RecordType(
                name = "自定义活动",
                icon = "ic_work_24px",
                color = com.example.util.simpletimetracker.domain.color.model.AppColor(0, "", null),
                defaultDuration = 0,
                note = "",
            ),
        )

        CategorySeeder(repo).ensure()

        assertEquals(8, repo.getAll().size)
        assertTrue(repo.getAll().any { it.name == "自定义活动" })
    }

    @Test
    fun `is idempotent`() = runBlocking {
        val repo = FakeRecordTypeRepo()
        val seeder = CategorySeeder(repo)

        seeder.ensure()
        seeder.ensure()
        seeder.ensure()

        assertEquals(7, repo.getAll().size)
        assertEquals(7, repo.added.size)
    }

    @Test
    fun `every category has non-blank icon and color`() {
        DefaultCategories.ALL.forEach { spec ->
            assertTrue("icon blank for ${spec.name}", spec.icon.isNotBlank())
            assertTrue("color missing for ${spec.name}", spec.argb != 0L)
        }
    }
}
