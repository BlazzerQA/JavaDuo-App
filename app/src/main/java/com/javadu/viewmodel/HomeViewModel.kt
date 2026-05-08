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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

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
            val todayXp = sharedPrefs.getTodayXp()
            val dailyGoal = sharedPrefs.dailyGoal

            combine(
                repository.currentUser,
                repository.allLessons,
                repository.currentUser
            ) { user, lessons, _ ->
                val progress = user?.id?.let {
                    repository.getUserProgress(it).firstOrNull() ?: emptyList()
                } ?: emptyList()
                Triple(user, lessons, progress)
            }.collect { (user, lessons, progress) ->
                _state.value = HomeState(
                    user = user,
                    lessons = lessons,
                    progress = progress,
                    isLoading = false,
                    todayXp = todayXp,
                    dailyGoal = dailyGoal
                )
            }
        }
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
