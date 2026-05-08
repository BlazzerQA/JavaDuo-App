package com.javadu.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "user_progress",
    primaryKeys = ["userId", "lessonId"],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Lesson::class,
            parentColumns = ["id"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["lessonId"])
    ]
)
data class UserProgress(
    val userId: Long,
    val lessonId: Long,
    val isCompleted: Boolean = false,
    val xpEarned: Int = 0,
    val completedAt: Long? = null
)
