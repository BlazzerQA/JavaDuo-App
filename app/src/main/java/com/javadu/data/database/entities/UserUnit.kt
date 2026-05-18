package com.javadu.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_units",
    foreignKeys = [
        ForeignKey(
            entity = Unit::class,
            parentColumns = ["id"],
            childColumns = ["unitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["unitId"])]
)
data class UserUnit(
    @PrimaryKey
    val unitId: String,
    val isHired: Boolean = false,
    val level: Int = 1,
    val currentHp: Int = 0
)
