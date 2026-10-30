package com.javadu.data.database.migrations

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Проверяет миграцию 7 → 8 на реальном SQLite устройства/эмулятора
 * для обоих вариантов «версии 7»: без колонки users.diamonds и с ней.
 *
 * Запуск: ./gradlew :app:connectedDebugAndroidTest
 */
@RunWith(AndroidJUnit4::class)
class Migration7To8Test {

    private var helper: SupportSQLiteOpenHelper? = null

    @After
    fun tearDown() {
        helper?.close()
    }

    /** Схема users версии 7 до 03.06.2026 — без diamonds. */
    @Test
    fun addsDiamondsColumnWhenMissing() {
        val db = openV7(USERS_V7_WITHOUT_DIAMONDS)
        db.execSQL(
            "INSERT INTO users (name, totalXp, coins, currentStreak, isGuest) " +
                "VALUES ('Dima', 500, 42, 3, 0)"
        )

        DatabaseMigrations.MIGRATION_7_8.migrate(db)

        assertTrue(columnsOf(db, "users").contains("diamonds"))
        db.query("SELECT name, totalXp, coins, currentStreak, diamonds FROM users").use { c ->
            assertTrue(c.moveToFirst())
            assertEquals("Dima", c.getString(0))
            assertEquals(500, c.getInt(1))
            assertEquals(42, c.getInt(2))
            assertEquals(3, c.getInt(3))
            assertEquals(0, c.getInt(4))
        }
    }

    /** Схема users версии 7 после 03.06.2026 — diamonds уже есть, миграция ничего не ломает. */
    @Test
    fun keepsExistingDiamondsWhenColumnPresent() {
        val db = openV7(USERS_V7_WITH_DIAMONDS)
        db.execSQL(
            "INSERT INTO users (name, totalXp, coins, diamonds, currentStreak, isGuest) " +
                "VALUES ('Dima', 500, 42, 7, 3, 0)"
        )

        DatabaseMigrations.MIGRATION_7_8.migrate(db)

        db.query("SELECT totalXp, coins, diamonds FROM users").use { c ->
            assertTrue(c.moveToFirst())
            assertEquals(500, c.getInt(0))
            assertEquals(42, c.getInt(1))
            assertEquals(7, c.getInt(2))
        }
    }

    private fun openV7(createUsersSql: String): SupportSQLiteDatabase {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(null) // in-memory
            .callback(object : SupportSQLiteOpenHelper.Callback(7) {
                override fun onCreate(db: SupportSQLiteDatabase) = db.execSQL(createUsersSql)
                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
            })
            .build()
        return FrameworkSQLiteOpenHelperFactory().create(config).also { helper = it }.writableDatabase
    }

    private fun columnsOf(db: SupportSQLiteDatabase, table: String): Set<String> =
        db.query("PRAGMA table_info(`$table`)").use { c ->
            val names = mutableSetOf<String>()
            while (c.moveToNext()) names += c.getString(c.getColumnIndexOrThrow("name"))
            names
        }

    private companion object {
        const val USERS_V7_WITHOUT_DIAMONDS =
            "CREATE TABLE IF NOT EXISTS `users` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `email` TEXT, " +
                "`totalXp` INTEGER NOT NULL, `coins` INTEGER NOT NULL, `currentStreak` INTEGER NOT NULL, " +
                "`isGuest` INTEGER NOT NULL, `avatarUri` TEXT, `avatarIcon` TEXT)"

        const val USERS_V7_WITH_DIAMONDS =
            "CREATE TABLE IF NOT EXISTS `users` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `email` TEXT, " +
                "`totalXp` INTEGER NOT NULL, `coins` INTEGER NOT NULL, `diamonds` INTEGER NOT NULL, " +
                "`currentStreak` INTEGER NOT NULL, `isGuest` INTEGER NOT NULL, `avatarUri` TEXT, `avatarIcon` TEXT)"
    }
}
