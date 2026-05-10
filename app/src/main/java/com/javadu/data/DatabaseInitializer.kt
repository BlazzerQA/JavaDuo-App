package com.javadu.data

import android.content.Context
import androidx.room.withTransaction
import com.google.gson.Gson
import com.javadu.data.database.AppDatabase
import com.javadu.data.database.dao.InterviewQuestionDao
import com.javadu.data.database.dao.LessonDao
import com.javadu.data.database.dao.ModuleDao
import com.javadu.data.database.dao.QuestionDao
import com.javadu.data.database.entities.InterviewQuestion
import com.javadu.data.database.entities.Lesson
import com.javadu.data.database.entities.Module
import com.javadu.data.database.entities.Question
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val moduleDao: ModuleDao,
    private val lessonDao: LessonDao,
    private val questionDao: QuestionDao,
    private val interviewQuestionDao: InterviewQuestionDao,
    private val database: AppDatabase
) {

    private val gson = Gson()

    suspend fun initialize() {
        // Если модули уже есть — данные загружены ранее
        if (moduleDao.getCount() > 0) return

        val modules = parseJson("modules.json", Array<Module>::class.java).toList()
        val lessons = parseJson("lessons.json", Array<Lesson>::class.java).toList()
        val questions = parseJson("questions.json", Array<Question>::class.java).toList()
        val interviewQuestions = parseJson(
            "interview_questions.json",
            Array<InterviewQuestion>::class.java
        ).toList()

        // Вставляем в одной транзакции для атомарности
        database.withTransaction {
            moduleDao.insertModules(modules)
            lessonDao.insertLessons(lessons)
            questionDao.insertQuestions(questions)
            interviewQuestionDao.insertQuestions(interviewQuestions)
        }
    }

    private fun <T> parseJson(fileName: String, clazz: Class<T>): T {
        val json = context.assets.open(fileName).bufferedReader().use { it.readText() }
        return gson.fromJson(json, clazz)
    }
}
