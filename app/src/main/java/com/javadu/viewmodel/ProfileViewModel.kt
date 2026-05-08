package com.javadu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        val isLoading: Boolean = true
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
                    _state.value = ProfileState(
                        user = user,
                        completedLessons = completed,
                        isLoading = false
                    )
                } else {
                    _state.value = ProfileState(isLoading = false)
                }
            }
        }
    }

    fun resetProgress() {
        viewModelScope.launch {
            repository.resetAllProgress()
            loadData()
        }
    }
}
