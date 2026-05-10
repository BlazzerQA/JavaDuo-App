package com.javadu.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.javadu.data.database.entities.ModuleProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface ModuleProgressDao {
    @Query("SELECT * FROM module_progress WHERE moduleId = :moduleId")
    fun getProgressByModuleId(moduleId: Long): Flow<ModuleProgress?>

    @Query("SELECT * FROM module_progress")
    fun getAllProgress(): Flow<List<ModuleProgress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: ModuleProgress)

    @Update
    suspend fun updateProgress(progress: ModuleProgress)

    @Query("DELETE FROM module_progress")
    suspend fun deleteAll()
}
