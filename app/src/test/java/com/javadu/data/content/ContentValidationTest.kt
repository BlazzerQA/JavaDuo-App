package com.javadu.data.content

import com.google.gson.Gson
import com.javadu.data.database.entities.InterviewQuestion
import com.javadu.data.database.entities.Lesson
import com.javadu.data.database.entities.Module
import com.javadu.data.database.entities.Question
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Проверяет целостность учебного контента в app/src/main/assets.
 * Парсит JSON в те же классы и той же библиотекой (Gson), что и DatabaseInitializer.
 * Каждый тест собирает все нарушения сразу, чтобы в отчёте был полный список.
 */
class ContentValidationTest {

    private val gson = Gson()

    private val assetsDir: File = listOf(
        File("src/main/assets"),      // рабочая папка Gradle = модуль app
        File("app/src/main/assets")   // запуск из корня проекта
    ).firstOrNull { it.isDirectory }
        ?: error("Не найдена папка assets (рабочая папка: ${File(".").absolutePath})")

    private fun <T> load(name: String, clazz: Class<Array<T>>): List<T> =
        gson.fromJson(File(assetsDir, name).readText(), clazz).toList()

    private val modules by lazy { load("modules.json", Array<Module>::class.java) }
    private val lessons by lazy { load("lessons.json", Array<Lesson>::class.java) }
    private val questions by lazy { load("questions.json", Array<Question>::class.java) }
    private val interview by lazy {
        load("interview_questions.json", Array<InterviewQuestion>::class.java)
    }

    private fun assertNoErrors(what: String, errors: List<String>) {
        assertTrue("$what:\n" + errors.joinToString("\n"), errors.isEmpty())
    }

    private fun <T, K> duplicates(items: List<T>, key: (T) -> K): List<K> =
        items.groupingBy(key).eachCount().filterValues { it > 1 }.keys.toList()

    /** Все четыре JSON-файла контента не пустые. */
    @Test
    fun contentIsNotEmpty() {
        assertTrue(modules.isNotEmpty())
        assertTrue(lessons.isNotEmpty())
        assertTrue(questions.isNotEmpty())
        assertTrue(interview.isNotEmpty())
    }

    /** id модулей, id уроков и order модулей уникальны. */
    @Test
    fun moduleAndLessonIdsAreUnique() {
        assertNoErrors("Дубли id модулей", duplicates(modules) { it.id }.map { "module $it" })
        assertNoErrors("Дубли id уроков", duplicates(lessons) { it.id }.map { "lesson $it" })
        assertNoErrors("Дубли order модулей", duplicates(modules) { it.order }.map { "order $it" })
    }

    /** Каждый урок ссылается на существующий модуль. */
    @Test
    fun everyLessonReferencesExistingModule() {
        val moduleIds = modules.map { it.id }.toSet()
        assertNoErrors("Уроки с несуществующим moduleId",
            lessons.filter { it.moduleId !in moduleIds }
                .map { "урок ${it.id} '${it.title}' -> moduleId ${it.moduleId}" })
    }

    /** totalLessons модуля совпадает с реальным числом уроков. */
    @Test
    fun moduleTotalLessonsMatchesActualLessonCount() {
        val counts = lessons.groupingBy { it.moduleId }.eachCount()
        assertNoErrors("Расхождение totalLessons",
            modules.filter { it.totalLessons != (counts[it.id] ?: 0) }
                .map { "модуль ${it.id} '${it.title}': totalLessons=${it.totalLessons}, уроков=${counts[it.id] ?: 0}" })
    }

    /** Каждый вопрос ссылается на существующий урок. */
    @Test
    fun everyQuestionReferencesExistingLesson() {
        val lessonIds = lessons.map { it.id }.toSet()
        assertNoErrors("Вопросы с несуществующим lessonId",
            questions.filter { it.lessonId !in lessonIds }
                .map { "lessonId ${it.lessonId}: ${it.questionText}" })
    }

    /** У каждого урока есть хотя бы один вопрос. */
    @Test
    fun everyLessonHasQuestions() {
        val withQuestions = questions.map { it.lessonId }.toSet()
        assertNoErrors("Уроки без вопросов",
            lessons.filter { it.id !in withQuestions }.map { "урок ${it.id} '${it.title}'" })
    }

    /** Правильный ответ дословно совпадает с одним из вариантов. */
    @Test
    fun correctAnswerIsAmongOptions() {
        assertNoErrors("Правильный ответ не совпадает ни с одним вариантом",
            questions.filter { it.correctAnswer !in listOf(it.option1, it.option2, it.option3) }
                .map { "урок ${it.lessonId}: '${it.questionText}' -> '${it.correctAnswer}'" })
    }

    /** Варианты ответа не пустые и не повторяются. */
    @Test
    fun answerOptionsAreNotBlankAndUnique() {
        assertNoErrors("Пустые или повторяющиеся варианты",
            questions.filter {
                val options = listOf(it.option1, it.option2, it.option3)
                options.any { o -> o.isBlank() } || options.toSet().size != options.size
            }.map { "урок ${it.lessonId}: '${it.questionText}'" })
    }

    /** У вопросов собеседования заполнены категория, вопрос, ответ и сложность. */
    @Test
    fun interviewQuestionsAreFilled() {
        assertNoErrors("Некорректные вопросы собеседования",
            interview.filter {
                it.category.isBlank() || it.question.isBlank() || it.answer.isBlank() || it.difficulty < 1
            }.map { "'${it.question}'" })
    }
}
