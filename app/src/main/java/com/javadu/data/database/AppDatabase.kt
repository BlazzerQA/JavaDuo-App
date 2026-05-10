package com.javadu.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.javadu.data.database.dao.LessonDao
import com.javadu.data.database.dao.QuestionDao
import com.javadu.data.database.dao.UserDao
import com.javadu.data.database.dao.UserProgressDao
import com.javadu.data.database.entities.Lesson
import com.javadu.data.database.entities.Question
import com.javadu.data.database.entities.User
import com.javadu.data.database.entities.UserProgress

@Database(
    entities = [User::class, Lesson::class, Question::class, UserProgress::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun lessonDao(): LessonDao
    abstract fun questionDao(): QuestionDao
    abstract fun userProgressDao(): UserProgressDao
}
