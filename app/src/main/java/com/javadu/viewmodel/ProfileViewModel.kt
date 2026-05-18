package com.javadu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javadu.data.database.entities.LevelInfo
import com.javadu.data.database.entities.User
import com.javadu.data.repository.LessonRepository
import com.javadu.utils.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: LessonRepository,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    data class ProfileState(
        val user: User? = null,
        val completedLessons: Int = 0,
        val levelInfo: LevelInfo? = null,
        val isLoading: Boolean = true,
        val showAvatarPicker: Boolean = false
    )

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.currentUser.collect { user ->
                if (user != null) {
                    val completed = repository.getCompletedLessonsCount(user.id)
                    val level = repository.getLevelInfo(user.totalXp)
                    _state.value = _state.value.copy(
                        user = user,
                        completedLessons = completed,
                        levelInfo = level,
                        isLoading = false
                    )
                } else {
                    _state.value = _state.value.copy(isLoading = false)
                }
            }
        }
    }

    fun refreshLevel() {
        viewModelScope.launch {
            val user = state.value.user ?: return@launch
            val level = repository.getLevelInfo(user.totalXp)
            _state.value = _state.value.copy(levelInfo = level)
        }
    }

    fun resetProgress() {
        viewModelScope.launch {
            repository.resetAllProgress()
            sharedPrefs.resetTodayXp()
            loadData()
        }
    }

    fun updateAvatarUri(avatarUri: String?) {
        viewModelScope.launch {
            val userId = state.value.user?.id ?: return@launch
            repository.updateAvatarUri(userId, avatarUri)
            if (!avatarUri.isNullOrBlank()) {
                repository.updateAvatarIcon(userId, null)
            }
            _state.value = _state.value.copy(showAvatarPicker = false)
        }
    }

    fun updateAvatarIcon(avatarIcon: String?) {
        viewModelScope.launch {
            val userId = state.value.user?.id ?: return@launch
            repository.updateAvatarIcon(userId, avatarIcon)
            if (!avatarIcon.isNullOrBlank()) {
                repository.updateAvatarUri(userId, null)
            }
            _state.value = _state.value.copy(showAvatarPicker = false)
        }
    }

    fun showAvatarPicker() {
        _state.value = _state.value.copy(showAvatarPicker = true)
    }

    fun dismissAvatarPicker() {
        _state.value = _state.value.copy(showAvatarPicker = false)
    }
}
