package com.javadu.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String? = null,
    val totalXp: Int = 0,
    val currentStreak: Int = 0,
    val isGuest: Boolean = false
)
