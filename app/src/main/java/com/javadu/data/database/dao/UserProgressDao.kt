package com.javadu.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.javadu.data.database.entities.UserProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE userId = :userId")
    fun getUserProgress(userId: Long): Flow<List<UserProgress>>

    @Query("SELECT * FROM user_progress WHERE userId = :userId AND lessonId = :lessonId")
    suspend fun getProgressForLesson(userId: Long, lessonId: Long): UserProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: UserProgress)

    @Update
    suspend fun updateProgress(progress: UserProgress)

    @Query("DELETE FROM user_progress WHERE userId = :userId")
    suspend fun deleteUserProgress(userId: Long)

    @Query("DELETE FROM user_progress")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM user_progress WHERE userId = :userId AND isCompleted = 1")
    suspend fun getCompletedLessonsCount(userId: Long): Int
}
