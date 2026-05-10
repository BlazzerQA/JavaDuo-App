package com.javadu.data.repository

import com.javadu.data.database.dao.LessonDao
import com.javadu.data.database.dao.QuestionDao
import com.javadu.data.database.dao.UserDao
import com.javadu.data.database.dao.UserProgressDao
import com.javadu.data.database.entities.Lesson
import com.javadu.data.database.entities.Question
import com.javadu.data.database.entities.User
import com.javadu.data.database.entities.UserProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LessonRepository @Inject constructor(
    private val userDao: UserDao,
    private val lessonDao: LessonDao,
    private val questionDao: QuestionDao,
    private val userProgressDao: UserProgressDao
) {
    val currentUser: Flow<User?> = userDao.getUser()
    val allLessons: Flow<List<Lesson>> = lessonDao.getAllLessons()

    fun getQuestionsForLesson(lessonId: Long): Flow<List<Question>> =
        questionDao.getQuestionsForLesson(lessonId)

    suspend fun getLessonById(id: Long): Lesson? = lessonDao.getLessonById(id)

    suspend fun createUser(name: String, email: String? = null): Long {
        val user = User(name = name, email = email, isGuest = false)
        return userDao.insertUser(user)
    }

    suspend fun createGuestUser(): Long {
        val user = User(name = "Guest", isGuest = true)
        return userDao.insertUser(user)
    }

    suspend fun addXp(userId: Long, xp: Int) {
        userDao.addXp(userId, xp)
    }

    suspend fun completeLesson(userId: Long, lessonId: Long, xpEarned: Int) {
        val progress = UserProgress(
            userId = userId,
            lessonId = lessonId,
            isCompleted = true,
            xpEarned = xpEarned,
            completedAt = System.currentTimeMillis()
        )
        userProgressDao.insertProgress(progress)
        userDao.addXp(userId, xpEarned)
    }

    fun getUserProgress(userId: Long): Flow<List<UserProgress>> =
        userProgressDao.getUserProgress(userId)

    fun getUserProgressSync(userId: Long): Flow<List<UserProgress>> =
        userProgressDao.getUserProgress(userId)

    suspend fun isLessonCompleted(userId: Long, lessonId: Long): Boolean {
        val progress = userProgressDao.getProgressForLesson(userId, lessonId)
        return progress?.isCompleted == true
    }

    suspend fun insertInitialData(lessons: List<Lesson>, questions: List<Question>) {
        lessonDao.insertLessons(lessons)
        questionDao.insertQuestions(questions)
    }

    suspend fun hasData(): Boolean = lessonDao.getCount() > 0

    suspend fun resetAllProgress() {
        userProgressDao.deleteAll()
        userDao.getUser().firstOrNull()?.let { user ->
            userDao.updateUser(user.copy(totalXp = 0))
        }
    }

    suspend fun resetAllData() {
        userProgressDao.deleteAll()
        userDao.deleteAll()
    }

    suspend fun getCompletedLessonsCount(userId: Long): Int =
        userProgressDao.getCompletedLessonsCount(userId)

    suspend fun getTotalXp(userId: Long): Int {
        return userDao.getUserById(userId)?.totalXp ?: 0
    }
}
