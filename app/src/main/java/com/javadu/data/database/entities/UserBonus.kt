package com.javadu.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_bonuses")
data class UserBonus(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val bonusType: BonusType,
    val quantity: Int = 1,
    val purchasedAt: Long = System.currentTimeMillis()
)
