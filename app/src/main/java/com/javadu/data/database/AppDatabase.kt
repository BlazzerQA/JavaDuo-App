package com.javadu.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.javadu.data.database.dao.InterviewQuestionDao
import com.javadu.data.database.dao.LessonDao
import com.javadu.data.database.dao.ModuleDao
import com.javadu.data.database.dao.ModuleProgressDao
import com.javadu.data.database.dao.QuestionDao
import com.javadu.data.database.dao.UnitDao
import com.javadu.data.database.dao.UserBonusDao
import com.javadu.data.database.dao.UserDao
import com.javadu.data.database.dao.UserProgressDao
import com.javadu.data.database.entities.InterviewQuestion
import com.javadu.data.database.entities.Lesson
import com.javadu.data.database.entities.Module
import com.javadu.data.database.entities.ModuleProgress
import com.javadu.data.database.entities.Question
import com.javadu.data.database.entities.Unit
import com.javadu.data.database.entities.User
import com.javadu.data.database.entities.UserBonus
import com.javadu.data.database.entities.UserProgress
import com.javadu.data.database.entities.UserUnit

@Database(
    entities = [
        User::class,
        Lesson::class,
        Question::class,
        UserProgress::class,
        Module::class,
        ModuleProgress::class,
        InterviewQuestion::class,
        UserBonus::class,
        Unit::class,
        UserUnit::class
    ],
    version = 7,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun lessonDao(): LessonDao
    abstract fun questionDao(): QuestionDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun moduleDao(): ModuleDao
    abstract fun moduleProgressDao(): ModuleProgressDao
    abstract fun interviewQuestionDao(): InterviewQuestionDao
    abstract fun userBonusDao(): UserBonusDao
    abstract fun unitDao(): UnitDao
}
