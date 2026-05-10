package com.javadu.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "modules")
data class Module(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val icon: String,
    val order: Int,
    val totalLessons: Int
)
