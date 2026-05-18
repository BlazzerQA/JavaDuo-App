package com.javadu.data

import android.content.Context
import androidx.room.withTransaction
import com.google.gson.Gson
import com.javadu.data.database.AppDatabase
import com.javadu.data.database.dao.InterviewQuestionDao
import com.javadu.data.database.dao.LessonDao
import com.javadu.data.database.dao.ModuleDao
import com.javadu.data.database.dao.QuestionDao
import com.javadu.data.database.dao.UnitDao
import com.javadu.data.database.entities.InterviewQuestion
import com.javadu.data.database.entities.Lesson
import com.javadu.data.database.entities.Module
import com.javadu.data.database.entities.Question
import com.javadu.data.database.entities.Unit
import com.javadu.data.database.entities.UserUnit
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
    private val unitDao: UnitDao,
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
        val units = getUnits()

        // Вставляем в одной транзакции для атомарности
        database.withTransaction {
            moduleDao.insertModules(modules)
            lessonDao.insertLessons(lessons)
            questionDao.insertQuestions(questions)
            interviewQuestionDao.insertQuestions(interviewQuestions)
            unitDao.insertUnits(units)
        }
    }

    private fun <T> parseJson(fileName: String, clazz: Class<T>): T {
        val json = context.assets.open(fileName).bufferedReader().use { it.readText() }
        return gson.fromJson(json, clazz)
    }

    fun getUnits(): List<Unit> = listOf(
        Unit(
            id = "unit_java",
            name = "Java-воин",
            icon = "☕",
            moduleId = 1,
            baseAttack = 10,
            baseDefense = 5,
            baseHp = 30,
            hireCost = 100,
            description = "Мастер объектно-ориентированного программирования"
        ),
        Unit(
            id = "unit_bug",
            name = "Баг-сквайр",
            icon = "🐞",
            moduleId = 2,
            baseAttack = 8,
            baseDefense = 7,
            baseHp = 28,
            hireCost = 100,
            description = "Юный воин, обучающийся искусству отладки"
        ),
        Unit(
            id = "unit_api",
            name = "API-лучник",
            icon = "🌐",
            moduleId = 3,
            baseAttack = 12,
            baseDefense = 4,
            baseHp = 25,
            hireCost = 150,
            description = "Метко попадаете в endpoints"
        ),
        Unit(
            id = "unit_ui",
            name = "Selenide-шпион",
            icon = "📱",
            moduleId = 4,
            baseAttack = 14,
            baseDefense = 6,
            baseHp = 28,
            hireCost = 150,
            description = "Мастер автоматизации UI-тестирования"
        ),
        Unit(
            id = "unit_sql",
            name = "SQL-маг",
            icon = "🗄️",
            moduleId = 5,
            baseAttack = 15,
            baseDefense = 3,
            baseHp = 22,
            hireCost = 200,
            description = "Колдует над базами данных"
        ),
        Unit(
            id = "unit_interview",
            name = "Интервью-рыцарь",
            icon = "🎓",
            moduleId = 6,
            baseAttack = 18,
            baseDefense = 8,
            baseHp = 35,
            hireCost = 300,
            description = "Элитный воин, готовый к собеседованиям"
        )
    )
}
