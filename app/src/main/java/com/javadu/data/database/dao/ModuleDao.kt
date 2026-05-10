package com.javadu.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.javadu.data.database.entities.Module
import kotlinx.coroutines.flow.Flow

@Dao
interface ModuleDao {
    @Query("SELECT * FROM modules ORDER BY `order` ASC")
    fun getAllModules(): Flow<List<Module>>

    @Query("SELECT * FROM modules WHERE id = :id")
    suspend fun getModuleById(id: Long): Module?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModules(modules: List<Module>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModule(module: Module)

    @Query("DELETE FROM modules")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM modules")
    suspend fun getCount(): Int
}
