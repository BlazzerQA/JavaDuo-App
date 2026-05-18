package com.javadu.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.javadu.data.database.entities.Unit
import com.javadu.data.database.entities.UserUnit
import kotlinx.coroutines.flow.Flow

@Dao
interface UnitDao {
    @Query("SELECT * FROM units")
    fun getAllUnits(): Flow<List<Unit>>

    @Query("SELECT * FROM units WHERE moduleId IN (SELECT moduleId FROM module_progress WHERE completedLessons > 0)")
    fun getAvailableUnits(): Flow<List<Unit>>

    @Query("SELECT * FROM user_units WHERE isHired = 1")
    fun getHiredUnits(): Flow<List<UserUnit>>

    @Query("SELECT * FROM user_units WHERE isHired = 1")
    fun getHiredUnitsWithDetailsFlow(): Flow<List<UserUnit>>

    @Query("SELECT * FROM units WHERE id = :id")
    fun getUnitById(id: String): Flow<Unit?>

    @Query("SELECT * FROM user_units WHERE unitId = :unitId")
    fun getUserUnitById(unitId: String): Flow<UserUnit?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnit(unit: Unit)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnits(units: List<Unit>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserUnit(userUnit: UserUnit)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserUnits(userUnits: List<UserUnit>)

    @Query("UPDATE user_units SET isHired = :isHired WHERE unitId = :unitId")
    suspend fun updateHired(unitId: String, isHired: Boolean): Int

    @Query("UPDATE user_units SET level = :level WHERE unitId = :unitId")
    suspend fun updateUserUnitLevel(unitId: String, level: Int)

    @Query("UPDATE user_units SET currentHp = :currentHp WHERE unitId = :unitId")
    suspend fun updateCurrentHp(unitId: String, currentHp: Int)

    @Query("SELECT * FROM user_units")
    fun getAllUserUnits(): Flow<List<UserUnit>>

    @Query("SELECT * FROM units WHERE id NOT IN (SELECT unitId FROM user_units)")
    fun getUnownedUnits(): Flow<List<Unit>>
}
