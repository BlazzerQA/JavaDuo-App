package com.javadu.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.javadu.data.database.entities.Lesson
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons ORDER BY `order` ASC")
    fun getAllLessons(): Flow<List<Lesson>>

    @Query("SELECT * FROM lessons WHERE id = :id")
    suspend fun getLessonById(id: Long): Lesson?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<Lesson>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: Lesson)

    @Query("DELETE FROM lessons")
    suspend fun deleteAll()

    @Query("SELECT * FROM lessons WHERE moduleId = :moduleId ORDER BY `order` ASC")
    fun getLessonsByModule(moduleId: Long): Flow<List<Lesson>>

    @Query("SELECT COUNT(*) FROM lessons")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM lessons WHERE moduleId = :moduleId")
    suspend fun getCountByModule(moduleId: Long): Int
}
