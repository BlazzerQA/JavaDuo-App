package com.javadu.data.database.migrations

import com.javadu.data.database.AppDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Проверяет, что набор миграций согласован с версией БД:
 * при повышении AppDatabase.VERSION без новой миграции тест упадёт
 * раньше, чем приложение упадёт у игрока.
 */
class MigrationsChainTest {

    private val migrations = DatabaseMigrations.ALL.toList()
    private val legacy = DatabaseMigrations.LEGACY_DESTRUCTIVE_VERSIONS.toSet()
    private val startVersions = migrations.map { it.startVersion }

    /** Каждая миграция переводит схему ровно на одну версию вперёд. */
    @Test
    fun everyMigrationIsSingleStep() {
        migrations.forEach {
            assertEquals("миграция ${it.startVersion}→${it.endVersion}", it.startVersion + 1, it.endVersion)
        }
    }

    /** Нет двух миграций с одной и той же стартовой версии. */
    @Test
    fun migrationStartVersionsAreUnique() {
        assertEquals(startVersions.size, startVersions.toSet().size)
    }

    /** Каждая прошлая версия либо мигрируется, либо явно помечена как пересоздаваемая — но не то и другое сразу. */
    @Test
    fun everyPreviousVersionIsEitherMigratedOrLegacy() {
        for (version in 1 until AppDatabase.VERSION) {
            val migrated = version in startVersions
            val destructive = version in legacy
            assertTrue("версия $version: migrated=$migrated, legacy=$destructive", migrated != destructive)
        }
    }

    /** Пересоздаются только самые старые версии: после первой миграции разрывов нет. */
    @Test
    fun legacyVersionsPrecedeMigratedOnes() {
        assertTrue(legacy.max() < startVersions.min())
    }

    /** Ни одна миграция не уходит дальше текущей версии. */
    @Test
    fun noMigrationTargetsFutureVersion() {
        migrations.forEach {
            assertTrue("миграция в ${it.endVersion}", it.endVersion <= AppDatabase.VERSION)
        }
    }
}
