package com.javadu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javadu.data.repository.LessonRepository
import com.javadu.utils.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: LessonRepository,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    val isDarkTheme: Boolean
        get() = sharedPrefs.isDarkTheme

    fun setDarkTheme(isDark: Boolean) {
        sharedPrefs.isDarkTheme = isDark
    }

    fun resetAllData(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.resetAllData()
            sharedPrefs.resetAll()
            onComplete()
        }
    }
}
