package com.javadu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javadu.data.repository.LessonRepository
import com.javadu.utils.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: LessonRepository,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    fun addCoins(amount: Int) {
        viewModelScope.launch {
            val user = repository.currentUser.firstOrNull()
            user?.let {
                repository.addCoins(it.id, amount)
            }
        }
    }

    fun addDiamonds(amount: Int) {
        viewModelScope.launch {
            val user = repository.currentUser.firstOrNull()
            user?.let {
                repository.addDiamonds(it.id, amount)
            }
        }
    }

    fun addXp(amount: Int) {
        viewModelScope.launch {
            val user = repository.currentUser.firstOrNull()
            user?.let {
                repository.addXp(it.id, amount)
            }
        }
    }

    fun resetProgress() {
        viewModelScope.launch {
            repository.resetAllProgress()
            sharedPrefs.resetTodayXp()
        }
    }

    fun resetAllData(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.resetAllData()
            sharedPrefs.resetAll()
            onComplete()
        }
    }
}
