package com.javadu.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.javadu.data.database.entities.Question
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions WHERE lessonId = :lessonId")
    fun getQuestionsForLesson(lessonId: Long): Flow<List<Question>>

    @Query("SELECT * FROM questions WHERE lessonId = :lessonId")
    suspend fun getQuestionsForLessonSync(lessonId: Long): List<Question>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<Question>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: Question)

    @Query("DELETE FROM questions")
    suspend fun deleteAll()
}
