package com.javadu.data.repository

import android.util.Log
import com.javadu.data.database.dao.ModuleProgressDao
import com.javadu.data.database.dao.UnitDao
import com.javadu.data.database.dao.UserDao
import com.javadu.data.database.entities.Unit
import com.javadu.data.database.entities.UserUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "UnitRepository"

@Singleton
class UnitRepository @Inject constructor(
    private val unitDao: UnitDao,
    private val moduleProgressDao: ModuleProgressDao,
    private val userDao: UserDao
) {
    fun getAllUnits(): Flow<List<Unit>> = unitDao.getAllUnits()

    fun getAvailableUnits(): Flow<List<Unit>> = unitDao.getAvailableUnits()

    fun getHiredUnits(): Flow<List<UserUnit>> = unitDao.getHiredUnits()

    fun getHiredUnitsWithDetails(): Flow<List<Pair<UserUnit, Unit>>> = unitDao.getHiredUnitsWithDetailsFlow().map { userUnits ->
        userUnits.map { userUnit ->
            val unit = unitDao.getUnitById(userUnit.unitId).firstOrNull()
            if (unit != null) {
                userUnit to unit
            } else {
                null
            }
        }.filterNotNull()
    }

    fun getUnitById(id: String): Flow<Unit?> = unitDao.getUnitById(id)

    fun getUserUnitById(unitId: String): Flow<UserUnit?> = unitDao.getUserUnitById(unitId)

    fun getUnownedUnits(): Flow<List<Unit>> = unitDao.getUnownedUnits()

    suspend fun hireUnit(unitId: String, userId: Long): Result<Unit> {
        Log.d(TAG, "=== hireUnit started === unitId: $unitId, userId: $userId")
        
        val unit = unitDao.getUnitById(unitId).firstOrNull()
        Log.d(TAG, "Unit from DB: $unit")
        if (unit == null) {
            Log.e(TAG, "Unit not found: $unitId")
            return Result.failure(Exception("Юнит не найден: $unitId"))
        }

        val user = userDao.getUserById(userId)
        Log.d(TAG, "User from DB: $user")
        if (user == null) {
            Log.e(TAG, "User not found: $userId")
            return Result.failure(Exception("Пользователь не найден: $userId"))
        }

        Log.d(TAG, "Checking coins: user.coins=${user.coins}, unit.hireCost=${unit.hireCost}")
        if (user.coins < unit.hireCost) {
            Log.e(TAG, "Not enough coins")
            return Result.failure(Exception("Недостаточно CodeCoins: есть ${user.coins}, нужно ${unit.hireCost}"))
        }

        Log.d(TAG, "Calling spendCoins: userId=$userId, amount=${unit.hireCost}")
        val coinsSpent = userDao.spendCoins(userId, unit.hireCost)
        Log.d(TAG, "spendCoins returned: $coinsSpent")
        if (coinsSpent == 0) {
            Log.e(TAG, "Failed to spend coins (returned 0)")
            return Result.failure(Exception("Не удалось списать CodeCoins (spendCoins вернул 0)"))
        }

        val existingUserUnit = unitDao.getUserUnitById(unitId).firstOrNull()
        Log.d(TAG, "Existing UserUnit: $existingUserUnit")
        
        if (existingUserUnit != null) {
            Log.d(TAG, "Updating existing user unit isHired to true")
            val updated = unitDao.updateHired(unitId, true)
            Log.d(TAG, "updateHired returned: $updated")
            if (updated == 0) {
                Log.e(TAG, "Failed to update hired status (returned 0)")
                return Result.failure(Exception("Не удалось обновить статус юнита (updateHired вернул 0)"))
            }
        } else {
            Log.d(TAG, "Inserting new user unit")
            unitDao.insertUserUnit(
                UserUnit(
                    unitId = unitId,
                    isHired = true,
                    level = 1,
                    currentHp = unit.baseHp
                )
            )
        }

        Log.d(TAG, "=== hireUnit completed successfully ===")
        return Result.success(unit)
    }

    suspend fun unhireUnit(unitId: String) {
        unitDao.updateHired(unitId, false)
    }

    suspend fun upgradeUnit(unitId: String) {
        val userUnit = unitDao.getUserUnitById(unitId).firstOrNull()
            ?: return

        val newLevel = userUnit.level + 1
        unitDao.updateUserUnitLevel(unitId, newLevel)
    }

    suspend fun updateUnitCurrentHp(unitId: String, currentHp: Int) {
        unitDao.updateCurrentHp(unitId, currentHp)
    }

    suspend fun initializeUserUnits(userId: Long) {
        val availableUnits = unitDao.getAvailableUnits().firstOrNull() ?: return
        val existingUserUnits = unitDao.getAllUserUnits().firstOrNull() ?: emptyList()
        val existingUnitIds = existingUserUnits.map { it.unitId }.toSet()

        val newUnits = availableUnits.filter { it.id !in existingUnitIds }
        val userUnits = newUnits.map { unit ->
            UserUnit(
                unitId = unit.id,
                isHired = false,
                level = 1,
                currentHp = unit.baseHp
            )
        }

        if (userUnits.isNotEmpty()) {
            unitDao.insertUserUnits(userUnits)
        }
    }

    suspend fun getNewlyAvailableUnits(moduleId: Long): List<Unit> {
        val allUnits = unitDao.getAllUnits().firstOrNull() ?: return emptyList()
        return allUnits.filter { it.moduleId == moduleId }
    }
}
