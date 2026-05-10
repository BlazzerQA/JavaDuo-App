package com.javadu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javadu.data.database.entities.Lesson
import com.javadu.data.database.entities.Module
import com.javadu.data.database.entities.ModuleProgress
import com.javadu.data.database.entities.User
import com.javadu.data.database.entities.UserProgress
import com.javadu.data.repository.LessonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModuleLessonsViewModel @Inject constructor(
    private val repository: LessonRepository
) : ViewModel() {

    data class ModuleLessonsState(
        val module: Module? = null,
        val lessons: List<Lesson> = emptyList(),
        val user: User? = null,
        val progress: List<UserProgress> = emptyList(),
        val moduleProgress: ModuleProgress? = null,
        val isLoading: Boolean = true
    )

    private val _state = MutableStateFlow(ModuleLessonsState())
    val state: StateFlow<ModuleLessonsState> = _state

    fun loadModuleData(moduleId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val module = repository.getModuleById(moduleId)
            val user = repository.currentUser.firstOrNull()

            combine(
                repository.getLessonsByModule(moduleId),
                if (user != null) repository.getUserProgress(user.id) else kotlinx.coroutines.flow.flowOf(emptyList()),
                repository.getModuleProgress(moduleId)
            ) { lessons, progress, moduleProgress ->
                lessons to progress to moduleProgress
            }.collect { (lessonsAndProgress, moduleProgress) ->
                val (lessons, progress) = lessonsAndProgress
                _state.value = ModuleLessonsState(
                    module = module,
                    lessons = lessons,
                    user = user,
                    progress = progress,
                    moduleProgress = moduleProgress,
                    isLoading = false
                )
            }
        }
    }

    fun isLessonCompleted(lessonId: Long): Boolean {
        return _state.value.progress.find { it.lessonId == lessonId }?.isCompleted == true
    }

    fun isLessonUnlocked(lessonOrder: Int): Boolean {
        if (lessonOrder == 0) return true
        val prevLesson = _state.value.lessons.find { it.order == lessonOrder - 1 }
        if (prevLesson == null) return true // первый урок модуля — открываем
        val prevProgress = _state.value.progress.find { it.lessonId == prevLesson.id }
        return prevProgress?.isCompleted == true
    }
}
