package com.javadu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javadu.data.DatabaseInitializer
import com.javadu.data.database.entities.InterviewQuestion
import com.javadu.data.database.entities.Module
import com.javadu.data.database.entities.ModuleProgress
import com.javadu.data.database.entities.User
import com.javadu.data.repository.LessonRepository
import com.javadu.utils.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: LessonRepository,
    private val sharedPrefs: SharedPrefs,
    private val databaseInitializer: DatabaseInitializer
) : ViewModel() {

    data class HomeState(
        val user: User? = null,
        val modules: List<Module> = emptyList(),
        val moduleProgress: Map<Long, ModuleProgress> = emptyMap(),
        val randomQuestion: InterviewQuestion? = null,
        val isLoading: Boolean = true,
        val todayXp: Int = 0,
        val dailyGoal: Int = 50
    )

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    init {
        viewModelScope.launch {
            _state.value = _state.value.copy(todayXp = sharedPrefs.getTodayXp())
            databaseInitializer.initialize()
            loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val dailyGoal = sharedPrefs.dailyGoal

            combine(
                repository.currentUser,
                repository.allModules,
                repository.getAllModuleProgress()
            ) { user, modules, allProgress ->
                Triple(user, modules, allProgress)
            }.collect { (user, modules, allProgress) ->
                _state.value = _state.value.copy(
                    user = user,
                    modules = modules,
                    moduleProgress = allProgress.associateBy { it.moduleId },
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

    fun loadRandomQuestion() {
        viewModelScope.launch {
            val question = repository.getRandomInterviewQuestion()
            _state.value = _state.value.copy(randomQuestion = question)
        }
    }

    fun getModuleProgressPercentage(moduleId: Long, totalLessons: Int): Float {
        if (totalLessons <= 0) return 0f
        val completed = state.value.moduleProgress[moduleId]?.completedLessons ?: 0
        return (completed.toFloat() / totalLessons).coerceIn(0f, 1f)
    }
}
