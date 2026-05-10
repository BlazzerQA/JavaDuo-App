package com.javadu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javadu.data.DataInitializer
import com.javadu.data.database.entities.Lesson
import com.javadu.data.database.entities.User
import com.javadu.data.database.entities.UserProgress
import com.javadu.data.repository.LessonRepository
import com.javadu.utils.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: LessonRepository,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    data class HomeState(
        val user: User? = null,
        val lessons: List<Lesson> = emptyList(),
        val progress: List<UserProgress> = emptyList(),
        val isLoading: Boolean = true,
        val todayXp: Int = 0,
        val dailyGoal: Int = 50
    )

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    init {
        viewModelScope.launch {
            _state.value = _state.value.copy(todayXp = sharedPrefs.getTodayXp())
            if (!repository.hasData()) {
                repository.insertInitialData(
                    DataInitializer.getInitialLessons(),
                    DataInitializer.getInitialQuestions()
                )
            }
            loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val dailyGoal = sharedPrefs.dailyGoal

            combine(
                repository.currentUser,
                repository.allLessons,
            ) { user, lessons ->
                user to lessons
            }.flatMapLatest { (user, lessons) ->
                val progressFlow: Flow<List<UserProgress>> = user?.id?.let {
                    repository.getUserProgress(it)
                } ?: flowOf(emptyList())

                progressFlow.map { progress ->
                    Triple(user, lessons, progress)
                }
            }.collect { (user, lessons, progress) ->
                _state.value = _state.value.copy(
                    user = user,
                    lessons = lessons,
                    progress = progress,
                    isLoading = false,
                    dailyGoal = dailyGoal
                )
            }
        }
    }

    fun refreshTodayXp() {
        _state.value = _state.value.copy(
            todayXp = sharedPrefs.getTodayXp()
        )
    }

    fun isLessonUnlocked(lessonOrder: Int): Boolean {
        return if (lessonOrder == 0) true
        else {
            val prevLesson = state.value.lessons.find { it.order == lessonOrder - 1 }
            val prevProgress = state.value.progress.find { it.lessonId == prevLesson?.id }
            prevProgress?.isCompleted == true
        }
    }

    fun isLessonCompleted(lessonId: Long): Boolean {
        return state.value.progress.find { it.lessonId == lessonId }?.isCompleted == true
    }
}
