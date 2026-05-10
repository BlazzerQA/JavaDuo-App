package com.javadu.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.javadu.data.database.entities.InterviewQuestion
import kotlinx.coroutines.flow.Flow

@Dao
interface InterviewQuestionDao {
    @Query("SELECT * FROM interview_questions")
    fun getAllQuestions(): Flow<List<InterviewQuestion>>

    @Query("SELECT * FROM interview_questions ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuestion(): InterviewQuestion?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<InterviewQuestion>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: InterviewQuestion)

    @Query("DELETE FROM interview_questions")
    suspend fun deleteAll()
}
