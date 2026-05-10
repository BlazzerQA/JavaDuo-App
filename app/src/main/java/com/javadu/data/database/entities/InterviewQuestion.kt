package com.javadu.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "interview_questions")
data class InterviewQuestion(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String,
    val question: String,
    val answer: String,
    val difficulty: Int
)
