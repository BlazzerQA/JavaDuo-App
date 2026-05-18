package com.javadu.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "units",
    foreignKeys = [
        ForeignKey(
            entity = Module::class,
            parentColumns = ["id"],
            childColumns = ["moduleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["moduleId"])]
)
data class Unit(
    @PrimaryKey
    val id: String,
    val name: String,
    val icon: String,
    val moduleId: Long,
    val baseAttack: Int,
    val baseDefense: Int,
    val baseHp: Int,
    val hireCost: Int,
    val description: String
)
