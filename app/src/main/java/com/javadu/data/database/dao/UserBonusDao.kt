package com.javadu.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.javadu.data.database.entities.BonusType
import com.javadu.data.database.entities.UserBonus
import kotlinx.coroutines.flow.Flow

@Dao
interface UserBonusDao {
    @Query("SELECT * FROM user_bonuses WHERE userId = :userId")
    fun getUserBonuses(userId: Long): Flow<List<UserBonus>>

    @Query("SELECT * FROM user_bonuses WHERE userId = :userId AND bonusType = :type")
    suspend fun getBonusByType(userId: Long, type: BonusType): UserBonus?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBonus(bonus: UserBonus)

    @Update
    suspend fun updateBonus(bonus: UserBonus)

    @Query("UPDATE user_bonuses SET quantity = quantity + :amount WHERE userId = :userId AND bonusType = :type")
    suspend fun addBonusQuantity(userId: Long, type: BonusType, amount: Int = 1)

    @Query("UPDATE user_bonuses SET quantity = quantity - 1 WHERE userId = :userId AND bonusType = :type AND quantity > 0")
    suspend fun useBonus(userId: Long, type: BonusType): Int

    @Query("DELETE FROM user_bonuses WHERE userId = :userId")
    suspend fun deleteUserBonuses(userId: Long)

    @Query("DELETE FROM user_bonuses")
    suspend fun deleteAll()
}
