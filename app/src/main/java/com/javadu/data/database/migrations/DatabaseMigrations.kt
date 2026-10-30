package com.javadu.data.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Миграции схемы Room.
 *
 * Правило: любое изменение entity-класса (новое поле, таблица, индекс) =
 * +1 к [com.javadu.data.database.AppDatabase.VERSION] + новая Migration в [ALL]
 * + закоммиченный файл app/schemas/.../<версия>.json, который генерирует сборка.
 */
object DatabaseMigrations {

    /**
     * Версии, для которых нет истории схем (exportSchema был выключен),
     * поэтому перенести данные с них нечем — БД пересоздаётся.
     * v6 — схема релиза v1.0.3.
     */
    val LEGACY_DESTRUCTIVE_VERSIONS: IntArray = intArrayOf(1, 2, 3, 4, 5, 6)

    /**
     * 7 → 8.
     *
     * Колонку users.diamonds добавили 03.06.2026 (коммит e71fd61) без повышения версии,
     * поэтому «версия 7» на устройствах бывает в двух вариантах: с колонкой и без.
     * Добавляем колонку, только если её нет. Остальные таблицы не меняются.
     */
    val MIGRATION_7_8: Migration = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            if (!db.hasColumn(table = "users", column = "diamonds")) {
                db.execSQL("ALTER TABLE `users` ADD COLUMN `diamonds` INTEGER NOT NULL DEFAULT 0")
            }
        }
    }

    /** Все миграции по порядку. Передаются в Room.databaseBuilder(...).addMigrations(*ALL). */
    val ALL: Array<Migration> = arrayOf(
        MIGRATION_7_8
    )

    internal fun SupportSQLiteDatabase.hasColumn(table: String, column: String): Boolean =
        query("PRAGMA table_info(`$table`)").use { cursor ->
            val nameIndex = cursor.getColumnIndexOrThrow("name")
            var found = false
            while (!found && cursor.moveToNext()) {
                found = cursor.getString(nameIndex) == column
            }
            found
        }
}
