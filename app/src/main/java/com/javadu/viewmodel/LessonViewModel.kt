package com.javadu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javadu.data.database.entities.BonusType
import com.javadu.data.database.entities.Lesson
import com.javadu.data.database.entities.Question
import com.javadu.data.database.entities.User
import com.javadu.data.repository.LessonRepository
import com.javadu.utils.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val repository: LessonRepository,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    data class BonusesState(
        val hintCount: Int = 0,
        val insuranceCount: Int = 0,
        val xpBoostCount: Int = 0,
        val xpBoostActive: Boolean = false,
        val usedHintThisQuestion: Boolean = false,
        val usedInsuranceThisQuestion: Boolean = false
    )

    data class LessonState(
        val lesson: Lesson? = null,
        val questions: List<Question> = emptyList(),
        val user: User? = null,
        val currentQuestionIndex: Int = 0,
        val selectedAnswer: String? = null,
        val isAnswered: Boolean = false,
        val correctAnswersCount: Int = 0,
        val totalXp: Int = 0,
        val earnedCoins: Int = 0,
        val isCompleted: Boolean = false,
        val isFailed: Boolean = false,
        val isLoading: Boolean = true,
        val showTheory: Boolean = true,
        val bonuses: BonusesState = BonusesState(),
        val revealedHint: String? = null,
        val userXpBeforeLesson: Int = 0,
        val isTransitioning: Boolean = false
    )

    private val _state = MutableStateFlow(LessonState())
    val state: StateFlow<LessonState> = _state

    fun loadLesson(lessonId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val lesson = repository.getLessonById(lessonId)
            val questions = repository.getQuestionsForLesson(lessonId).firstOrNull() ?: emptyList()
            val user = repository.currentUser.firstOrNull()

            val bonuses = if (user != null) {
                loadBonusesState(user.id)
            } else BonusesState()

            _state.value = LessonState(
                lesson = lesson,
                questions = questions,
                user = user,
                isLoading = false,
                bonuses = bonuses,
                userXpBeforeLesson = user?.totalXp ?: 0
            )
        }
    }

    private suspend fun loadBonusesState(userId: Long): BonusesState {
        val list = repository.getUserBonuses(userId).firstOrNull() ?: emptyList()
        val map = list.associate { it.bonusType to it.quantity }
        val xpBoostActive = sharedPrefs.isXpBoostActive()
        return BonusesState(
            hintCount = map[BonusType.HINT] ?: 0,
            insuranceCount = map[BonusType.INSURANCE] ?: 0,
            xpBoostCount = map[BonusType.XP_BOOST] ?: 0,
            xpBoostActive = xpBoostActive
        )
    }

    fun selectAnswer(answer: String) {
        val currentState = _state.value
        if (currentState.isAnswered) return

        val currentQuestion = currentState.questions.getOrNull(currentState.currentQuestionIndex)
            ?: return

        val isCorrect = answer == currentQuestion.correctAnswer
        val newCorrectCount = currentState.correctAnswersCount + if (isCorrect) 1 else 0

        // Страховка: если ошибка и есть активная страховка — считаем правильным
        val bonuses = currentState.bonuses
        val hasInsurance = bonuses.insuranceCount > 0 && !bonuses.usedInsuranceThisQuestion
        val effectiveCorrect = isCorrect || (hasInsurance && !isCorrect)

        val questionXp = when {
            effectiveCorrect && bonuses.xpBoostActive -> 10 // удвоенный XP
            effectiveCorrect -> 5
            else -> 0
        }

        val newTotalXp = currentState.totalXp + questionXp

        // Если использовали страховку
        var newBonuses = bonuses
        if (!isCorrect && hasInsurance) {
            viewModelScope.launch {
                currentState.user?.id?.let { repository.useBonus(it, BonusType.INSURANCE) }
            }
            newBonuses = bonuses.copy(
                insuranceCount = bonuses.insuranceCount - 1,
                usedInsuranceThisQuestion = true
            )
        }

        _state.value = currentState.copy(
            selectedAnswer = answer,
            isAnswered = true,
            correctAnswersCount = newCorrectCount,
            totalXp = newTotalXp,
            bonuses = newBonuses
        )
    }

    fun nextQuestion() {
        val currentState = _state.value
        val nextIndex = currentState.currentQuestionIndex + 1

        if (nextIndex >= currentState.questions.size) {
            val totalQuestions = currentState.questions.size
            val correctPercentage = if (totalQuestions > 0) {
                (currentState.correctAnswersCount * 100) / totalQuestions
            } else 0

            if (correctPercentage < 60) {
                _state.value = currentState.copy(
                    isFailed = true,
                    bonuses = currentState.bonuses.copy(
                        xpBoostActive = false,
                        usedHintThisQuestion = false,
                        usedInsuranceThisQuestion = false
                    )
                )
            } else {
                val bonusXp = 10
                var earnedXp = currentState.totalXp + bonusXp

                if (currentState.bonuses.xpBoostActive) {
                    earnedXp += 10
                }

                if (currentState.bonuses.xpBoostActive) {
                    sharedPrefs.clearXpBoost()
                }

                val earnedCoins = currentState.correctAnswersCount * 3 + 5

                _state.value = currentState.copy(
                    isCompleted = true,
                    totalXp = earnedXp,
                    earnedCoins = earnedCoins,
                    bonuses = currentState.bonuses.copy(
                        xpBoostActive = false,
                        usedHintThisQuestion = false,
                        usedInsuranceThisQuestion = false
                    ),
                    userXpBeforeLesson = currentState.user?.totalXp ?: 0
                )
            }
        } else {
            viewModelScope.launch {
                _state.value = currentState.copy(isTransitioning = true)
                kotlinx.coroutines.delay(200)
                _state.value = currentState.copy(
                    currentQuestionIndex = nextIndex,
                    selectedAnswer = null,
                    isAnswered = false,
                    revealedHint = null,
                    bonuses = currentState.bonuses.copy(
                        usedHintThisQuestion = false,
                        usedInsuranceThisQuestion = false
                    ),
                    isTransitioning = false
                )
            }
        }
    }

    fun startQuestions() {
        _state.value = _state.value.copy(showTheory = false)
    }

    fun finishLesson(onComplete: () -> Unit) {
        viewModelScope.launch {
            val currentState = _state.value
            val userId = currentState.user?.id
            val lessonId = currentState.lesson?.id
            if (lessonId == null) {
                onComplete()
                return@launch
            }
            val moduleId = currentState.lesson?.moduleId
            val xp = currentState.totalXp

            if (userId != null) {
                repository.completeLesson(userId, lessonId, xp, moduleId)
                sharedPrefs.addTodayXp(xp)
                repository.addCoins(userId, currentState.earnedCoins)
            }
            onComplete()
        }
    }

    // ========== Бонусы ==========

    fun useHint() {
        val currentState = _state.value
        if (currentState.isAnswered) return
        if (currentState.bonuses.usedHintThisQuestion) return
        if (currentState.bonuses.hintCount <= 0) return

        val currentQuestion = currentState.questions.getOrNull(currentState.currentQuestionIndex)
            ?: return

        viewModelScope.launch {
            currentState.user?.id?.let { repository.useBonus(it, BonusType.HINT) }
        }

        _state.value = currentState.copy(
            revealedHint = currentQuestion.correctAnswer,
            bonuses = currentState.bonuses.copy(
                hintCount = currentState.bonuses.hintCount - 1,
                usedHintThisQuestion = true
            )
        )
    }

    fun activateXpBoost() {
        val currentState = _state.value
        if (currentState.bonuses.xpBoostActive) return
        if (currentState.bonuses.xpBoostCount <= 0) return

        viewModelScope.launch {
            currentState.user?.id?.let { repository.useBonus(it, BonusType.XP_BOOST) }
        }

        sharedPrefs.activateXpBoost()
        _state.value = currentState.copy(
            bonuses = currentState.bonuses.copy(
                xpBoostCount = currentState.bonuses.xpBoostCount - 1,
                xpBoostActive = true
            )
        )
    }
}
