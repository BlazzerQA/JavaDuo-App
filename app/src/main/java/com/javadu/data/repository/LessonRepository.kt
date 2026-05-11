package com.javadu.data.repository

import com.javadu.data.database.dao.InterviewQuestionDao
import com.javadu.data.database.dao.LessonDao
import com.javadu.data.database.dao.ModuleDao
import com.javadu.data.database.dao.ModuleProgressDao
import com.javadu.data.database.dao.QuestionDao
import com.javadu.data.database.dao.UserBonusDao
import com.javadu.data.database.dao.UserDao
import com.javadu.data.database.dao.UserProgressDao
import com.javadu.data.database.entities.BonusType
import com.javadu.data.database.entities.InterviewQuestion
import com.javadu.data.database.entities.Lesson
import com.javadu.data.database.entities.Module
import com.javadu.data.database.entities.ModuleProgress
import com.javadu.data.database.entities.Question
import com.javadu.data.database.entities.User
import com.javadu.data.database.entities.UserBonus
import com.javadu.data.database.entities.UserProgress
import com.javadu.data.database.entities.LevelInfo
import com.javadu.data.database.entities.LevelSystem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LessonRepository @Inject constructor(
    private val userDao: UserDao,
    private val lessonDao: LessonDao,
    private val questionDao: QuestionDao,
    private val userProgressDao: UserProgressDao,
    private val moduleDao: ModuleDao,
    private val moduleProgressDao: ModuleProgressDao,
    private val interviewQuestionDao: InterviewQuestionDao,
    private val userBonusDao: UserBonusDao
) {
    val currentUser: Flow<User?> = userDao.getUser()
    val allLessons: Flow<List<Lesson>> = lessonDao.getAllLessons()
    val allModules: Flow<List<Module>> = moduleDao.getAllModules()
    val allInterviewQuestions: Flow<List<InterviewQuestion>> = interviewQuestionDao.getAllQuestions()

    fun getQuestionsForLesson(lessonId: Long): Flow<List<Question>> =
        questionDao.getQuestionsForLesson(lessonId)

    suspend fun getLessonById(id: Long): Lesson? = lessonDao.getLessonById(id)

    suspend fun getModuleById(id: Long): Module? = moduleDao.getModuleById(id)

    fun getLessonsByModule(moduleId: Long): Flow<List<Lesson>> =
        lessonDao.getLessonsByModule(moduleId)

    fun getModuleProgress(moduleId: Long): Flow<ModuleProgress?> =
        moduleProgressDao.getProgressByModuleId(moduleId)

    fun getAllModuleProgress(): Flow<List<ModuleProgress>> =
        moduleProgressDao.getAllProgress()

    suspend fun upsertModuleProgress(progress: ModuleProgress) {
        moduleProgressDao.insertOrUpdateProgress(progress)
    }

    suspend fun getRandomInterviewQuestion(): InterviewQuestion? =
        interviewQuestionDao.getRandomQuestion()

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

    suspend fun addCoins(userId: Long, coins: Int) {
        userDao.addCoins(userId, coins)
    }

    suspend fun spendCoins(userId: Long, coins: Int): Boolean {
        val affected = userDao.spendCoins(userId, coins)
        return affected > 0
    }

    suspend fun getUserCoins(userId: Long): Int {
        return userDao.getUserById(userId)?.coins ?: 0
    }

    suspend fun completeLesson(userId: Long, lessonId: Long, xpEarned: Int, moduleId: Long?) {
        val progress = UserProgress(
            userId = userId,
            lessonId = lessonId,
            isCompleted = true,
            xpEarned = xpEarned,
            completedAt = System.currentTimeMillis()
        )
        userProgressDao.insertProgress(progress)
        userDao.addXp(userId, xpEarned)

        // Обновляем прогресс модуля
        moduleId?.let { mid ->
            val currentModuleProgress = moduleProgressDao.getProgressByModuleId(mid).firstOrNull()
                ?: ModuleProgress(moduleId = mid, completedLessons = 0)
            val updatedProgress = currentModuleProgress.copy(
                completedLessons = currentModuleProgress.completedLessons + 1
            )
            moduleProgressDao.insertOrUpdateProgress(updatedProgress)
        }
    }

    fun getUserProgress(userId: Long): Flow<List<UserProgress>> =
        userProgressDao.getUserProgress(userId)

    fun getUserProgressSync(userId: Long): Flow<List<UserProgress>> =
        userProgressDao.getUserProgress(userId)

    suspend fun isLessonCompleted(userId: Long, lessonId: Long): Boolean {
        val progress = userProgressDao.getProgressForLesson(userId, lessonId)
        return progress?.isCompleted == true
    }

    suspend fun insertInitialData(
        lessons: List<Lesson>,
        questions: List<Question>,
        modules: List<Module> = emptyList(),
        interviewQuestions: List<InterviewQuestion> = emptyList()
    ) {
        lessonDao.insertLessons(lessons)
        questionDao.insertQuestions(questions)
        if (modules.isNotEmpty()) moduleDao.insertModules(modules)
        if (interviewQuestions.isNotEmpty()) interviewQuestionDao.insertQuestions(interviewQuestions)
    }

    suspend fun hasData(): Boolean = lessonDao.getCount() > 0

    suspend fun resetAllProgress() {
        userProgressDao.deleteAll()
        moduleProgressDao.deleteAll()
        userDao.getUser().firstOrNull()?.let { user ->
            userDao.updateUser(user.copy(totalXp = 0))
        }
    }

    suspend fun resetAllData() {
        userProgressDao.deleteAll()
        moduleProgressDao.deleteAll()
        questionDao.deleteAll()
        lessonDao.deleteAll()
        moduleDao.deleteAll()
        interviewQuestionDao.deleteAll()
        userDao.deleteAll()
    }

    suspend fun getCompletedLessonsCount(userId: Long): Int =
        userProgressDao.getCompletedLessonsCount(userId)

    suspend fun getTotalXp(userId: Long): Int {
        return userDao.getUserById(userId)?.totalXp ?: 0
    }

    fun getLevelInfo(totalXp: Int): LevelInfo = LevelSystem.getLevelInfo(totalXp)

    suspend fun getUserLevelInfo(userId: Long): LevelInfo {
        val totalXp = getTotalXp(userId)
        return getLevelInfo(totalXp)
    }

    // ========== Бонусы ==========

    fun getUserBonuses(userId: Long): kotlinx.coroutines.flow.Flow<List<UserBonus>> =
        userBonusDao.getUserBonuses(userId)

    suspend fun purchaseBonus(userId: Long, type: BonusType, price: Int): Boolean {
        val success = spendCoins(userId, price)
        if (!success) return false
        val existing = userBonusDao.getBonusByType(userId, type)
        if (existing != null) {
            userBonusDao.addBonusQuantity(userId, type, 1)
        } else {
            userBonusDao.insertBonus(UserBonus(userId = userId, bonusType = type, quantity = 1))
        }
        return true
    }

    suspend fun useBonus(userId: Long, type: BonusType): Boolean {
        val bonus = userBonusDao.getBonusByType(userId, type)
        if (bonus == null || bonus.quantity <= 0) return false
        userBonusDao.useBonus(userId, type)
        return true
    }

    suspend fun getBonusQuantity(userId: Long, type: BonusType): Int {
        return userBonusDao.getBonusByType(userId, type)?.quantity ?: 0
    }

    suspend fun updateAvatarUri(userId: Long, avatarUri: String?) {
        userDao.updateAvatarUri(userId, avatarUri)
    }

    suspend fun updateAvatarIcon(userId: Long, avatarIcon: String?) {
        userDao.updateAvatarIcon(userId, avatarIcon)
    }
}
