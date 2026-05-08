package com.javadu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javadu.data.repository.LessonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LessonRepository
) : ViewModel() {

    fun registerUser(name: String, email: String?, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.createUser(name, email)
            onComplete()
        }
    }

    fun loginAsGuest(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.createGuestUser()
            onComplete()
        }
    }
}
