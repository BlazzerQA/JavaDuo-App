package com.javadu.data.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lessons",
    indices = [Index(value = ["moduleId"])]
)
data class Lesson(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val theory: String,
    val codeExample: String? = null,
    val xpReward: Int = 20,
    val order: Int = 0,
    val moduleId: Long = 0
)
