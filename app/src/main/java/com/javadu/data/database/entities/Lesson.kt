package com.javadu.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lessons")
data class Lesson(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val theory: String,
    val codeExample: String? = null,
    val xpReward: Int = 20,
    val order: Int = 0
)
