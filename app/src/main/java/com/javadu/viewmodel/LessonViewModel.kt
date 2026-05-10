package com.javadu.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    data class LessonState(
        val lesson: Lesson? = null,
        val questions: List<Question> = emptyList(),
        val user: User? = null,
        val currentQuestionIndex: Int = 0,
        val selectedAnswer: String? = null,
        val isAnswered: Boolean = false,
        val correctAnswersCount: Int = 0,
        val totalXp: Int = 0,
        val isCompleted: Boolean = false,
        val isLoading: Boolean = true,
        val showTheory: Boolean = true
    )

    private val _state = MutableStateFlow(LessonState())
    val state: StateFlow<LessonState> = _state

    fun loadLesson(lessonId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val lesson = repository.getLessonById(lessonId)
            val questions = repository.getQuestionsForLesson(lessonId).firstOrNull() ?: emptyList()
            val user = repository.currentUser.firstOrNull()

            _state.value = LessonState(
                lesson = lesson,
                questions = questions,
                user = user,
                isLoading = false
            )
        }
    }

    fun selectAnswer(answer: String) {
        val currentState = _state.value
        if (currentState.isAnswered) return

        val currentQuestion = currentState.questions.getOrNull(currentState.currentQuestionIndex)
            ?: return

        val isCorrect = answer == currentQuestion.correctAnswer
        val newCorrectCount = currentState.correctAnswersCount + if (isCorrect) 1 else 0
        val questionXp = if (isCorrect) 5 else 0
        val newTotalXp = currentState.totalXp + questionXp

        _state.value = currentState.copy(
            selectedAnswer = answer,
            isAnswered = true,
            correctAnswersCount = newCorrectCount,
            totalXp = newTotalXp
        )
    }

    fun nextQuestion() {
        val currentState = _state.value
        val nextIndex = currentState.currentQuestionIndex + 1

        if (nextIndex >= currentState.questions.size) {
            // Урок завершён
            val bonusXp = 10
            val finalXp = currentState.totalXp + bonusXp
            _state.value = currentState.copy(
                isCompleted = true,
                totalXp = finalXp
            )
        } else {
            _state.value = currentState.copy(
                currentQuestionIndex = nextIndex,
                selectedAnswer = null,
                isAnswered = false
            )
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
            }
            onComplete()
        }
    }
}
